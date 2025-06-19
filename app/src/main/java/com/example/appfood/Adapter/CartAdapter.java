package com.example.appfood.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.appfood.Domain.Foods;
import com.example.appfood.Helper.ChangeNumberItemsListener;
import com.example.appfood.Helper.ManagmentCart;
import com.example.appfood.R;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.viewholder> {
    ArrayList<Foods> list;
    ManagmentCart managmentCart;
    ChangeNumberItemsListener changeNumberItemsListener;

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false));

    }

    public CartAdapter(ArrayList<Foods> list, ManagmentCart managmentCart, ChangeNumberItemsListener changeNumberItemsListener) {
        this.list = list;
        this.managmentCart = managmentCart;
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        Foods item = list.get(position);


        holder.title.setText(item.getTitle());
        double fee = item.getNumberInCart() * item.getPrice();
        holder.feeEachItem.setText( String.format("%.0f", fee) + " VND");
        holder.num.setText(item.getNumberInCart() + "");
        Glide.with(holder.itemView.getContext())
                .load(item.getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.pic);

        holder.plusItem.setOnClickListener(v -> {
            managmentCart.plusNumberItem(list, position, () -> {
                notifyDataSetChanged();
                changeNumberItemsListener.change();
            });
        });
        holder.minusItem.setOnClickListener(v -> {
            managmentCart.minusNumberItem(list, position, () -> {
                notifyDataSetChanged();
                changeNumberItemsListener.change();
            });
        });
        holder.trashBtn.setOnClickListener(v->{
            managmentCart.removeItem(list, position, () -> {
                notifyDataSetChanged();
                changeNumberItemsListener.change();
            });
        });


    }

    public CartAdapter(ArrayList<Foods> list) {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public CartAdapter() {
    }

    public class viewholder extends RecyclerView.ViewHolder {

        TextView title, feeEachItem, plusItem, minusItem;
        ImageView pic;
        TextView num;
        ConstraintLayout trashBtn;
        Button checkOutButton;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTxt);
            pic = itemView.findViewById(R.id.pic);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            plusItem = itemView.findViewById(R.id.plusCartBtn);
            minusItem = itemView.findViewById(R.id.minusCartBtn);
            num = itemView.findViewById(R.id.numberItemTxt);
            trashBtn = itemView.findViewById(R.id.trashBtn);
            checkOutButton = itemView.findViewById(R.id.checkOutBtn);
        }
    }
}
