package com.example.appfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.appfood.Utils.Utils;
import com.example.appfood.databinding.ActivityLoginBinding;

public class LoginActivity extends BaseActivity {
    ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();

    }

    private void setVariable() {
        binding.signUpButton.setOnClickListener(c -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        binding.goButton.setOnClickListener(v -> {
            String email = binding.editEmail.getText().toString().trim();
            String pass = binding.editPassword.getText().toString().trim();

            boolean isEmailValid = Utils.inputValidation(binding.editEmail);
            boolean isPasswordValid = Utils.inputValidation(binding.editPassword);

            if (isEmailValid && isPasswordValid) {
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Đăng nhập thành công
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish(); // Đóng LoginActivity
                            } else {
                                // Đăng nhập thất bại
                                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại. Vui lòng kiểm tra lại tài khoản hoặc mật khẩu.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // Gán lỗi cho từng ô nếu cần thiết
                if (!isEmailValid) {
                    binding.editEmail.setError("Email đăng nhập không hợp lệ");
                }
                if (!isPasswordValid) {
                    binding.editPassword.setError("Mật khẩu không hợp lệ");
                }
            }
        });
    }

}