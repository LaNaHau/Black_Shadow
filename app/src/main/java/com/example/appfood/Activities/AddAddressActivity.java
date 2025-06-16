package com.example.appfood.Activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appfood.Domain.Address;
import com.example.appfood.databinding.ActivityAddAddressBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddAddressActivity extends BaseActivity {

    private ActivityAddAddressBinding binding;
    private DatabaseReference userAddressRef;
    private String addressId = null;
    private String userId = null;
    private boolean isDefault = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = getIntent().getStringExtra("userId");
        userAddressRef = firebaseDatabase.getReference("addresses").child(userId);

        Address address = (Address) getIntent().getSerializableExtra("address");
        if (address != null) {
            addressId = address.getId();
            isDefault = address.isDefault();
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
                addressId = userAddressRef.push().getKey();
            }

            Address newAddress = new Address(addressId, name, phone, fullAddress, isDefault);
            userAddressRef.child(addressId).setValue(newAddress);

            Toast.makeText(this, "Lưu địa chỉ thành công", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
