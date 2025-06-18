package com.example.appfood.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.appfood.R;
import com.example.appfood.Utils.Utils;
import com.example.appfood.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Lấy từ google-services.json
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Lấy thông tin đã lưu nếu có
        SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        String savedEmail = prefs.getString("email", "");
        String savedPass = prefs.getString("password", "");

        if (!savedEmail.isEmpty() && !savedPass.isEmpty()) {
            binding.editEmail.setText(savedEmail);
            binding.editPassword.setText(savedPass);
            binding.checkboxRemember.setChecked(true);
        }

        setVariable();
    }

    private void setVariable() {
        // Chuyển đến RegisterActivity khi nhấn "Đăng ký"
        binding.signUpButton.setOnClickListener(c ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        // Đăng nhập Email + Mật khẩu
        binding.goButton.setOnClickListener(v -> {
            String email = binding.editEmail.getText().toString().trim();
            String pass = binding.editPassword.getText().toString().trim();

            boolean isEmailValid = Utils.inputValidation(binding.editEmail);
            boolean isPasswordValid = Utils.inputValidation(binding.editPassword);

            if (isEmailValid && isPasswordValid) {
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Nếu người dùng tích ô "Lưu tài khoản", thì lưu email/pass
                                if (binding.checkboxRemember.isChecked()) {
                                    SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("email", email);
                                    editor.putString("password", pass);
                                    editor.apply();
                                } else {
                                    // Xoá thông tin cũ nếu không muốn lưu
                                    getSharedPreferences("login_prefs", MODE_PRIVATE).edit().clear().apply();
                                }

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "Đăng nhập thất bại. Vui lòng kiểm tra lại tài khoản hoặc mật khẩu.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                if (!isEmailValid) {
                    binding.editEmail.setError("Mail đăng nhập không hợp lệ");
                }
                if (!isPasswordValid) {
                    binding.editPassword.setError("Mật khẩu không hợp lệ");
                }
            }
        });

        // Đăng nhập bằng Google
        binding.btnGoogleSignIn.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        // quen mat khau
        binding.forgotPasswordText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Nếu tích ô lưu tài khoản, lưu email (không cần lưu pass cho Google)
                        if (binding.checkboxRemember.isChecked()) {
                            SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("email", user.getEmail());
                            editor.putString("password", ""); // Không cần pass Google
                            editor.apply();
                        } else {
                            getSharedPreferences("login_prefs", MODE_PRIVATE).edit().clear().apply();
                        }

                        Toast.makeText(LoginActivity.this,
                                "Chào mừng, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Xác thực với Google thất bại.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
