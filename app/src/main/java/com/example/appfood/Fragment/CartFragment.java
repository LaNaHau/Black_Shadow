package com.example.appfood.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.appfood.Activities.CartActivity;
import com.example.appfood.Activities.MainActivity;
import com.example.appfood.R;

public class CartFragment extends Fragment {
    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_cart, container, false);
    }
}
