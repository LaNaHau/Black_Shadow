package com.example.appfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.appfood.Domain.User;
import com.example.appfood.R;
import com.example.appfood.databinding.ActivityEditProfileBinding;
import com.example.appfood.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends BaseActivity {

    ActivityEditProfileBinding binding;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadUserInfo();
        binding.saveButton.setOnClickListener(v -> saveUserInfo());
    }
    private void loadUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        String avatarUrl = snapshot.child("avatar").getValue(String.class);


                        if (user != null) {
                            String userName = user.getUserName() != null && !user.getUserName().isEmpty()
                                    ? user.getUserName() : "Nhập tên";
                            String phone = user.getPhone() != null && !user.getPhone().isEmpty()
                                    ? user.getPhone() : "Nhập số điện thoại";
                            String address = user.getAddress() != null && !user.getAddress().toString().isEmpty()
                                    ? user.getAddress().toString() : "Nhập địa chỉ";

                            binding.nameTextView.setText(user.getUserName());
                            binding.emailTextView.setText(user.getEmail());
                            binding.nameEditText.setText(userName);
                            binding.phoneEditText.setText(phone);
                            binding.addressEditText.setText(address);

                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                Glide.with(EditProfileActivity.this)
                                        .load(avatarUrl)
                                        .into(binding.avatarImageView);
                            }
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Người dùng không tồn tại trong Database", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EditProfileActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            // Lấy dữ liệu từ các EditText
            String name = binding.nameEditText.getText().toString().trim();
            String phone = binding.phoneEditText.getText().toString().trim();
            String address = binding.addressEditText.getText().toString().trim();

            // Có thể thêm check dữ liệu tại đây nếu muốn
            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật dữ liệu vào Firebase
            Map<String, Object> updates = new HashMap<>();
            updates.put("userName", name);
            updates.put("phone", phone);
            updates.put("address", address);

            userRef.updateChildren(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfileActivity.this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
        }
    }

}
