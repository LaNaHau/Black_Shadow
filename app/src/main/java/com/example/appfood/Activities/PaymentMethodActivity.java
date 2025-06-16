package com.example.appfood.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appfood.Domain.Order;
import com.example.appfood.Domain.Voucher;
import com.example.appfood.R;
import com.example.appfood.Utils.CustomIdGeneratorUtils;
import com.example.appfood.Utils.QRCodeUtils;
import com.example.appfood.databinding.ActivityPaymentMethodBinding;
import com.google.zxing.WriterException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

public class PaymentMethodActivity extends BaseActivity {

    ActivityPaymentMethodBinding binding;
    CountDownTimer qrCountdownTimer;
    private static final long QR_VALID_DURATION = 5 * 60 * 1000; // 5 phút


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentMethodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setVariable();
    }

    private void setVariable() {
        Order order = (Order) getIntent().getSerializableExtra("order_data");
        Voucher discountVoucher = (Voucher) getIntent().getSerializableExtra("selected_discount_voucher");
        Voucher freeshipVoucher = (Voucher) getIntent().getSerializableExtra("selected_freeship_voucher");

        if (discountVoucher != null) {
            // Hiển thị: ví dụ
        }
        if (freeshipVoucher != null) {
        }

        if (order == null) {
            showToast("Không nhận được thông tin đơn hàng");
            finish();
            return;
        }

        order.recalculateFinalTotal();
        Log.e("Order", "" + order);

        binding.radioGroupPayment.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioQR) {
                binding.imgQR.setVisibility(View.VISIBLE);
                binding.btnConfirmQR.setVisibility(View.VISIBLE);
                generateAndDisplayOrderQR(order);
            } else {
                binding.imgQR.setVisibility(View.GONE);
                binding.btnConfirmQR.setVisibility(View.GONE);
                binding.tvQRCountdown.setVisibility(View.GONE);
            }
        });

        String customOrderId = CustomIdGeneratorUtils.generateUserId("ORDER_");
        order.setOrderId(customOrderId);

        binding.btnConfirm.setOnClickListener(v -> {
            if (binding.radioCOD.isChecked()) {
                order.setStatus("COD - Pending");
                saveOrderToFirebase(order, true);
                showToast("Đã chọn thanh toán khi nhận hàng");
            } else if (binding.radioQR.isChecked()) {
                if (isQRExpired(order)) {
                    showToast("Mã QR đã hết hạn, vui lòng tạo lại");
                } else {
                    showToast("Vui lòng quét mã QR để thanh toán");
                }
            } else {
                showToast("Vui lòng chọn phương thức thanh toán");
            }
        });

        binding.btnConfirmQR.setOnClickListener(v -> {
            if (isQRExpired(order)) {
                showToast("Mã QR đã hết hạn. Không thể xác nhận.");
                return;
            }

            order.setStatus("Paid via QR");
            order.setPaymentConfirmTime(System.currentTimeMillis());
            saveOrderToFirebase(order, true);
            showToast("Đã xác nhận thanh toán bằng QR");
        });
    }

    private void generateAndDisplayOrderQR(Order order) {
        if (order.getFinalTotal() <= 0) {
            showToast("Tổng tiền không hợp lệ");
            return;
        }

        if (order.getUserId() == null || order.getUserId().trim().isEmpty()) {
            showToast("Bạn cần đăng nhập để tạo QR");
            return;
        }

        if (!isQRExpired(order)) {
            showToast("QR đã được tạo và còn hiệu lực");
            return;
        }

        String qrContent = buildQRContent(order);

        try {
            Bitmap qrBitmap = QRCodeUtils.generateQRCode(qrContent, 500);
            binding.imgQR.setImageBitmap(qrBitmap);

            order.setStatus("Awaiting QR Payment");
            long expiryTime = System.currentTimeMillis() + QR_VALID_DURATION;
            order.setPaymentConfirmTime(expiryTime);

            startQRCountdown(QR_VALID_DURATION);

            Log.d("QR Content", qrContent);
        } catch (WriterException e) {
            e.printStackTrace();
            showToast("Lỗi tạo mã QR");
        }
    }

    private void startQRCountdown(long durationInMillis) {
        binding.tvQRCountdown.setVisibility(View.VISIBLE);

        if (qrCountdownTimer != null) qrCountdownTimer.cancel();

        qrCountdownTimer = new CountDownTimer(durationInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                String timeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                binding.tvQRCountdown.setText("Thời gian còn lại: " + timeLeft);
            }

            @Override
            public void onFinish() {
                binding.tvQRCountdown.setText("Mã QR đã hết hạn!");
                binding.tvQRCountdown.setVisibility(View.GONE);
                binding.imgQR.setImageDrawable(null);
                binding.btnConfirmQR.setVisibility(View.GONE);
                showToast("Mã QR đã hết hạn, vui lòng tạo lại");
            }
        }.start();
    }

    private boolean isQRExpired(Order order) {
        return order.getPaymentConfirmTime() == 0
                || System.currentTimeMillis() > order.getPaymentConfirmTime();
    }

    private void saveOrderToFirebase(Order order, boolean finishAfter) {
        firebaseDatabase.getReference("orders")
                .child(order.getOrderId())
                .setValue(order)
                .addOnSuccessListener(aVoid -> {
                    showToast("Đơn hàng đã được lưu!");
                    if (finishAfter) {
                        Intent intent = new Intent();
                        intent.putExtra("order_success", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .addOnFailureListener(e ->
                        showToast("Lỗi khi lưu đơn hàng: " + e.getMessage())
                );
    }

    private String buildQRContent(Order order) {
        String paymentNote = "Thanh toan " + order.getOrderId();
        if (paymentNote.length() > 100) paymentNote = paymentNote.substring(0, 100);

        return "Ngân hàng: MB Bank\n"
                + "Số TK: 123456789\n"
                + "Tên: Nguyen Van A\n"
                + String.format("Tổng tiền: %.2f", order.getFinalTotal()) + "\n"
                + "Nội dung: " + paymentNote;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (qrCountdownTimer != null) {
            qrCountdownTimer.cancel();
            qrCountdownTimer = null;
        }
    }
}

