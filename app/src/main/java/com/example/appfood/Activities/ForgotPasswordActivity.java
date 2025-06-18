package com.example.appfood.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appfood.databinding.ActivityForgotPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.resetBtn.setOnClickListener(v -> {
            String email = binding.emailInput.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                binding.emailInput.setError("Vui lòng nhập email");
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (email.equals("test0999@gmail.com") || email.equals("user099@gmail.com")){
                            new androidx.appcompat.app.AlertDialog.Builder(ForgotPasswordActivity.this)
                                    .setTitle("Gửi thất bại")
                                    .setMessage("Email không hợp lệ.\nVui lòng nhập lại!!!")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        dialog.dismiss();
                                        finish();
                                    })
                                    .show();
                        } else {
                            if (task.isSuccessful()) {
                                new androidx.appcompat.app.AlertDialog.Builder(ForgotPasswordActivity.this)
                                        .setTitle("Gửi thành công")
                                        .setMessage("Đường dẫn khôi phục mật khẩu đã được gửi tới email của bạn.\nVui lòng kiểm tra hộp thư đến hoặc spam.")
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            dialog.dismiss();
                                            finish();
                                        })
                                        .show();
                            } else {
                                Toast.makeText(this, "Không gửi được: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        });

        binding.backToLogin.setOnClickListener(v -> finish());
    }
}
