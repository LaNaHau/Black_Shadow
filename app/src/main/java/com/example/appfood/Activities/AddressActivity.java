package com.example.appfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfood.Adapter.AddressAdapter;
import com.example.appfood.Domain.Address;
import com.example.appfood.R;
import com.example.appfood.databinding.ActivityAddressBinding;
import com.example.appfood.databinding.ActivityCartBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends BaseActivity {
    private ActivityAddressBinding binding;
    private AddressAdapter adapter;
    private List<Address> addressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo danh sách và adapter
        addressList = new ArrayList<>();

        // Thiết lập RecyclerView
        adapter = new AddressAdapter(addressList);
        binding.addressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.addressRecyclerView.setAdapter(adapter);


        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference addressRef = FirebaseDatabase.getInstance()
                .getReference("addresses")
                .child(userId);


        // Nút thêm địa chỉ


        // Lắng nghe thay đổi dữ liệu theo thời gian thực
        addressRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addressList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Address address = data.getValue(Address.class);
                    if (address != null) {
                        address.setId(data.getKey()); // Lưu ID để chỉnh sửa
                        addressList.add(address);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddressActivity.this, "Lỗi tải địa chỉ", Toast.LENGTH_SHORT).show();
            }
        });
        // Mở màn hình thêm địa chỉ
        binding.btnAddAddress.setOnClickListener(v -> {
            startActivity(new Intent(this, AddAddressActivity.class));
        });
    }
}