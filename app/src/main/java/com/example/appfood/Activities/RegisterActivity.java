package com.example.appfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.appfood.Domain.User;
import com.example.appfood.Domain.Voucher;
import com.example.appfood.Utils.SystemUtils;
import com.example.appfood.Utils.Utils;
import com.example.appfood.databinding.ActivityRegisterBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends BaseActivity {
    ActivityRegisterBinding binding;
    private DatabaseReference databaseRef;


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

            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();

                            User user = new User(userName, fullName, userId, email, phone);



                            saveUserToDatabase(user);
                        } else {
                            Toast.makeText(this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("AuthError", task.getException().getMessage());
                        }
                    });
        });
    }
    private void saveUserToDatabase(User user) {
        firebaseDatabase.getReference("Users")
                .child(user.getUserId())
                .setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Lỗi lưu thông tin người dùng: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


}