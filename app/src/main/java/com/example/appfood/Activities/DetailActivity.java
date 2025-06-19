    package com.example.appfood.Activities;

    import android.content.Context;
    import android.os.Bundle;
    import android.util.Log;
    import android.widget.ImageView;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;

    import com.bumptech.glide.Glide;
    import com.bumptech.glide.load.resource.bitmap.CenterCrop;
    import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
    import com.example.appfood.Domain.Foods;
    import com.example.appfood.Helper.ManagmentCart;
    import com.example.appfood.R;
    import com.example.appfood.databinding.ActivityDetailBinding;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.ValueEventListener;

    import org.checkerframework.checker.nullness.qual.NonNull;

    import java.util.HashMap;
    import java.util.Map;

    public class DetailActivity extends BaseActivity {

        private static final String TAG = "DetailActivity";
        ActivityDetailBinding binding;
        private Foods object;
        private int num = 1;
        private ManagmentCart managmentCart;
        private boolean isLiked = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityDetailBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            getIntentExtra();
            setVariable();

            setupFavoriteButton();
            loadUserRating(String.valueOf(object.getId()));

            if (object != null) {
                Log.d(TAG, "Received object: " + object.getTitle());
                setVariable();
            } else {
                Log.e(TAG, "Object from Intent is null!");
            }
        }

        private void getIntentExtra() {
            try {
                object = (Foods) getIntent().getSerializableExtra("object");
                if (object == null) {
                    Log.e(TAG, "getIntentExtra: Failed to retrieve object from intent.");
                }
            } catch (Exception e) {
                Log.e(TAG, "getIntentExtra: Exception while retrieving object", e);
            }
        }
    Context context = this;
        private void setVariable() {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            managmentCart = new ManagmentCart(context, currentUser.getUid());

            binding.backBtn.setOnClickListener(v -> {finish();});

            Glide.with(this)
                    .load(object.getImagePath())
                    .transform(new CenterCrop(), new RoundedCorners(60))
                    .into(binding.pic);

            binding.priceTxt.setText(object.getPrice() + " VND");
            binding.titleTxt.setText(object.getTitle());
            binding.descriptionTxt.setText(object.getDescription());
            binding.ratingTxt.setText(object.getStar() + " Rating");
            binding.ratingBar.setRating((float) object.getStar());
            binding.totalTxt.setText((num * object.getPrice())+ " VND");

            binding.plusBtn.setOnClickListener(v -> {
                num = num + 1;
                Log.d(TAG, "Increased quantity: " + num);
                binding.numTxt.setText(String.valueOf(num));
                binding.totalTxt.setText((num * object.getPrice()) + " VND");
            });

            binding.minusBtn.setOnClickListener(v -> {
                if (num > 1) {
                    num = num - 1;

                    binding.numTxt.setText(String.valueOf(num));
                    binding.totalTxt.setText( (num * object.getPrice()) + " VND");
                }
            });

            binding.cartButton.setOnClickListener(v -> {
                object.setNumberInCart(num);
                managmentCart.insertFood(object);
                Log.d(TAG, "Item added to cart: " + object.getTitle() + " | Quantity: " + num);
            });
        }

        //Danh Gia

        private void setupFavoriteButton() {
            ImageView favoBtn = findViewById(R.id.favoBtn);

            checkIfFavorite(favoBtn);

            favoBtn.setOnClickListener(v -> {
                if (object != null) {
                    toggleFavorite(object, favoBtn, isLiked, DetailActivity.this, newIsLiked -> {
                        isLiked = newIsLiked; // Cập nhật trạng thái
                    });
                }
            });
        }

        private void checkIfFavorite(ImageView favoBtn) {
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = (user != null) ? user.getUid() : "Guest";

            databaseReference.child("Favorite")
                    .child(userId)
                    .child(String.valueOf(object.getId()))
                    .get()
                    .addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            isLiked = true;
                            favoBtn.setImageResource(R.drawable.like);
                        } else {
                            isLiked = false;
                            favoBtn.setImageResource(R.drawable.favorite);
                        }
                    });
        }
        private void enableRating() {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null || object == null) return;
            String userId = user.getUid();
            String foodId = String.valueOf(object.getId());
            String foodCategoryId = String.valueOf(object.getCategoryId());

            DatabaseReference ordersRef = databaseReference.child("orders")
                    .child(userId);
            ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean purchased = false;

                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        String status = orderSnapshot.child("status").getValue(String.class);
                        String uid = orderSnapshot.child("userId").getValue(String.class);

                        if (!userId.equals(uid) || !"Completed".equals(status)) continue;

                        DataSnapshot foodListSnapshot = orderSnapshot.child("foodList");
                        for (DataSnapshot foodItemSnapshot : foodListSnapshot.getChildren()) {
                            Log.d("DEBUG", "Đơn hàng ID: " + orderSnapshot.getKey());
                            Log.d("DEBUG", "Trạng thái: " + status + " - UID: " + uid);
                            String orderedFoodId = String.valueOf(foodItemSnapshot.child("Id").getValue());

                            if (foodId.equals(orderedFoodId)) {
                                purchased = true;
                                break;
                            }
                        }

                        if (purchased) break;
                    }

                    if (purchased) {
                        // Đã mua → Cho phép đánh giá
                        binding.ratingBarInput.setIsIndicator(false);
                        binding.submitRatingBtn.setEnabled(true);
                        binding.submitRatingBtn.setAlpha(1f);
                        binding.submitRatingBtn.setOnClickListener(v -> {
                            float rating = binding.ratingBarInput.getRating();
                            saveRatingToFirebase(rating);
                            binding.ratingBarInput.setIsIndicator(true);
                            binding.submitRatingBtn.setEnabled(false);
                            binding.submitRatingBtn.setAlpha(0.5f);
                        });
                    } else {
                        // Chưa mua → Không cho đánh giá
                        binding.ratingBarInput.setIsIndicator(true);
                        binding.submitRatingBtn.setEnabled(true);
                        binding.submitRatingBtn.setAlpha(1f);
                        binding.submitRatingBtn.setOnClickListener(v -> {
                            binding.submitRatingBtn.setEnabled(false);
                            binding.submitRatingBtn.setAlpha(0.5f);
                            Toast.makeText(DetailActivity.this, "Bạn cần mua món này trước khi đánh giá", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("RatingCheck", "Lỗi truy vấn: " + error.getMessage());
                }
            });
        }

        private void saveRatingToFirebase(float rating) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null || object == null) {
                Toast.makeText(this, "Lỗi: Chưa đăng nhập hoặc không có dữ liệu món ăn", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = user.getUid();
            String foodId = String.valueOf(object.getId());

            DatabaseReference ratingRef = databaseReference
                    .child("Ratings")
                    .child(foodId)
                    .child(userId);

            Map<String, Object> ratingData = new HashMap<>();
            ratingData.put("star", rating);

            ratingRef.setValue(ratingData)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Đánh giá của bạn đã được ghi nhận", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Lưu đánh giá thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        private void loadUserRating(String foodId) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) return;

            String userId = user.getUid();

            DatabaseReference ratingRef = databaseReference
                    .child("Ratings")
                    .child(foodId)
                    .child(userId)
                    .child("star");

            ratingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Float userRating = snapshot.getValue(Float.class);
                        if (userRating != null) {
                            // Gán lại số sao vào RatingBar
                            binding.ratingBarInput.setRating(userRating);
                            binding.ratingBarInput.setIsIndicator(true);
                            binding.submitRatingBtn.setEnabled(false);
                            binding.submitRatingBtn.setAlpha(0.5f);
                        }
                    }else{
                        enableRating();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("RatingLoad", "Lỗi load đánh giá: " + error.getMessage());
                }
            });
        }


    }
