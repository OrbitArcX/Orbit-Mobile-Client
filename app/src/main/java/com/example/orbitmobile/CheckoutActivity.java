package com.example.orbitmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.orbitmobile.api.CartApi;
import com.example.orbitmobile.api.OrderApi;
import com.example.orbitmobile.models.Cart;
import com.example.orbitmobile.models.CartItem;
import com.example.orbitmobile.models.OrderRequest;
import com.example.orbitmobile.models.LoginSuccessResponse;
import com.example.orbitmobile.network.ApiClient;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private TextView subtotalText, shippingCostText, totalText;
    private EditText shippingAddressInput;
    private Button placeOrderButton;
    private Cart cart;
    private LoginSuccessResponse customer;
    private double shippingCost = 8.00;
    private double totalAmount = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize UI components
        subtotalText = findViewById(R.id.subtotal);
        shippingCostText = findViewById(R.id.shipping_cost);
        totalText = findViewById(R.id.total);
        shippingAddressInput = findViewById(R.id.shipping_address);
        placeOrderButton = findViewById(R.id.place_order_button);

        String userJson = getSharedPreferences("OrbitPrefs", MODE_PRIVATE)
                .getString("userObject", null);
        customer = new Gson().fromJson(userJson, LoginSuccessResponse.class);
        String cartJson = getIntent().getStringExtra("cart");
        cart = new Gson().fromJson(cartJson, Cart.class);
        calculateAndSetPrices();
        placeOrderButton.setOnClickListener(v -> {
            String shippingAddress = shippingAddressInput.getText().toString().trim();
            if (shippingAddress.isEmpty()) {
                Toast.makeText(CheckoutActivity.this, "Please enter a shipping address", Toast.LENGTH_SHORT).show();
            } else {
                placeOrder(shippingAddress);
            }
        });
    }

    // calculate, then set them in the TextViews
    private void calculateAndSetPrices() {
        double subtotal = 0.0;
        List<CartItem> cartItems = cart.getCartItems();
        for (CartItem item : cartItems) {
            subtotal += item.getTotalPrice();
        }
        totalAmount = subtotal + shippingCost;
        subtotalText.setText(String.format("Rs.%.2f", subtotal));
        shippingCostText.setText(String.format("Rs.%.2f", shippingCost));
        totalText.setText(String.format("Rs.%.2f", totalAmount));
    }

    // Place order
    private void placeOrder(String shippingAddress) {
        OrderApi orderApi = ApiClient.getRetrofitInstance().create(OrderApi.class);
        OrderRequest orderRequest = new OrderRequest(customer, cart.getCartItems(), totalAmount, shippingAddress);
        orderApi.createOrder(orderRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // if successful, delete the cart
                    deleteCart(cart.getId());
                } else {
                    Toast.makeText(CheckoutActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //delete the cart after order placement
    private void deleteCart(String cartId) {
        CartApi cartApi = ApiClient.getRetrofitInstance().create(CartApi.class);
        cartApi.deleteCartById(cartId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showOrderSuccessPopup();
                } else {
                    Toast.makeText(CheckoutActivity.this, "Failed to delete cart", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show thre success dialog after the order is placed
    private void showOrderSuccessPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
        View popupView = getLayoutInflater().inflate(R.layout.dialog_order_success, null);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button seeOrderDetailsButton = popupView.findViewById(R.id.see_order_details_button);
        seeOrderDetailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, OrdersActivity.class);
            startActivity(intent);
            dialog.dismiss();
        });
    }
}
