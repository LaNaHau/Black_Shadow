package com.example.appfood.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfood.Activities.AddAddressActivity;
import com.example.appfood.Domain.Address;
import com.example.appfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    public interface OnAddressSelectedListener {
        void onAddressSelected(Address selectedAddress);
    }

    private List<Address> addressList;
    private OnAddressSelectedListener listener;

    public AddressAdapter(List<Address> addressList, OnAddressSelectedListener listener) {
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.ViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.tvNamePhone.setText(address.getName() + " | " + address.getPhone());
        holder.tvFullAddress.setText(address.getFullAddress());
        holder.tvDefaultLabel.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);

        holder.tvEdit.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AddAddressActivity.class);
            intent.putExtra("address", address);
            v.getContext().startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("addresses").child(userId);

            for (Address add : addressList) {
                ref.child(add.getId()).child("default").setValue(false);
            }

            ref.child(address.getId()).child("default").setValue(true);

            if (listener != null) {
                listener.onAddressSelected(address);
            }

            Toast.makeText(v.getContext(), "Đã chọn địa chỉ làm mặc định", Toast.LENGTH_SHORT).show();
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
