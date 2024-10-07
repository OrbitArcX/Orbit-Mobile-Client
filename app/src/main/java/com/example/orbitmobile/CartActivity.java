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
    private double shippingCost = 8.00;  // Example static shipping cost
    private double total = 0;
    private Cart existingCart; // Cart object for updating
    private String customerId; // To store the customer ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize UI components
        backButton = findViewById(R.id.back_button);
        removeAll = findViewById(R.id.remove_all);
        subtotalText = findViewById(R.id.subtotal);
        shippingCostText = findViewById(R.id.shipping_cost);
        totalText = findViewById(R.id.total);
        recyclerViewCart = findViewById(R.id.recycler_view_cart);
        checkoutButton = findViewById(R.id.checkout_button);

        // Setup RecyclerView
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));

        // Load cart items from API
        loadCartItems();

        // Back button click event
        backButton.setOnClickListener(v -> finish());

        // Remove all cart items click event
        removeAll.setOnClickListener(v -> clearCart());

        // Checkout button click event
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

        // Retrieve user object from SharedPreferences
        String userJson = getSharedPreferences("OrbitPrefs", MODE_PRIVATE)
                .getString("userObject", null);

        if (userJson != null) {
            // Parse the user object
            LoginSuccessResponse user = new Gson().fromJson(userJson, LoginSuccessResponse.class);
            customerId = user.getId();  // Get the customer ID from the parsed object
            Log.d("customerId from object", "Customer ID: " + customerId);

            // Make API call using the correct customer ID
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
                        // Load empty cart layout if 404 or empty response
                        setContentView(R.layout.activity_cart_empty);
                        initializeEmptyCartComponents();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Cart> call, @NonNull Throwable t) {
                    Toast.makeText(CartActivity.this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
                    setContentView(R.layout.activity_cart_empty);  // Load empty cart layout on failure
                    initializeEmptyCartComponents();
                }
            });
        } else {
            // Load empty cart layout if no user info
            setContentView(R.layout.activity_cart_empty);
            initializeEmptyCartComponents();
        }
    }

    // Initialize empty cart components for the back button functionality
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
            updatePrices(subtotal);  // Call updatePrices with one parameter
        }
    }

    // Calculate subtotal, shipping cost, and total
    private void calculatePrices(List<CartItem> cartItems) {
        subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getTotalPrice();  // Sum up the prices
        }
        total = subtotal + shippingCost;
        updatePrices(subtotal);  // Call updatePrices with one parameter
    }

    // Update price labels (Now it takes just one parameter for subtotal)
    public void updatePrices(double newSubtotal) {
        subtotalText.setText(String.format("Rs.%.2f", newSubtotal));
        shippingCostText.setText(String.format("Rs.%.2f", shippingCost));  // Static shipping cost
        total = newSubtotal + shippingCost;
        totalText.setText(String.format("Rs.%.2f", total));
    }

    // Update the cart before proceeding to checkout
    private void updateCartBeforeCheckout() {
        if (existingCart != null) {
            // Update the cart items and price before checkout
            existingCart.setCartItems(cartAdapter.getCartItemList());  // Get the updated cart items from the adapter
            existingCart.setCartPrice(subtotal);

            CartApi cartApi = ApiClient.getRetrofitInstance().create(CartApi.class);

            // Make API call to update the cart
            cartApi.updateCart(existingCart.getId(), existingCart).enqueue(new Callback<Cart>() {
                @Override
                public void onResponse(@NonNull Call<Cart> call, @NonNull Response<Cart> response) {
                    if (response.isSuccessful()) {
                        // Proceed to checkout activity
                        Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                        intent.putExtra("total", total);  // Pass total amount to CheckoutActivity

                        // Pass the updated cart object
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
