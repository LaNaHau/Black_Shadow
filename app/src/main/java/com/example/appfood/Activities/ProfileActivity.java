package com.example.appfood.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.example.appfood.Domain.User;
import com.example.appfood.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileActivity extends BaseActivity {

    ActivityProfileBinding binding;
    User user;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadUserInfo();
        setAvailable();
        setupDarkModeSwitch(); // ✅ xử lý chuyển đổi dark mode
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
                            String address = user.getAddress() != null && !user.getAddress().toString().isEmpty()
                                    ? user.getAddress().toString() : "Chưa cập nhật";
                            binding.passText.setText("********");
                            binding.nameTxt.setText(user.getUserName());
                            binding.emailTxt.setText(user.getEmail());
                            binding.tellTxt.setText(user.getPhone());
                            binding.mobileTxt.setText(user.getPhone());
                            binding.addressTxt.setText(address);

                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                Glide.with(ProfileActivity.this)
                                        .load(avatarUrl)
                                        .into(binding.avaBtn);
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "Người dùng không tồn tại trong Database", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAvailable() {
        binding.passEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditPasswordActivity.class);
            startActivity(intent);
        });

        binding.editProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        binding.logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        binding.avaBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        binding.backBtn.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, MainActivity.class)));

        binding.orderHistoryLayout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, OrderListActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadToCloudinary(imageUri);
        }
    }

    private void uploadToCloudinary(Uri imageUri) {
        try {
            InputStream iStream = getContentResolver().openInputStream(imageUri);
            byte[] inputData = getBytes(iStream);

            String mimeType = getContentResolver().getType(imageUri);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "avatar.jpg",
                            RequestBody.create(MediaType.parse(mimeType), inputData))
                    .addFormDataPart("upload_preset", "AppFood")
                    .build();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/dnnntf6qx/image/upload")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(ProfileActivity.this, "Lỗi upload: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    if (response.isSuccessful()) {
                        try {
                            JSONObject obj = new JSONObject(json);
                            String imageUrl = obj.getString("secure_url");

                            runOnUiThread(() -> {
                                Glide.with(ProfileActivity.this)
                                        .load(imageUrl)
                                        .into(binding.avaBtn);

                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (currentUser != null) {
                                    String userId = currentUser.getUid();
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(userId)
                                            .child("avatar")
                                            .setValue(imageUrl)
                                            .addOnSuccessListener(aVoid ->
                                                    Toast.makeText(ProfileActivity.this, "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(ProfileActivity.this, "Lỗi khi lưu ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                }
                            });
                        } catch (JSONException e) {
                            runOnUiThread(() ->
                                    Toast.makeText(ProfileActivity.this, "Lỗi xử lý JSON", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Log.e("CLOUDINARY_ERROR", json);
                        runOnUiThread(() ->
                                Toast.makeText(ProfileActivity.this, "Upload thất bại: " + response.message(), Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(ProfileActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
    // dark mode
    private void setupDarkModeSwitch() {
        binding.darkModeSwitch.setChecked(
                getSharedPreferences("settings", MODE_PRIVATE).getBoolean("dark_mode", false)
        );

        binding.darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });
    }
}
