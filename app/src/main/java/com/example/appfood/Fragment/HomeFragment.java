package com.example.appfood.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.appfood.Activities.BaseActivity;
import com.example.appfood.Activities.ListFoodActivity;
import com.example.appfood.Adapter.CategoryAdapter;
import com.example.appfood.Adapter.SliderViewHolderAdapter;
import com.example.appfood.Domain.Category;
import com.example.appfood.Domain.SliderItem;
import com.example.appfood.Domain.User;
import com.example.appfood.R;
import com.example.appfood.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;


import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        initProfile();
        initBanner();
        initCategory();
        setSearchEvent();

        return binding.getRoot();
    }

    private void initProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    String avatarUrl = snapshot.child("avatar").getValue(String.class);
                    binding.textView2.setText(user.getUserName());
                    Glide.with(requireContext()).load(avatarUrl).into(binding.imageView6);
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initBanner() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Banners");

        binding.progressBarBanner.setVisibility(View.VISIBLE);
        ArrayList<SliderItem> list = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot a : snapshot.getChildren()) {
                    list.add(a.getValue(SliderItem.class));
                }
                binding.progressBarBanner.setVisibility(View.GONE);
                binding.viewPager2.setAdapter(new SliderViewHolderAdapter(binding.viewPager2, list));
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi banner: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initCategory() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Category");

        binding.progressBarCategory.setVisibility(View.VISIBLE);
        ArrayList<Category> list = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot a : snapshot.getChildren()) {
                    list.add(a.getValue(Category.class));
                }
                binding.cartgoryView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                binding.cartgoryView.setAdapter(new CategoryAdapter(list));
                binding.progressBarCategory.setVisibility(View.GONE);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi category: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSearchEvent() {
        binding.searchTxt.setOnClickListener(v -> {
            String keyword = binding.searchTxt.getText().toString().trim();
            if (!keyword.isEmpty()) {
                Intent intent = new Intent(getContext(), ListFoodActivity.class);
                intent.putExtra("searchKeyword", keyword);
                startActivity(intent);
            }
        });
    }
}

