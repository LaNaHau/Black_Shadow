package com.example.appfood.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appfood.Adapter.CartAdapter;
import com.example.appfood.Domain.Address;
import com.example.appfood.Domain.Foods;
import com.example.appfood.Domain.Order;
import com.example.appfood.Domain.User;
import com.example.appfood.Domain.Voucher;
import com.example.appfood.Helper.ManagmentCart;
import com.example.appfood.databinding.ActivityCartBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

// import giữ nguyên như cũ

public class CartActivity extends BaseActivity {
    ActivityCartBinding binding;
    ManagmentCart managmentCart;
    private static final int REQUEST_ADDRESS = 1;
    private static final int REQUEST_PAYMENT = 102;
    private static final int REQUEST_VOUCHER = 103;

    private Voucher selectedDiscountVoucher = null;
    private Voucher selectedFreeshipVoucher = null;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        initCart();
        setVariable();
        calculateCart();


    }

    private void initCart() {
        loadUserInfo();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        managmentCart = new ManagmentCart(context, currentUser.getUid());
        binding.cartView.setAdapter(new CartAdapter(managmentCart.getListCart(), managmentCart, this::calculateCart));



        if (managmentCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.lin.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.lin.setVisibility(View.VISIBLE);

            CartAdapter adapter = new CartAdapter(managmentCart.getListCart(), managmentCart, this::calculateCart);
            binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            binding.cartView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        Log.d("DEBUG", "Cart size: " + managmentCart.getListCart().size());

    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());

        binding.checkOutBtn.setOnClickListener(v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                showToast("Bạn chưa đăng nhập!");
                return;
            }

            String userId = currentUser.getUid();
            String orderId = firebaseDatabase.getReference("Orders").push().getKey();
            if (orderId == null) {
                showToast("Không thể tạo đơn hàng.");
                return;
            }

            String name = binding.nameTxt.getText().toString();
            String phone = binding.phoneTxt.getText().toString();
            String address = binding.addTxt.getText().toString();

            double itemTotal = managmentCart.getTotalFee();
            double discount = 0;
            double delivery = 10;
            double tax = 0;

            if (selectedDiscountVoucher != null) {
                if ("percent".equalsIgnoreCase(selectedDiscountVoucher.getDiscountType())) {
                    discount = itemTotal * (selectedDiscountVoucher.getValue() / 100.0);
                } else if ("amount".equalsIgnoreCase(selectedDiscountVoucher.getDiscountType())) {
                    discount = selectedDiscountVoucher.getValue();
                }
            }

            itemTotal = Math.max(0, itemTotal - discount);
            if (selectedFreeshipVoucher != null) delivery = 0;
            tax = itemTotal * 0.02;
            double total = itemTotal + tax + delivery;

            Order order = new Order(orderId, userId, name, phone, address,
                    managmentCart.getListCart(), itemTotal, tax, delivery, total, System.currentTimeMillis());

            Intent intent = new Intent(CartActivity.this, PaymentMethodActivity.class);
            intent.putExtra("order_data", order);
            intent.putExtra("selected_discount_voucher", selectedDiscountVoucher);
            intent.putExtra("selected_freeship_voucher", selectedFreeshipVoucher);
            startActivityForResult(intent, REQUEST_PAYMENT);
        });

        binding.addressBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, AddressActivity.class);
            startActivityForResult(intent, REQUEST_ADDRESS);
        });

        binding.VoucherBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, VoucherActivity.class);
            startActivityForResult(intent, REQUEST_VOUCHER);
        });
    }

    private void calculateCart() {
        double itemTotal = managmentCart.getTotalFee();
        double delivery = 10;
        double discountAmount = 0;

        if (selectedDiscountVoucher != null) {
            if ("percent".equalsIgnoreCase(selectedDiscountVoucher.getDiscountType())) {
                discountAmount = itemTotal * (selectedDiscountVoucher.getValue() / 100.0);
            } else if ("amount".equalsIgnoreCase(selectedDiscountVoucher.getDiscountType())) {
                discountAmount = selectedDiscountVoucher.getValue();
            }
        }

        itemTotal = Math.max(0, itemTotal - discountAmount);

        if (selectedFreeshipVoucher != null) delivery = 0;
        double tax = itemTotal * 0.02;
        double total = itemTotal + tax + delivery;
        // Debug log
        Log.d("DEBUG_CART", "Subtotal: " + itemTotal);
        Log.d("DEBUG_CART", "Discount: " + discountAmount);
        Log.d("DEBUG_CART", "Tax: " + tax);
        Log.d("DEBUG_CART", "Delivery: " + delivery);
        Log.d("DEBUG_CART", "Total: " + total);

        binding.totalFeeTxt.setText("$" + String.format("%.2f", itemTotal));
        binding.taxTxt.setText("$" + String.format("%.2f", tax));
        binding.deliveryTxt.setText("- $" + String.format("%.2f", delivery));
        binding.totalTxt.setText("$" + String.format("%.2f", total));

        // Cập nhật UI voucher
        String voucherText = "";
        if (selectedDiscountVoucher != null)
            voucherText += "Giảm: " + selectedDiscountVoucher.getCode();
        if (selectedFreeshipVoucher != null)
            voucherText += (voucherText.isEmpty() ? "" : " | ") + "Freeship";

        binding.VoucherTxt.setText(voucherText.isEmpty() ? "Chọn mã giảm giá" : voucherText);
    }

    private void loadUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            showToast("Bạn chưa đăng nhập!");
            return;
        }

        DatabaseReference userRef = firebaseDatabase.getReference("Users").child(currentUser.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    binding.nameTxt.setText(user.getUserName());
                    binding.phoneTxt.setText(user.getPhone());
                    binding.addTxt.setText(user.getAddress());
                } else {
                    showToast("Không tìm thấy người dùng");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Lỗi: " + error.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADDRESS && resultCode == RESULT_OK && data != null) {
            Address selected = (Address) data.getSerializableExtra("selected_address");
            if (selected != null) {
                binding.phoneTxt.setText(selected.getPhone());
                binding.addTxt.setText(selected.getFullAddress());
                binding.nameTxt.setText(selected.getName());
            }
        }

        if (requestCode == REQUEST_VOUCHER && resultCode == RESULT_OK && data != null) {
            selectedDiscountVoucher = (Voucher) data.getSerializableExtra("selected_discount_voucher");
            selectedFreeshipVoucher = (Voucher) data.getSerializableExtra("selected_freeship_voucher");
            calculateCart();
        }

        if (requestCode == REQUEST_PAYMENT && resultCode == RESULT_OK && data != null) {
            boolean success = data.getBooleanExtra("order_success", false);
            if (success) {
                managmentCart.clearCart();
                initCart();
                calculateCart();
            }
        }
    }
}