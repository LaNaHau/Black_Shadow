package com.example.appfood.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appfood.Domain.Foods;
import com.example.appfood.Domain.Order;
import com.example.appfood.R;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private static final String TAG = "OrderAdapter";
    private Context context;
    private List<Order> orderList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Set data to TextViews
        holder.orderIdTxt.setText("Order Code: " + order.getOrderId());
        holder.orderTimeTxt.setText("Order Date: " + dateFormat.format(new Date(order.getOrderTime())));
        holder.foodListTxt.setText("Items: " + getFoodListString(order.getFoodList()));
        holder.finalTotalTxt.setText("Total: " + String.format("%.0f", order.getFinalTotal()) + " VND");
        holder.addressTxt.setText("Address: " + (order.getAddress() != null ? order.getAddress() : "Not available"));

        // Set status and color
        String status = order.getStatus() != null ? order.getStatus() : "Undefined";
        holder.statusTxt.setText("Status: " + status);
        if ("Canceled".equalsIgnoreCase(status)) {
            holder.statusTxt.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else if ("Completed".equalsIgnoreCase(status)) {
            holder.statusTxt.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else if ("Pending".equalsIgnoreCase(status)) {
            holder.statusTxt.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        } else {
            holder.statusTxt.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        // Set image from the first valid food item in foodList
        if (order.getFoodList() != null && !order.getFoodList().isEmpty()) {
            Foods selectedFood = null;
            for (Foods food : order.getFoodList()) {
                if (food != null && food.getImagePath() != null && !food.getImagePath().isEmpty()) {
                    selectedFood = food;
                    break; // Lấy mục đầu tiên có ImagePath hợp lệ
                }
            }

            if (selectedFood != null) {
                Log.d(TAG, "Order: " + order.getOrderId() + ", Selected Food: " + selectedFood.getTitle() +
                        ", ImagePath: " + (selectedFood.getImagePath() != null ? selectedFood.getImagePath() : "null"));
                try {
                    if (selectedFood.getImagePath() != null && !selectedFood.getImagePath().isEmpty()) {
                        Glide.with(context)
                                .load(selectedFood.getImagePath())
                                .placeholder(R.drawable.history_order) // Sử dụng placeholder bạn đã chỉ định
                                .error(R.drawable.history_order)       // Sử dụng error bạn đã chỉ định
                                .into(holder.foodImage);
                        Log.d(TAG, "Loading image from URL: " + selectedFood.getImagePath());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading image for order " + order.getOrderId() + ": " + e.getMessage());
                    holder.foodImage.setImageResource(R.drawable.history_order); // Sử dụng history_oder làm mặc định
                }
            } else {
                holder.foodImage.setImageResource(R.drawable.history_order); // Mặc định nếu không có ImagePath
                Log.d(TAG, "No valid image found, using default image for order: " + order.getOrderId());
            }
        } else {
            holder.foodImage.setImageResource(R.drawable.history_order); // Mặc định nếu foodList rỗng
            Log.d(TAG, "No food items, using default image for order: " + order.getOrderId());
        }

        // Manage button visibility based on status and time
        long currentTime = System.currentTimeMillis();
        long orderTime = order.getOrderTime();
        long timeDiffMinutes = (currentTime - orderTime) / (1000 * 60); // Convert to minutes

        holder.cancelButton.setVisibility(View.GONE);
        holder.receivedButton.setVisibility(View.GONE);

        if (timeDiffMinutes < 1) {
            if ("COD - Pending".equalsIgnoreCase(status) || ("Paid via QR".equalsIgnoreCase(status) )) {
                holder.cancelButton.setVisibility(View.VISIBLE);
            }
        } else if ("COD - Pending".equalsIgnoreCase(status)) {
            holder.receivedButton.setVisibility(View.VISIBLE);
        }

        // Handle Cancel button click to update status
        holder.cancelButton.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("orders")
                    .child(order.getOrderId())
                    .child("status")
                    .setValue("Canceled")
                    .addOnSuccessListener(aVoid -> {
                        order.setStatus("Canceled");
                        holder.statusTxt.setText("Status: Canceled");
                        holder.statusTxt.setTextColor(ContextCompat.getColor(context, R.color.red));
                        holder.cancelButton.setVisibility(View.GONE);
                        holder.receivedButton.setVisibility(View.GONE);
                        Toast.makeText(context, "Order " + order.getOrderId() + " has been canceled", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error canceling order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Handle Received button click to update status
        holder.receivedButton.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("orders")
                    .child(order.getOrderId())
                    .child("status")
                    .setValue("Completed")
                    .addOnSuccessListener(aVoid -> {
                        order.setStatus("Completed");
                        holder.statusTxt.setText("Status: Completed");
                        holder.statusTxt.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                        holder.cancelButton.setVisibility(View.GONE);
                        holder.receivedButton.setVisibility(View.GONE);
                        Toast.makeText(context, "Order " + order.getOrderId() + " marked as completed", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error marking order as completed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView orderIdTxt, orderTimeTxt, foodListTxt, finalTotalTxt, statusTxt, addressTxt;
        Button cancelButton, receivedButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            orderIdTxt = itemView.findViewById(R.id.orderIdTxt);
            orderTimeTxt = itemView.findViewById(R.id.orderTimeTxt);
            foodListTxt = itemView.findViewById(R.id.foodListTxt);
            finalTotalTxt = itemView.findViewById(R.id.finalTotalTxt);
            statusTxt = itemView.findViewById(R.id.statusTxt);
            addressTxt = itemView.findViewById(R.id.addressTxt);
            cancelButton = itemView.findViewById(R.id.cancelButton);
            receivedButton = itemView.findViewById(R.id.receivedButton);
        }
    }

    private String getFoodListString(List<Foods> foodList) {
        if (foodList == null || foodList.isEmpty()) return "No items";
        StringBuilder sb = new StringBuilder();
        for (Foods food : foodList) {
            sb.append(food.getTitle()).append(" (x").append(food.getNumberInCart())
                    .append(", ").append(String.format("%.0f", food.getPrice()))
                    .append(" VND), ");
        }
        return sb.substring(0, sb.length() - 2); // Remove trailing comma
    }
}