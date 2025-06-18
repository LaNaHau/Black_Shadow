package com.example.appfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.appfood.Adapter.CategoryAdapter;
import com.example.appfood.Adapter.SliderViewHolderAdapter;
import com.example.appfood.Domain.Category;
import com.example.appfood.Domain.SliderItem;
import com.example.appfood.Domain.User;
import com.example.appfood.Fragment.CartFragment;
import com.example.appfood.Fragment.FavouriteFragment;
import com.example.appfood.Fragment.HomeFragment;
import com.example.appfood.Fragment.ProfileFragment;
import com.example.appfood.R;
import com.example.appfood.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initCategory();
        setVariable();
        initBanner();
        initProfile();

    }

    private void initProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = firebaseDatabase.getReference("Users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            String avatarUrl = snapshot.child("avatar").getValue(String.class);
                            binding.textView2.setText(user.getUserName());
                            Glide.with(MainActivity.this)
                                    .load(avatarUrl).into(binding.imageView6);
                        } else {
                            Toast.makeText(MainActivity.this, "Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }

    private void initBanner() {
        DatabaseReference myRef = firebaseDatabase.getReference("Banners");


        binding.progressBarBanner.setVisibility(View.VISIBLE);
        ArrayList<SliderItem> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot a : snapshot.getChildren()) {
                        list.add(a.getValue(SliderItem.class));
                    }
                    banner(list);
                    binding.progressBarBanner.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void banner(ArrayList<SliderItem> list) {
        binding.viewPager2.setAdapter(new SliderViewHolderAdapter(binding.viewPager2, list));
        binding.viewPager2.setClipChildren(false);
        binding.viewPager2.setClipToPadding(false);
        binding.viewPager2.setOffscreenPageLimit(3);
        binding.viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        binding.viewPager2.setPageTransformer(compositePageTransformer);
    }

    private void setVariable() {
        binding.bottomMenu.setItemSelected(R.id.home, true);

        binding.bottomMenu.setOnItemSelectedListener(item -> {
            if (item == R.id.cart) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            if (item == R.id.profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            if (item == R.id.favourites) {
                startActivity(new Intent(MainActivity.this, FavouriteActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

        });

        binding.searchTxt.setOnClickListener(v -> {
            String keyword = binding.searchTxt.getText().toString().trim();
            if (!keyword.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, ListFoodActivity.class);
                intent.putExtra("searchKeyword", keyword);
                startActivity(intent);
            }
        });
    }


    private void initCategory() {
        DatabaseReference myRef = firebaseDatabase.getReference("Category");

        binding.progressBarCategory.setVisibility(View.VISIBLE);
        ArrayList<Category> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot a : snapshot.getChildren()) {
                        list.add(a.getValue(Category.class));
                    }
                    if (!list.isEmpty()) {
                        binding.cartgoryView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
                        binding.cartgoryView.setAdapter(new CategoryAdapter(list));
                    }
                    binding.progressBarCategory.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


}