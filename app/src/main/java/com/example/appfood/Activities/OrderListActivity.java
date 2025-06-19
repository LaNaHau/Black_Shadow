package com.example.appfood.Activities;

import static com.example.appfood.Utils.Utils.getDoubleValue;
import static com.example.appfood.Utils.Utils.getLongValue;

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

public class OrderListActivity extends BaseActivity {
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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            DatabaseReference ordersRef =
                    firebaseDatabase.getReference("orders").child(currentUserId);

            ordersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    orderList.clear();
                    Log.d(TAG, "Loading orders for user: " + currentUserId);

                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        try {
                            String orderId = orderSnapshot.child("orderId").getValue(String.class);
                            if (orderId == null) {
                                Long orderIdLong = orderSnapshot.child("orderId").getValue(Long.class);
                                orderId = orderIdLong != null ? orderIdLong.toString() : "";
                            }

                            String userId = currentUserId;
                            String userName = orderSnapshot.child("userName").getValue(String.class);
                            String phone = orderSnapshot.child("phone").getValue(String.class);
                            String address = orderSnapshot.child("address").getValue(String.class);
                            String status = orderSnapshot.child("status").getValue(String.class);

                            double itemTotal = getDoubleValue(orderSnapshot, "itemTotal");
                            double tax = getDoubleValue(orderSnapshot, "tax");
                            double deliveryFee = getDoubleValue(orderSnapshot, "deliveryFee");
                            double totalAmount = getDoubleValue(orderSnapshot, "totalAmount");
                            double finalTotal = getDoubleValue(orderSnapshot, "finalTotal");

                            long orderTime = getLongValue(orderSnapshot, "orderTime");
                            long paymentConfirmTime = getLongValue(orderSnapshot, "paymentConfirmTime");

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

                    // Sort by orderTime descending
                    Collections.sort(orderList, (o1, o2) ->
                            Long.compare(o2.getOrderTime(), o1.getOrderTime()));

                    if (binding.orderRecyclerView != null) {
                        orderAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Notified adapter with " + orderList.size() + " orders");
                    }

                    if (orderList.isEmpty()) {
                        Toast.makeText(OrderListActivity.this, "No orders available", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "No orders found for user");
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