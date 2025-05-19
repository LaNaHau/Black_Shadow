package com.example.appfood.Activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appfood.Domain.Address;
import com.example.appfood.R;
import com.example.appfood.databinding.ActivityAddAddressBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddAddressActivity extends AppCompatActivity {

    private ActivityAddAddressBinding binding;
    private DatabaseReference databaseRef;
    private String addressId = null; // nếu có thì sửa, không thì thêm mới

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseRef = FirebaseDatabase.getInstance().getReference("addresses");

        // Nếu nhận được intent sửa
        Address address = (Address) getIntent().getSerializableExtra("address");
        if (address != null) {
            addressId = address.getId(); // id dùng để cập nhật
            binding.edtName.setText(address.getName());
            binding.edtPhone.setText(address.getPhone());
            binding.edtAddress.setText(address.getFullAddress());
        }

        binding.btnSaveAddress.setOnClickListener(v -> {
            String name = binding.edtName.getText().toString().trim();
            String phone = binding.edtPhone.getText().toString().trim();
            String fullAddress = binding.edtAddress.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || fullAddress.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (addressId == null) {
                // Thêm mới
                String id = databaseRef.push().getKey();
                Address newAddress = new Address(id, name, phone, fullAddress, false);
                databaseRef.child(id).setValue(newAddress);
            } else {
                // Cập nhật
                Address updated = new Address(addressId, name, phone, fullAddress, address.isDefault());
                databaseRef.child(addressId).setValue(updated);
            }

            finish(); // Quay lại
        });
    }
}