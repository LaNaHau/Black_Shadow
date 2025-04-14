package com.example.appfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.appfood.Utils.SystemUtils;
import com.example.appfood.Utils.Utils;
import com.example.appfood.databinding.ActivityRegisterBinding;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends BaseActivity {
    ActivityRegisterBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();
    }

    private void setVariable() {
        binding.alreadyHaveAccount.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );

        binding.signUpButton.setOnClickListener(v -> {
            String email = binding.editEmailId.getText().toString().trim();
            String userName = binding.editUsername.getText().toString().trim();
            String pass = binding.editPassword.getText().toString().trim();
            String phone = binding.editPhoneNumber.getText().toString().trim();
            String fullName = binding.editFullname.getText().toString().trim();

            SystemUtils.hideKeyBoard(this);

            // Validate inputs
            if (!Utils.inputValidation(binding.editFullname)) {
                binding.editFullname.setError("Please enter your Fullname");
                return;
            }
            if (!Utils.inputValidation(binding.editUsername)) {
                binding.editUsername.setError("Please enter your username");
                return;
            }
            if (!Utils.inputValidation(binding.editEmailId)) {
                binding.editEmailId.setError("Please enter your email id");
                return;
            } else if (!Utils.isValidEmail(binding.editEmailId)) {
                binding.editEmailId.setError("Please enter a valid email");
                return;
            }
            if (!Utils.inputValidation(binding.editPhoneNumber)) {
                binding.editPhoneNumber.setError("Please enter your phone number");
                return;
            } else if (!Utils.isValidPhone(binding.editPhoneNumber)) {
                binding.editPhoneNumber.setError("Please enter a valid 10-digit phone number");
                return;
            }
            if (!Utils.inputValidation(binding.editPassword)) {
                binding.editPassword.setError("Please enter your password");
                return;
            } else if (!Utils.isValidPassword(binding.editPassword)) {
                binding.editPassword.setError("Password must be at least 6 characters");
                return;
            }

            // Tạo tài khoản Firebase
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Lấy user ID
                            String userId = mAuth.getCurrentUser().getUid();

                            // Tạo đối tượng người dùng
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("fullName", fullName);
                            userMap.put("userName", userName);
                            userMap.put("email", email);
                            userMap.put("phone", phone);
                            userMap.put("uid", userId);

                            // Ghi vào Realtime Database
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userId)
                                    .setValue(userMap)
                                    .addOnCompleteListener(saveTask -> {
                                        if (saveTask.isSuccessful()) {
                                            Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Failed to save user data: " + saveTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            Toast.makeText(this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("Error", task.getException().getMessage());
                        }
                    });
        });
    }

}