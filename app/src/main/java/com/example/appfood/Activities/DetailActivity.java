    package com.example.appfood.Activities;

    import android.content.Context;
    import android.os.Bundle;
    import android.util.Log;

    import androidx.appcompat.app.AppCompatActivity;

    import com.bumptech.glide.Glide;
    import com.bumptech.glide.load.resource.bitmap.CenterCrop;
    import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
    import com.example.appfood.Domain.Foods;
    import com.example.appfood.Helper.ManagmentCart;
    import com.example.appfood.databinding.ActivityDetailBinding;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;

    public class DetailActivity extends AppCompatActivity {

        private static final String TAG = "DetailActivity";
        ActivityDetailBinding binding;
        private Foods object;
        private int num = 1;
        private ManagmentCart managmentCart;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityDetailBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            getIntentExtra();
            setVariable();

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

            binding.priceTxt.setText("$" + object.getPrice());
            binding.titleTxt.setText(object.getTitle());
            binding.descriptionTxt.setText(object.getDescription());
            binding.ratingTxt.setText(object.getStar() + " Rating");
            binding.ratingBar.setRating((float) object.getStar());
            binding.totalTxt.setText((num * object.getPrice()) + "$");

            binding.plusBtn.setOnClickListener(v -> {
                num = num + 1;
                Log.d(TAG, "Increased quantity: " + num);
                binding.numTxt.setText(String.valueOf(num));
                binding.totalTxt.setText("$" + (num * object.getPrice()));
            });

            binding.minusBtn.setOnClickListener(v -> {
                if (num > 1) {
                    num = num - 1;

                    binding.numTxt.setText(String.valueOf(num));
                    binding.totalTxt.setText("$" + (num * object.getPrice()));
                }
            });

            binding.cartButton.setOnClickListener(v -> {
                object.setNumberInCart(num);
                managmentCart.insertFood(object);
                Log.d(TAG, "Item added to cart: " + object.getTitle() + " | Quantity: " + num);
            });
        }


    }
