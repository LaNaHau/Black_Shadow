package com.example.appfood.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appfood.Adapter.OrderAdapter;
import com.example.appfood.Domain.Foods;
import com.example.appfood.Domain.Order;
import com.example.appfood.databinding.ActivityOrderListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {
    private static final String TAG = "OrderListActivity";
    private ActivityOrderListBinding binding;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check binding
        if (binding == null) {
            Log.e(TAG, "Binding is null!");
            return;
        }

        // Initialize RecyclerView
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);
        if (binding.orderRecyclerView != null) {
            binding.orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.orderRecyclerView.setAdapter(orderAdapter);
            Log.d(TAG, "RecyclerView initialized successfully");
        } else {
            Log.e(TAG, "orderRecyclerView is null!");
        }

        // Add back button to navigate back to ProfileActivity
        if (binding.backBtn != null) {
            binding.backBtn.setOnClickListener(v -> {
                finish(); // Quay lại ProfileActivity hiện có trong back stack
            });
            Log.d(TAG, "Back button initialized successfully");
        } else {
            Log.e(TAG, "backBtn is null!");
        }

        // Load order history
        loadOrderHistory();
    }

    private void loadOrderHistory() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");

            ordersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    orderList.clear();
                    Log.d(TAG, "Loading orders from Firebase");
                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        try {
                            String orderId = orderSnapshot.child("orderId").getValue(String.class);
                            if (orderId == null) {
                                Long orderIdLong = orderSnapshot.child("orderId").getValue(Long.class);
                                orderId = orderIdLong != null ? orderIdLong.toString() : "";
                            }

                            String userId = orderSnapshot.child("userId").getValue(String.class);
                            if (userId == null) {
                                Long userIdLong = orderSnapshot.child("userId").getValue(Long.class);
                                userId = userIdLong != null ? userIdLong.toString() : "";
                            }

                            String userName = orderSnapshot.child("userName").getValue(String.class);
                            String phone = orderSnapshot.child("phone").getValue(String.class);
                            String address = orderSnapshot.child("address").getValue(String.class);
                            String status = orderSnapshot.child("status").getValue(String.class);
                            double itemTotal = orderSnapshot.child("itemTotal").getValue(Double.class) != null ? orderSnapshot.child("itemTotal").getValue(Double.class) : 0.0;
                            double tax = orderSnapshot.child("tax").getValue(Double.class) != null ? orderSnapshot.child("tax").getValue(Double.class) : 0.0;
                            double deliveryFee = orderSnapshot.child("deliveryFee").getValue(Double.class) != null ? orderSnapshot.child("deliveryFee").getValue(Double.class) : 0.0;
                            double totalAmount = orderSnapshot.child("totalAmount").getValue(Double.class) != null ? orderSnapshot.child("totalAmount").getValue(Double.class) : 0.0;
                            double finalTotal = orderSnapshot.child("finalTotal").getValue(Double.class) != null ? orderSnapshot.child("finalTotal").getValue(Double.class) : 0.0;

                            Long orderTimeLong = orderSnapshot.child("orderTime").getValue(Long.class);
                            long orderTime = orderTimeLong != null ? orderTimeLong : 0L;

                            Long paymentConfirmTimeLong = orderSnapshot.child("paymentConfirmTime").getValue(Long.class);
                            long paymentConfirmTime = paymentConfirmTimeLong != null ? paymentConfirmTimeLong : 0L;

                            List<Foods> foodList = new ArrayList<>();
                            DataSnapshot foodListSnapshot = orderSnapshot.child("foodList");
                            for (DataSnapshot foodSnapshot : foodListSnapshot.getChildren()) {
                                Foods food = foodSnapshot.getValue(Foods.class);
                                if (food != null) {
                                    foodList.add(food);
                                }
                            }

                            Order order = new Order(orderId, userId, userName, phone, address, foodList,
                                    itemTotal, tax, deliveryFee, totalAmount, orderTime, status,
                                    paymentConfirmTime, finalTotal);
                            orderList.add(order);
                            Log.d(TAG, "Loaded order: " + orderId + ", Status: " + status);
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing order: " + orderSnapshot.getKey(), e);
                        }
                    }

                    // Sort list by orderTime descending (newest first)
                    Collections.sort(orderList, new Comparator<Order>() {
                        @Override
                        public int compare(Order o1, Order o2) {
                            return Long.compare(o2.getOrderTime(), o1.getOrderTime());
                        }
                    });

                    if (binding.orderRecyclerView != null) {
                        orderAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Notified adapter with " + orderList.size() + " orders");
                    }
                    if (orderList.isEmpty()) {
                        Toast.makeText(OrderListActivity.this, "No orders available", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "No orders found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(OrderListActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Firebase query cancelled: " + error.getMessage());
                }
            });
        } else {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            Log.d(TAG, "User not logged in, finishing activity");
        }
    }
}