package com.example.orbitmobile;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orbitmobile.adapters.ReviewAdapter;
import com.example.orbitmobile.api.VendorApi;
import com.example.orbitmobile.models.Review;
import com.example.orbitmobile.models.Vendor;
import com.example.orbitmobile.network.ApiClient;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendorDetailsActivity extends AppCompatActivity {

    private TextView vendorName, vendorRating;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_details);

        vendorName = findViewById(R.id.vendor_name);
        vendorRating = findViewById(R.id.vendor_rating);
        recyclerViewReviews = findViewById(R.id.recycler_view_reviews);

        backButton = findViewById(R.id.back_button);
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Get vendor data
        String vendorJson = getIntent().getStringExtra("vendor");
        Vendor vendor = new Gson().fromJson(vendorJson, Vendor.class);

        vendorName.setText(vendor.getName());
        vendorRating.setText(vendor.getRating() + "/10");
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        fetchVendorReviews(vendor.getId());
    }

    private void fetchVendorReviews(String vendorId) {
        VendorApi vendorApi = ApiClient.getRetrofitInstance().create(VendorApi.class);
        vendorApi.getVendorReviews(vendorId).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Review> reviews = response.body();
                    reviewAdapter = new ReviewAdapter(reviews, VendorDetailsActivity.this);
                    recyclerViewReviews.setAdapter(reviewAdapter);
                } else {
                    Toast.makeText(VendorDetailsActivity.this, "Failed to fetch reviews", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Toast.makeText(VendorDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
