package com.example.orbitmobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orbitmobile.adapters.OrderItemsAdapter;
import com.example.orbitmobile.api.VendorApi;
import com.example.orbitmobile.models.LoginSuccessResponse;
import com.example.orbitmobile.models.OrderItem;
import com.example.orbitmobile.models.RatingRequest;
import com.example.orbitmobile.models.RatingResponse;
import com.example.orbitmobile.models.Vendor;
import com.example.orbitmobile.network.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrderItems;
    private List<OrderItem> orderItems;
    private OrderItemsAdapter orderItemsAdapter;
    private Context context;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_items);

        recyclerViewOrderItems = findViewById(R.id.recycler_view_order_items);
        recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(this));

        backButton = findViewById(R.id.back_button);
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Deserialize the order items list from JSON passed via intent
        String orderItemsJson = getIntent().getStringExtra("orderItems");
        Type orderItemListType = new TypeToken<List<OrderItem>>() {}.getType();  // Correct the type for deserialization
        orderItems = new Gson().fromJson(orderItemsJson, orderItemListType);

        if (orderItems != null && !orderItems.isEmpty()) {
            // Set up RecyclerView with the adapter
            orderItemsAdapter = new OrderItemsAdapter(orderItems, this);
            recyclerViewOrderItems.setAdapter(orderItemsAdapter);
        } else {
            Toast.makeText(this, "No order items available.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to display rating popup
    public void showRatingPopup(Vendor vendor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupView = LayoutInflater.from(this).inflate(R.layout.dialog_rate_vendor, null);
        builder.setView(popupView);

        // Initialize views
        RatingBar ratingBar = popupView.findViewById(R.id.rating_bar);
        EditText commentInput = popupView.findViewById(R.id.comment_input);
        Button submitRatingButton = popupView.findViewById(R.id.submit_rating_button);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle submit button click
        submitRatingButton.setOnClickListener(v -> {
            float ratingValue = ratingBar.getRating();
            String comment = commentInput.getText().toString().trim();

            // Call method to submit rating
            submitRating(vendor, ratingValue, comment);
            dialog.dismiss();
        });
    }


    // Submit the rating to the API
    private void submitRating(Vendor vendor, float rating, String comment) {
        VendorApi vendorApi = ApiClient.getRetrofitInstance().create(VendorApi.class);

        // Retrieve the logged-in customer details
        String userJson = getSharedPreferences("OrbitPrefs", Context.MODE_PRIVATE)
                .getString("userObject", null);
        LoginSuccessResponse customer = new Gson().fromJson(userJson, LoginSuccessResponse.class);

        // Create the rating request with both vendor and customer objects
        RatingRequest ratingRequest = new RatingRequest(rating, comment, vendor, customer);

        vendorApi.submitRating(ratingRequest).enqueue(new Callback<RatingResponse>() {
            @Override
            public void onResponse(Call<RatingResponse> call, Response<RatingResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderItemsActivity.this, "Rating submitted successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OrderItemsActivity.this, "Failed to submit rating.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RatingResponse> call, Throwable t) {
                Toast.makeText(OrderItemsActivity.this, "Network error. Try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
