package com.example.appfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appfood.Adapter.CartAdapter;
import com.example.appfood.Domain.Address;
import com.example.appfood.Domain.Foods;
import com.example.appfood.Domain.Order;
import com.example.appfood.Domain.User;
import com.example.appfood.Helper.ManagmentCart;
import com.example.appfood.databinding.ActivityCartBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class CartActivity extends BaseActivity {
    ActivityCartBinding binding;
    ManagmentCart managmentCart;
    private static final int REQUEST_ADDRESS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        managmentCart = new ManagmentCart(this);
        setVariable();
        calculateCart();
        initCart();
    }

    private void initCart() {
        loadUserInfo();
        if (managmentCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollViewCart.setVisibility(View.VISIBLE);
        }
        binding.cartView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false));
        binding.cartView.setAdapter(new CartAdapter(managmentCart.getListCart(),
                managmentCart, () -> calculateCart()));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADDRESS && resultCode == RESULT_OK) {
            Address selected = (Address) data.getSerializableExtra("selected_address");


            // Hoặc tải lại dữ liệu giỏ hàng nếu từ server
            if (selected != null) {
                // Cập nhật giao diện hoặc dùng địa chỉ này
                binding.phoneTxt.setText(selected.getPhone());
                binding.addTxt.setText(selected.getFullAddress());
                binding.nameTxt.setText(selected.getName());
            }
            loadCartData(); // reload lại cart sau khi chọn địa chỉ
        }

        // Xử lý sau khi thanh toán xong
        if (resultCode == RESULT_OK && data != null && data.getBooleanExtra("order_success", false)) {
            managmentCart.clearCart();  // Xoá dữ liệu cart
            initCart();  // Tải lại giỏ hàng
            calculateCart();  // Cập nhật lại tổng tiền
        }
    }

    private void calculateCart() {
        double percentTax = 0.02; //percent 2% tax
        double delivery = 10; //10 Dollar
        double tax = Math.round(managmentCart.getTotalFee() * percentTax * 100.0) / 100;
        double total = Math.round((managmentCart.getTotalFee() + tax + delivery));
        double itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100;

        binding.toalFeeTxt.setText("$" + itemTotal);
        binding.taxTxt.setText("$" + tax);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);
    }

//    private void createOrderOnFirebase(ArrayList<Foods> cartList) {
//        if (cartList == null || cartList.isEmpty()) {
//            showToast("Your cart is empty!");
//            return;
//        }
//
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser == null) {
//            showToast("Bạn chưa đăng nhập!");
//            return;
//        }
//
//        String userId = currentUser.getUid();
//        String orderId = firebaseDatabase.getReference("Orders").push().getKey();
//
//        if (orderId == null) {
//            showToast("Error creating order. Try again.");
//            return;
//        }
//
//        // Lấy thông tin người dùng và địa chỉ hiển thị trên giao diện
//        String userName = binding.nameTxt.getText().toString();
//        String phone = binding.phoneTxt.getText().toString();
//        String address = binding.addTxt.getText().toString();
//
//        // Tính toán tiền
//        double percentTax = 0.02;
//        double delivery = 10;
//        double itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100.0;
//        double tax = Math.round(itemTotal * percentTax * 100.0) / 100.0;
//        double total = Math.round((itemTotal + tax + delivery) * 100.0) / 100.0;
//
//        long orderTime = System.currentTimeMillis();
//
//        Order order = new Order(orderId, userId, userName, phone, address,
//                cartList, itemTotal, tax, delivery, total, orderTime);
//
//        firebaseDatabase.getReference("Orders").child(orderId).setValue(order)
//                .addOnSuccessListener(unused -> {
//                    showToast("Order placed successfully!");
//                    managmentCart.clearCart(cartList);
//                    reloadCart(new ArrayList<>());
//                })
//                .addOnFailureListener(e -> {
//                    showToast("Failed to place order: " + e.getMessage());
//                });
//    }

    private void loadCartData() {
        initCart();
        calculateCart();
    }
    private void loadUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = firebaseDatabase.getReference("Users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            binding.nameTxt.setText(user.getUserName());
                            binding.phoneTxt.setText(user.getPhone());
                            binding.addTxt.setText(user.getAddress());
                        } else {
                            Toast.makeText(CartActivity.this, "Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CartActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }

    private void reloadCart(ArrayList<Foods> list) {
        CartAdapter cartAdapter = new CartAdapter(list); // Tạo adapter mới rỗng
        binding.cartView.setAdapter(cartAdapter); // Gán lại adapter
        binding.totalTxt.setText("$0.0"); // Reset tổng tiền
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

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
                showToast("Lỗi khi tạo đơn hàng");
                return;
            }

            String userName = binding.nameTxt.getText().toString();
            String phone = binding.phoneTxt.getText().toString();
            String address = binding.addTxt.getText().toString();

            double percentTax = 0.02;
            double delivery = 10;
            double itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100.0;
            double tax = Math.round(itemTotal * percentTax * 100.0) / 100.0;
            double total = Math.round((itemTotal + tax + delivery) * 100.0) / 100.0;
            long orderTime = System.currentTimeMillis();

            Order order = new Order(orderId, userId, userName, phone, address,
                    managmentCart.getListCart(), itemTotal, tax, delivery, total, orderTime);

            Intent intent = new Intent(CartActivity.this, PaymentMethodActivity.class);
            intent.putExtra("order_data", order);  // Truyền order sang PaymentMethodActivity
            startActivity(intent);
        });


        binding.addressBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, AddressActivity.class);
            startActivityForResult(intent, REQUEST_ADDRESS);

        });
    }
}