package com.example.appfood.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.appfood.databinding.ActivityIntroBinding;

public class IntroActivity extends BaseActivity {
    ActivityIntroBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.goBtn.setOnClickListener(v ->{
            startActivity(new Intent(IntroActivity.this, RegisterActivity.class));
        });

    }
}