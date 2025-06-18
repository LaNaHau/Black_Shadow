package com.example.appfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appfood.Adapter.FoodLisAdapter;
import com.example.appfood.Domain.Foods;
import com.example.appfood.databinding.ActivityListFoodBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavouriteActivity extends BaseActivity {

    ActivityListFoodBinding binding;
    ArrayList<Foods> list = new ArrayList<>();
    FoodLisAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.titleTxt.setText("Món ăn yêu thích");
        binding.backBtn.setOnClickListener(v ->
                startActivity(new Intent(FavouriteActivity.this, MainActivity.class)));

        loadFavoriteFoods();
    }

    private void loadFavoriteFoods() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = (user != null) ? user.getUid() : "Guest";

        DatabaseReference favoriteRef =
                firebaseDatabase.getReference("Favorite").child(userId);
        binding.progressBar.setVisibility(View.VISIBLE);

        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot itemSnap : snapshot.getChildren()) {
                        Foods food = itemSnap.getValue(Foods.class);
                        if (food != null) {
                            list.add(food);
                        }
                    }
                }

                if (!list.isEmpty()) {
                    adapter = new FoodLisAdapter(list);
                    binding.foodListView.setLayoutManager(new LinearLayoutManager(FavouriteActivity.this));
                    binding.foodListView.setAdapter(adapter);
                } else {
                    binding.titleTxt.setText("Chưa có món yêu thích nào");
                }

                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FavouriteActivity", "Lỗi tải dữ liệu: " + error.getMessage());
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

}
