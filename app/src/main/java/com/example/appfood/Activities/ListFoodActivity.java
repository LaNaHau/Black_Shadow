package com.example.appfood.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appfood.Adapter.FoodLisAdapter;
import com.example.appfood.Domain.Foods;
import com.example.appfood.databinding.ActivityListFoodBinding;
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

        Log.d("FoodList", "Received CategoryId: " + categoryId);

        binding.titleTxt.setText(categoryName);
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void initList() {
        DatabaseReference myRef = firebaseDatabase.getReference("Foods").child("Foods");
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<Foods> list = new ArrayList<>();

        Query query = myRef.orderByChild("CategoryId").equalTo(categoryId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Foods foodItem = child.getValue(Foods.class);
                        if (foodItem != null) {
                            list.add(foodItem);
                        }
                    }
                    Log.d("FoodList", "Have List");
                    if (list.size() > 0) {
                        // Gán adapter
                        FoodLisAdapter adapter = new FoodLisAdapter(list);
                        binding.foodListView.setLayoutManager(new LinearLayoutManager(ListFoodActivity.this,
                                LinearLayoutManager.VERTICAL, false));
                        binding.foodListView.setAdapter(adapter);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                } else {
                    Log.d("FoodList", "No Data");
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
