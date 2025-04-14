package com.example.appfood.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appfood.Adapter.CartAdapter;
import com.example.appfood.Domain.Foods;
import com.example.appfood.Helper.ManagmentCart;
import com.example.appfood.databinding.ActivityCartBinding;

import java.util.ArrayList;

public class CartActivity extends BaseActivity {
    ActivityCartBinding binding;
    ManagmentCart managmentCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        managmentCart = new ManagmentCart(this);
        setVariable();
        calculaCart();
        initCart();
    }

    private void initCart() {
        if (managmentCart.getListCart().isEmpty()){
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        }else{
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollViewCart.setVisibility(View.VISIBLE);
        }
        binding.cartView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false));
        binding.cartView.setAdapter(new CartAdapter(managmentCart.getListCart(),
                managmentCart, () -> calculaCart()));
        binding.checkOutBtn.setOnClickListener(v -> createOrderOnFirebase(managmentCart.getListCart()));
    }

    private void createOrderOnFirebase(ArrayList<Foods> cartList) {
        if (cartList == null || cartList.isEmpty()) {
            // Nếu giỏ rỗng thì báo
            showToast("Your cart is empty!");
            return;
        }

        // Lưu đơn hàng lên Firebase
        String orderId = firebaseDatabase.getReference("Orders").push().getKey();
        if (orderId == null) {
            showToast("Error creating order. Try again.");
            return;
        }

        firebaseDatabase.getReference("Orders").child(orderId).setValue(cartList)
                .addOnSuccessListener(unused -> {
                    showToast("Order placed successfully!");
                    managmentCart.clearCart(cartList); // Xóa giỏ hàng sau khi order
                    reloadCart(cartList);
                })
                .addOnFailureListener(e -> {
                    showToast("Failed to place order: " + e.getMessage());
                });
    }

    private void reloadCart(ArrayList<Foods> list) {
        CartAdapter cartAdapter = new CartAdapter(list); // Tạo adapter mới rỗng
        binding.cartView.setAdapter(cartAdapter); // Gán lại adapter
        binding.totalTxt.setText("$0.0"); // Reset tổng tiền
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    private void calculaCart() {

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

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
    }
}