package com.example.orbitmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.orbitmobile.api.CartApi;
import com.example.orbitmobile.models.CartRequest;
import com.example.orbitmobile.models.CartItem;
import com.example.orbitmobile.models.Product;
import com.example.orbitmobile.models.LoginSuccessResponse;
import com.example.orbitmobile.network.ApiClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productPrice, productDescription, vendorName, vendorReviews, quantityText;
    private ImageButton increaseQuantityButton, decreaseQuantityButton;
    private Button addToCartButton;
    private int quantity = 1;
    private Product product;
    private LoginSuccessResponse customer;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Initialize UI elements
        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productDescription = findViewById(R.id.product_description);
        vendorName = findViewById(R.id.vendor_name);
        vendorReviews = findViewById(R.id.vendor_reviews);
        quantityText = findViewById(R.id.quantity);
        increaseQuantityButton = findViewById(R.id.increase_quantity);
        decreaseQuantityButton = findViewById(R.id.decrease_quantity);
        addToCartButton = findViewById(R.id.add_to_cart_button);

        backButton = findViewById(R.id.back_button);

        // Back button listener
        backButton.setOnClickListener(v -> finish());

        // Retrieve the product data passed from the previous activity
        String productJson = getIntent().getStringExtra("product");
        product = new Gson().fromJson(productJson, Product.class);

        // Retrieve user data from shared preferences (LoginSuccessResponse used instead of User model)
        customer = new Gson().fromJson(getSharedPreferences("OrbitPrefs", MODE_PRIVATE).getString("userObject", ""), LoginSuccessResponse.class);

        // Set product details
        productName.setText(product.getName());
        productPrice.setText("$" + product.getPrice());
        productDescription.setText(product.getDescription());
        Glide.with(this).load(product.getImageUrl()).into(productImage);

        // Set vendor details (vendor has no image, only name and reviews)
        vendorName.setText(product.getVendor().getName());
        vendorReviews.setText(product.getVendor().getEmail());  // Using vendor email for demo
        // No vendor image available, so you can set a default placeholder or omit it

        // Set quantity adjustment functionality
        quantityText.setText(String.valueOf(quantity));
        increaseQuantityButton.setOnClickListener(v -> adjustQuantity(1));
        decreaseQuantityButton.setOnClickListener(v -> adjustQuantity(-1));

        // Handle "Add to Cart" button
        addToCartButton.setOnClickListener(v -> addToCart());

        // Handle vendor click to go to vendor reviews
        vendorName.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailsActivity.this, VendorDetailsActivity.class);
            intent.putExtra("vendor", new Gson().toJson(product.getVendor()));  // Pass vendor data
            startActivity(intent);
        });
    }

    // Adjust quantity
    private void adjustQuantity(int change) {
        quantity = Math.max(1, quantity + change);  // Quantity cannot go below 1
        quantityText.setText(String.valueOf(quantity));
    }

    // Add product to cart
    private void addToCart() {
        CartApi cartApi = ApiClient.getRetrofitInstance().create(CartApi.class);

        // Prepare cart item
        CartItem cartItem = new CartItem(product, quantity, product.getPrice() * quantity);
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);

        // Create cart request (using LoginSuccessResponse for the customer object)
        CartRequest cartRequest = new CartRequest(customer, product.getPrice() * quantity, "Negombo", cartItems);

        // Make API call to add the product to the cart
        cartApi.addToCart(cartRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Show success message and popup dialog
                    showAddToCartSuccessPopup();
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, "Network error. Try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show a dialog for add to cart success
    private void showAddToCartSuccessPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsActivity.this);
        View popupView = getLayoutInflater().inflate(R.layout.dialog_add_to_cart_success, null);

        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        dialog.show();

        Button goToCartButton = popupView.findViewById(R.id.go_to_cart_button);
        goToCartButton.setOnClickListener(v -> {
            // Navigate to Cart Activity
            Intent intent = new Intent(ProductDetailsActivity.this, CartActivity.class);
            startActivity(intent);
            dialog.dismiss();
        });
    }
}
