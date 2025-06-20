package com.example.appfood.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.appfood.Activities.DetailActivity;
import com.example.appfood.Domain.Foods;
import com.example.appfood.R;
import com.example.appfood.Utils.Utils;

import java.util.List;

public class FoodOrderListAdapter extends RecyclerView.Adapter<FoodOrderListAdapter.FoodViewHolder> {

    private List<Foods> foodList;
    private Context context;

    public FoodOrderListAdapter(List<Foods> foodList, Context context) {
        this.foodList = foodList;
        this.context = context;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_in_order, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Foods food = foodList.get(position);
        holder.foodTitle.setText(food.getTitle());
        holder.foodPrice.setText(Utils.formatCurrency(food.getPrice()));
        holder.foodStar.setText("⭐ " + food.getStar());

        Glide.with(context)
                .load(food.getImagePath())
                .placeholder(R.drawable.history_order)
                .error(R.drawable.history_order)
                .into(holder.foodImage);

        holder.itemView.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("object", food);
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e("FoodOrderListAdapter", "Error opening detail: " + e.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodTitle, foodPrice, foodStar, foodTime;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodTitle = itemView.findViewById(R.id.foodTitle);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodStar = itemView.findViewById(R.id.foodStar);
        }
    }
}
