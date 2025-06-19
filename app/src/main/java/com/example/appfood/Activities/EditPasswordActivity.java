package com.example.appfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.example.appfood.Domain.User;
import com.example.appfood.databinding.ActivityEditPasswordBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

public class EditPasswordActivity extends BaseActivity {
    ActivityEditPasswordBinding binding;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.savePasswordButton.setOnClickListener(v -> changePassword());
        binding.backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(EditPasswordActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void changePassword() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String currentPass = binding.currentPasswordEditText.getText().toString().trim();
            String newPass = binding.newPasswordEditText.getText().toString().trim();
            String confirmPass = binding.confirmPasswordEditText.getText().toString().trim();

            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Re-authenticate người dùng với mật khẩu hiện tại
            AuthCredential credential = EmailAuthProvider
                    .getCredential(currentUser.getEmail(), currentPass);

            currentUser.reauthenticate(credential)
                    .addOnSuccessListener(unused -> {
                        // Đổi mật khẩu sau khi xác thực thành công
                        currentUser.updatePassword(newPass)
                                .addOnSuccessListener(unused1 -> {
                                    Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EditPasswordActivity.this, ProfileActivity.class);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Lỗi khi đổi mật khẩu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
        }
    }
}

