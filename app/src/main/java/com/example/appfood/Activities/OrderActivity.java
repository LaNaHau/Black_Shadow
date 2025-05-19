package com.example.appfood.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appfood.Helper.ManagmentCart;
import com.example.appfood.R;
import com.example.appfood.databinding.ActivityCartBinding;
import com.example.appfood.databinding.ActivityOrderBinding;

public class OrderActivity extends BaseActivity {
    ActivityOrderBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setVariable();

    }

    private void setVariable() {
    }


}