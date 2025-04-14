package com.example.appfood.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.appfood.Domain.SliderItem;
import com.example.appfood.R;

import java.util.ArrayList;

public class SliderViewHolderAdapter extends RecyclerView.Adapter<SliderViewHolderAdapter.ViewHolder> {
    private ArrayList<SliderItem> arrayList;
    private Context context;
    private ViewPager2 viewPager2;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };


    public SliderViewHolderAdapter(ViewPager2 viewPager2, ArrayList<SliderItem> arrayList) {
        this.viewPager2 = viewPager2;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.slider_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.transform(new CenterCrop(), new RoundedCorners(60));

        Glide.with(context).load(arrayList.get(position).getImage())
                .apply(requestOptions)
                .into(holder.imageView);


        if (position == arrayList.size() - 2){
            viewPager2.post(runnable);
        }
        viewPager2.postDelayed(runnable, 3000); // Slide sau 3 giây
         for (SliderItem a : arrayList) {
             Log.d("banner", a.toString());

         }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgSlider);
        }
    }
}
