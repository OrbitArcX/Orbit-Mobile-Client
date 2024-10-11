package com.example.orbitmobile;

import android.os.Bundle;
import android.util.Log;
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
    private Vendor vendor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_details);

        vendorName = findViewById(R.id.vendor_name);
        vendorRating = findViewById(R.id.vendor_rating);
        recyclerViewReviews = findViewById(R.id.recycler_view_reviews);
        backButton = findViewById(R.id.back_button);

        // Back button click listener
        backButton.setOnClickListener(v -> finish());

        // Get the vendor JSON from the intent
        String vendorJson = getIntent().getStringExtra("vendor");
        vendor = new Gson().fromJson(vendorJson, Vendor.class);

        // Log the entire vendor object to ensure you have the right data
        Log.d("VendorDetailsActivity", "Vendor JSON: " + vendorJson);

        // Fetch updated vendor details using the vendor's ID
        fetchVendorDetails(vendor.getId());
    }

    private void fetchVendorDetails(String vendorId) {
        VendorApi vendorApi = ApiClient.getRetrofitInstance().create(VendorApi.class);

        // API call to get updated vendor details
        vendorApi.getVendorById(vendorId).enqueue(new Callback<Vendor>() {
            @Override
            public void onResponse(Call<Vendor> call, Response<Vendor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    vendor = response.body();

                    // Log the full updated vendor object
                    Log.d("VendorDetailsActivity", "Updated Vendor JSON: " + new Gson().toJson(vendor));

                    // Set the vendor name and rating in the UI
                    vendorName.setText(vendor.getName());
                    String formattedRating = String.format("%.2f", vendor.getRating());
                    vendorRating.setText(formattedRating + "/5");

                    // Fetch vendor reviews after loading vendor details
                    fetchVendorReviews(vendor.getId());
                } else {
                    Toast.makeText(VendorDetailsActivity.this, "Failed to fetch vendor details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Vendor> call, Throwable t) {
                Toast.makeText(VendorDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchVendorReviews(String vendorId) {
        VendorApi vendorApi = ApiClient.getRetrofitInstance().create(VendorApi.class);

        // API call to get vendor reviews
        vendorApi.getVendorReviews(vendorId).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Review> reviews = response.body();
                    reviewAdapter = new ReviewAdapter(reviews, VendorDetailsActivity.this);
                    recyclerViewReviews.setLayoutManager(new LinearLayoutManager(VendorDetailsActivity.this));
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
