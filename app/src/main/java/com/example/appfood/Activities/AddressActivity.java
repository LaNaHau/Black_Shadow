package com.example.appfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appfood.Adapter.AddressAdapter;
import com.example.appfood.Domain.Address;
import com.example.appfood.databinding.ActivityAddressBinding;
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
    private List<Address> addressList = new ArrayList<>();
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new AddressAdapter(addressList, selectedAddress -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_address", selectedAddress);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
        binding.addressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.addressRecyclerView.setAdapter(adapter);

        String userId = mAuth.getCurrentUser().getUid();
        databaseRef = firebaseDatabase.getReference("addresses").child(userId);

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addressList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Address address = data.getValue(Address.class);
                    if (address != null) {
                        address.setId(data.getKey());
                        addressList.add(address);
                    }
                }
                Log.d("DEBUG", "Tổng số địa chỉ load được: " + addressList.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddressActivity.this, "Lỗi tải địa chỉ", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnAddAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddAddressActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }
}
