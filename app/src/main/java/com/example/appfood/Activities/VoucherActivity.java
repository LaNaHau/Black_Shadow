package com.example.appfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appfood.Adapter.VoucherAdapter;
import com.example.appfood.Domain.Voucher;
import com.example.appfood.databinding.ActivityVoucherBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class VoucherActivity extends BaseActivity{
    ActivityVoucherBinding binding;
    List <Voucher> list = new ArrayList<>();
    VoucherAdapter adapter;
     DatabaseReference databaseRef;

    ArrayList<Voucher> discountVouchers = new ArrayList<>();
    ArrayList<Voucher> freeshipVouchers = new ArrayList<>();
    Voucher selectedDiscountVoucher = null;
    Voucher selectedFreeshipVoucher = null;
    VoucherAdapter discountAdapter;

    VoucherAdapter freeshipAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVoucherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setVariable();
        initVoucher();
    }

    private void initVoucher() {
         discountAdapter = new VoucherAdapter(this, discountVouchers, voucher -> {
            selectedDiscountVoucher = voucher;
        });

         freeshipAdapter = new VoucherAdapter(this, freeshipVouchers, voucher -> {
            selectedFreeshipVoucher = voucher;
        });


        // Gán adapter và layout cho 2 RecyclerView
        binding.discountVoucherList.setLayoutManager(new LinearLayoutManager(this));
        binding.discountVoucherList.setAdapter(discountAdapter);

        binding.freeshipVoucherList.setLayoutManager(new LinearLayoutManager(this));
        binding.freeshipVoucherList.setAdapter(freeshipAdapter);

        // Lấy dữ liệu từ Firebase
        String userId = mAuth.getCurrentUser().getUid();
        databaseRef = firebaseDatabase.getReference("vouchers").child(userId);

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                discountVouchers.clear();
                freeshipVouchers.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Voucher voucher = data.getValue(Voucher.class);
                    if (voucher != null) {
                        if ("discount".equalsIgnoreCase(voucher.getType())) {
                            discountVouchers.add(voucher);
                        } else if ("freeship".equalsIgnoreCase(voucher.getType())) {
                            freeshipVouchers.add(voucher);
                        }
                    }
                }
                discountAdapter.notifyDataSetChanged();
                freeshipAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("voucher", "Lỗi đọc dữ liệu Firebase: " + error.getMessage());
            }
        });

        binding.btnApplyVoucher.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_discount_voucher", selectedDiscountVoucher);
            resultIntent.putExtra("selected_freeship_voucher", selectedFreeshipVoucher);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

    }

    private void setVariable() {

    }


}
