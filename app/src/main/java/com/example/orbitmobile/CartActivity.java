package com.example.orbitmobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orbitmobile.adapters.CartAdapter;
import com.example.orbitmobile.api.CartApi;
import com.example.orbitmobile.models.Cart;
import com.example.orbitmobile.models.CartItem;
import com.example.orbitmobile.models.LoginSuccessResponse;
import com.example.orbitmobile.network.ApiClient;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView removeAll, subtotalText, shippingCostText, totalText;
    private RecyclerView recyclerViewCart;
    private Button checkoutButton;
    private CartAdapter cartAdapter;
    private double subtotal = 0;
    private double shippingCost = 8.00;  // this cost is not from backend
    private double total = 0;
    private Cart existingCart;
    private String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        backButton = findViewById(R.id.back_button);
        removeAll = findViewById(R.id.remove_all);
        subtotalText = findViewById(R.id.subtotal);
        shippingCostText = findViewById(R.id.shipping_cost);
        totalText = findViewById(R.id.total);
        recyclerViewCart = findViewById(R.id.recycler_view_cart);
        checkoutButton = findViewById(R.id.checkout_button);

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));

        // Load cart items from API
        loadCartItems();

        // Back button
        backButton.setOnClickListener(v -> finish());

        // Remove all cart items
        removeAll.setOnClickListener(v -> clearCart());

        // Checkout button
        checkoutButton.setOnClickListener(v -> {
            if (subtotal > 0) {
                updateCartBeforeCheckout();
            } else {
                Toast.makeText(CartActivity.this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCartItems() {
        CartApi cartApi = ApiClient.getRetrofitInstance().create(CartApi.class);
        String userJson = getSharedPreferences("OrbitPrefs", MODE_PRIVATE)
                .getString("userObject", null);

        if (userJson != null) {
            LoginSuccessResponse user = new Gson().fromJson(userJson, LoginSuccessResponse.class);
            customerId = user.getId();
            // logging to check customerId
            Log.d("customerId from object", "Customer ID: " + customerId);

            cartApi.getCartByCustomerId(customerId).enqueue(new Callback<Cart>() {
                @Override
                public void onResponse(@NonNull Call<Cart> call, @NonNull Response<Cart> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        existingCart = response.body();
                        List<CartItem> cartItems = existingCart.getCartItems();
                        cartAdapter = new CartAdapter(cartItems, CartActivity.this, CartActivity.this::updatePrices);
                        recyclerViewCart.setAdapter(cartAdapter);
                        calculatePrices(cartItems);
                    } else {
                        // if 404 or empty response Load empty cart layout
                        setContentView(R.layout.activity_cart_empty);
                        initializeEmptyCartComponents();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Cart> call, @NonNull Throwable t) {
                    Toast.makeText(CartActivity.this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
                    setContentView(R.layout.activity_cart_empty);
                    initializeEmptyCartComponents();
                }
            });
        } else {
            // Load empty cart layout if no user info
            setContentView(R.layout.activity_cart_empty);
            initializeEmptyCartComponents();
        }
    }

    private void initializeEmptyCartComponents() {
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }

    // Clear all cart items
    private void clearCart() {
        if (cartAdapter != null) {
            cartAdapter.clearCart();
            subtotal = 0;
            total = 0;
            updatePrices(subtotal);
        }
    }

    // Calculate
    private void calculatePrices(List<CartItem> cartItems) {
        subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getTotalPrice();
        }
        total = subtotal + shippingCost;
        updatePrices(subtotal);
    }

    public void updatePrices(double newSubtotal) {
        subtotalText.setText(String.format("Rs.%.2f", newSubtotal));
        shippingCostText.setText(String.format("Rs.%.2f", shippingCost));  // Static shipping cost
        total = newSubtotal + shippingCost;
        totalText.setText(String.format("Rs.%.2f", total));
    }
    // Update the cart before checkout
    private void updateCartBeforeCheckout() {
        if (existingCart != null) {
            existingCart.setCartItems(cartAdapter.getCartItemList());
            existingCart.setCartPrice(subtotal);
            CartApi cartApi = ApiClient.getRetrofitInstance().create(CartApi.class);

            // Make API call to update the cart
            cartApi.updateCart(existingCart.getId(), existingCart).enqueue(new Callback<Cart>() {
                @Override
                public void onResponse(@NonNull Call<Cart> call, @NonNull Response<Cart> response) {
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                        intent.putExtra("total", total);
                        String cartJson = new Gson().toJson(existingCart);
                        intent.putExtra("cart", cartJson);
                        startActivity(intent);
                    } else {
                        Toast.makeText(CartActivity.this, "Failed to update cart", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<Cart> call, @NonNull Throwable t) {
                    Toast.makeText(CartActivity.this, "Network error. Try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
