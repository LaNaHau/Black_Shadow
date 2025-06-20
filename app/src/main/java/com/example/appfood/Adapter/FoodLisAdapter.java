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

import java.util.ArrayList;

public class FoodLisAdapter extends RecyclerView.Adapter<FoodLisAdapter.viewholeder> {
    private static final String TAG = "FoodLisAdapter";
    ArrayList<Foods> arrayList;
    Context context;

    public FoodLisAdapter(ArrayList<Foods> arrayList) {
        this.arrayList = arrayList;
    }


    @NonNull
    @Override
    public viewholeder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        Log.d(TAG, "onCreateViewHolder: Adapter created");
        return new viewholeder(LayoutInflater.from(context).inflate(R.layout.viewholder_list_food,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewholeder holder, int position) {
        try {
            Foods item = arrayList.get(position);

            holder.titleTxt.setText(item.getTitle());
            holder.timeTxt.setText(item.getTimeValue() + " min");
            holder.priceTxt.setText(Utils.formatCurrency(item.getPrice()));
            holder.rateTxt.setText(String.valueOf(item.getStar()));

            Log.d(TAG, "Binding item: " + item.getTitle() + " | Price: " + item.getPrice());

            Glide.with(context)
                    .load(item.getImagePath())
                    .transform(new CenterCrop(), new RoundedCorners(50))
                    .into(holder.pic);

            holder.itemView.setOnClickListener(v -> {
                try {

                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("object", item);
                    context.startActivity(intent);

                    Log.d(TAG, "Clicked on item: " + item.getTitle());
                } catch (Exception e) {
                    Log.e(TAG, "Error starting DetailActivity", e);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error binding data at position: " + position, e);
        }

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "Item count: " + arrayList.size());
        return arrayList.size();
    }


    public class viewholeder extends RecyclerView.ViewHolder {
        TextView titleTxt, priceTxt, rateTxt, timeTxt;
        ImageView pic;
        public viewholeder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            rateTxt = itemView.findViewById(R.id.ratingTxt);
            timeTxt = itemView.findViewById(R.id.timeTxt);
            pic = itemView.findViewById(R.id.img);
        }
    }
}
