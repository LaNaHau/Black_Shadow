package com.example.appfood.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appfood.Adapter.FoodLisAdapter;
import com.example.appfood.Domain.Foods;
import com.example.appfood.databinding.ActivityListFoodBinding;
import com.example.appfood.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListFoodActivity extends BaseActivity {
    ActivityListFoodBinding binding;
    private int categoryId;
    private String categoryName;
    ArrayList<Foods> list = new ArrayList<>();
    FoodLisAdapter adapter;

    private String searchKeyword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntentExtra();
        initList();
    }

    private void getIntentExtra() {
        categoryId = getIntent().getIntExtra("CategoryId", 0);
        categoryName = getIntent().getStringExtra("CategoryName");
        searchKeyword = getIntent().getStringExtra("searchKeyword");

        if (searchKeyword != null && !searchKeyword.isEmpty())
            binding.titleTxt.setText(searchKeyword);
        else binding.titleTxt.setText(categoryName);

        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void initList() {
        DatabaseReference myRef = firebaseDatabase.getReference("Foods").child("Foods");
        binding.progressBar.setVisibility(View.VISIBLE);

        Query query = myRef.orderByChild("CategoryId").equalTo(categoryId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Foods foodItem = child.getValue(Foods.class);
                        if (foodItem != null) {
                            if (searchKeyword != null && !searchKeyword.isEmpty()) {
                                if (foodItem.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())) {
                                    list.add(foodItem);
                                }
                            } else {
                                if (foodItem.getCategoryId() == categoryId) {
                                    list.add(foodItem);
                                }
                            }
                        }
                    }


                    if (!list.isEmpty()) {
                        // Gán adapter
                        adapter = new FoodLisAdapter(list);
                        binding.foodListView.setLayoutManager(new LinearLayoutManager(ListFoodActivity.this,
                                LinearLayoutManager.VERTICAL, false));
                        binding.foodListView.setAdapter(adapter);
                    }else {
                        // Hiển thị thông báo không có kết quả
                        binding.titleTxt.setText("No food match with your search");
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FoodList", "Database error: " + error.getMessage());
                binding.progressBar.setVisibility(View.GONE);
            }
        });

    }


}
