package com.example.appfood.Activities;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appfood.Domain.Foods;
import com.example.appfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BaseActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getReference();


    }
    protected void toggleFavorite(Foods food, ImageView button, boolean isLiked, Context context, ToggleFavoriteCallback callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = (user != null) ? user.getUid() : "Guest";

        if (!isLiked) {
            databaseReference.child("Favorite")
                    .child(userId)
                    .child(String.valueOf(food.getId()))
                    .setValue(food)
                    .addOnSuccessListener(unused -> {
                        button.setImageResource(R.drawable.like);
                        Toast.makeText(context, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                        callback.onResult(true);
                    });
        } else {
            databaseReference.child("Favorite")
                    .child(userId)
                    .child(String.valueOf(food.getId()))
                    .removeValue()
                    .addOnSuccessListener(unused -> {
                        button.setImageResource(R.drawable.favorite);
                        Toast.makeText(context, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                        callback.onResult(false);
                    });
        }
    }

    // Callback để cập nhật biến isLiked từ DetailActivity
    public interface ToggleFavoriteCallback {
        void onResult(boolean newIsLiked);
    }


}