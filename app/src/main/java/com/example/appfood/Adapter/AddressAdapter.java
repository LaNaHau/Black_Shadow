package com.example.appfood.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfood.Activities.AddAddressActivity;
import com.example.appfood.Domain.Address;
import com.example.appfood.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    private List<Address> addressList;
    public AddressAdapter(List<Address> addressList) {
        this.addressList = addressList;
    }
    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.ViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.tvNamePhone.setText(address.getName() + " | " + address.getPhone());
        holder.tvFullAddress.setText(address.getFullAddress());
        holder.tvDefaultLabel.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);

        // Nhấn để sửa địa chỉ
        holder.tvEdit.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AddAddressActivity.class);
            intent.putExtra("address", address);  // Đảm bảo Address implements Serializable
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamePhone, tvFullAddress, tvDefaultLabel, tvEdit;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNamePhone = itemView.findViewById(R.id.tvNamePhone);
            tvFullAddress = itemView.findViewById(R.id.tvFullAddress);
            tvDefaultLabel = itemView.findViewById(R.id.tvDefaultLabel);
            tvEdit = itemView.findViewById(R.id.tvEdit);
        }
    }
}
