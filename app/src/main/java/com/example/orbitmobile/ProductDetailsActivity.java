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
import com.example.orbitmobile.models.Cart;
import com.example.orbitmobile.models.CartItem;
import com.example.orbitmobile.models.CartRequest;
import com.example.orbitmobile.models.LoginSuccessResponse;
import com.example.orbitmobile.models.Product;
import com.example.orbitmobile.network.ApiClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productPrice, productDescription, vendorName, vendorReviews, quantityText, VendorRating;
    private ImageButton increaseQuantityButton, decreaseQuantityButton;
    private Button addToCartButton;
    private int quantity = 1;
    private Product product;
    private LoginSuccessResponse customer;
    private ImageView backButton;
    private Cart existingCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

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
        VendorRating = findViewById(R.id.vendor_rating);

        // Back button listener
        backButton.setOnClickListener(v -> finish());

        // get the product data from previous
        String productJson = getIntent().getStringExtra("product");
        product = new Gson().fromJson(productJson, Product.class);
        customer = new Gson().fromJson(getSharedPreferences("OrbitPrefs", MODE_PRIVATE).getString("userObject", ""), LoginSuccessResponse.class);

        // Set product
        productName.setText(product.getName());
        productPrice.setText("Rs." + product.getPrice());
        productDescription.setText(product.getDescription());
        Glide.with(this).load(product.getImageUrl()).into(productImage);

        // Set vendor
        vendorName.setText(product.getVendor().getName());
        vendorReviews.setText(product.getVendor().getEmail());
        //VendorRating.setText(product.getVendor().getRating()+"/5");

        // Set quantity adjustment functionality
        quantityText.setText(String.valueOf(quantity));
        increaseQuantityButton.setOnClickListener(v -> adjustQuantity(1));
        decreaseQuantityButton.setOnClickListener(v -> adjustQuantity(-1));

        // Add button
        addToCartButton.setOnClickListener(v -> checkAndAddToCart());

        //vendor click to vendor reviews
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

    // Add product to cart (check if cart exists first)
    private void checkAndAddToCart() {
        CartApi cartApi = ApiClient.getRetrofitInstance().create(CartApi.class);
        cartApi.getCartByCustomerId(customer.getId()).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    existingCart = response.body();
                    updateExistingCart();
                } else {
                    createNewCart();
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, "Failed to check cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Create a new cart and add the product
    private void createNewCart() {
        CartApi cartApi = ApiClient.getRetrofitInstance().create(CartApi.class);
        CartItem cartItem = new CartItem(product, quantity, product.getPrice() * quantity);
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        CartRequest cartRequest = new CartRequest(customer, product.getPrice() * quantity, "Negombo", cartItems);

        cartApi.addToCart(cartRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showAddToCartSuccessPopup();
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Failed to create cart", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, "Network error. Try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Update the existing cart
    private void updateExistingCart() {
        CartApi cartApi = ApiClient.getRetrofitInstance().create(CartApi.class);
        CartItem newItem = new CartItem(product, quantity, product.getPrice() * quantity);
        existingCart.getCartItems().add(newItem);
        existingCart.setCartPrice(existingCart.getCartPrice() + newItem.getTotalPrice());
        cartApi.updateCart(existingCart.getId(), existingCart).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {
                    showAddToCartSuccessPopup();
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Failed to update cart", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, "Network error. Try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // success dialog
    private void showAddToCartSuccessPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsActivity.this);
        View popupView = getLayoutInflater().inflate(R.layout.dialog_add_to_cart_success, null);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button goToCartButton = popupView.findViewById(R.id.go_to_cart_button);
        goToCartButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailsActivity.this, CartActivity.class);
            startActivity(intent);
            dialog.dismiss();
        });
    }
}
