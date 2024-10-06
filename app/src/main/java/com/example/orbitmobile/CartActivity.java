package com.example.orbitmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orbitmobile.adapters.CartAdapter;
import com.example.orbitmobile.api.CartApi;
import com.example.orbitmobile.models.Cart;
import com.example.orbitmobile.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private LinearLayout emptyCartLayout, cartItemsLayout;
    private RecyclerView recyclerViewCartItems;
    private TextView cartSubtotal, cartShippingCost, cartTotal;
    private Button checkoutButton, exploreCategoriesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize UI components
        emptyCartLayout = findViewById(R.id.empty_cart_layout);
        cartItemsLayout = findViewById(R.id.cart_items_layout);
        recyclerViewCartItems = findViewById(R.id.recycler_view_cart_items);
        cartSubtotal = findViewById(R.id.cart_subtotal);
        cartShippingCost = findViewById(R.id.cart_shipping_cost);
        cartTotal = findViewById(R.id.cart_total);
        checkoutButton = findViewById(R.id.checkout_button);
        exploreCategoriesButton = findViewById(R.id.explore_categories_button);

        // Set up RecyclerView
        recyclerViewCartItems.setLayoutManager(new LinearLayoutManager(this));

        // Back Button functionality
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        // Get cart by customer ID
        getCart("67000e49ae37a0e4c8229472");  // Replace with dynamic customer ID if needed

        // Explore Categories Button functionality
        exploreCategoriesButton.setOnClickListener(v -> {
            // Navigate to Categories Activity
            startActivity(new Intent(CartActivity.this, ShopByCategoryActivity.class));
        });

        // Checkout Button functionality
        checkoutButton.setOnClickListener(v -> {
            // Navigate to Checkout Activity
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });
    }

    private void getCart(String customerId) {
        CartApi cartApi = ApiClient.getRetrofitInstance().create(CartApi.class);
        cartApi.getCartByCustomerId(customerId).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Cart cart = response.body();
                    displayCart(cart);
                } else {
                    // Display empty cart layout if no items
                    emptyCartLayout.setVisibility(View.VISIBLE);
                    cartItemsLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                // Handle failure (network error, etc.)
            }
        });
    }

    private void displayCart(Cart cart) {
        // Hide empty cart layout and show cart items layout
        emptyCartLayout.setVisibility(View.GONE);
        cartItemsLayout.setVisibility(View.VISIBLE);

        // Update the cart details
        CartAdapter cartAdapter = new CartAdapter(cart.getCartItems(), this);
        recyclerViewCartItems.setAdapter(cartAdapter);

        cartSubtotal.setText("Subtotal: $" + cart.getCartPrice());
        cartShippingCost.setText("Shipping Cost: $8.00");  // Static value for now
        cartTotal.setText("Total: $" + (cart.getCartPrice() + 8));  // Subtotal + Shipping
    }
}
