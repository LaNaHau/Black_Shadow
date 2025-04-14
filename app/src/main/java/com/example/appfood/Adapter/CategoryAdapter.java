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
import com.example.appfood.Activities.ListFoodActivity;
import com.example.appfood.Domain.Category;
import com.example.appfood.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewholder> {
    ArrayList<Category> arrayList;
    Context context;

    public CategoryAdapter() {
    }

    public CategoryAdapter(ArrayList<Category> arrayList) {
        this.arrayList = arrayList;

    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflateView = LayoutInflater.from(context).inflate(R.layout.viewholder_category, parent, false);

        return new viewholder(inflateView);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.titleTxt.setText(arrayList.get(position).getName());
        String imagePath = arrayList.get(position).getImagePath();
        Glide.with(context)
                .load(imagePath)
                .into(holder.pic);
        for (Category food : arrayList) {
            Log.d("FoodItem", food.toString());
        }


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ListFoodActivity.class);
            intent.putExtra("CategoryId", arrayList.get(position).getId());
            intent.putExtra("CategoryName", arrayList.get(position).getName());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return arrayList != null ? arrayList.size() : 0;
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        ImageView pic;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.cartNameTxt);
            pic = itemView.findViewById(R.id.imgCart);
        }
    }
}
