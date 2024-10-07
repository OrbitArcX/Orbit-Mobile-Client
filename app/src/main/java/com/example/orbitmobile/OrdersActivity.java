package com.example.orbitmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orbitmobile.adapters.OrdersAdapter;
import com.example.orbitmobile.api.OrderApi;
import com.example.orbitmobile.models.Order;
import com.example.orbitmobile.models.LoginSuccessResponse;
import com.example.orbitmobile.network.ApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView ordersRecyclerView;
    private OrdersAdapter ordersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        // Initialize RecyclerView
        ordersRecyclerView = findViewById(R.id.orders_recycler_view);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load orders from API
        loadOrders();

        // Bottom Navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_orders);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    // Already in HomeActivity
                    startActivity(new Intent(OrdersActivity.this, HomeActivity.class));
                    return true;
                case R.id.nav_search:
                    startActivity(new Intent(OrdersActivity.this, SearchActivity.class));
                    return true;
                case R.id.nav_orders:
                    return true;
                case R.id.nav_profile:
                    startActivity(new Intent(OrdersActivity.this, ProfileActivity.class));
                    return true;
                default:
                    return false;
            }
        });
    }

    private void loadOrders() {
        // Retrieve user from SharedPreferences
        String userJson = getSharedPreferences("OrbitPrefs", MODE_PRIVATE)
                .getString("userObject", null);
        LoginSuccessResponse user = new Gson().fromJson(userJson, LoginSuccessResponse.class);
        String customerId = user.getId();

        // Call the API to get customer orders
        OrderApi orderApi = ApiClient.getRetrofitInstance().create(OrderApi.class);
        orderApi.getOrdersByCustomerId(customerId).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body();
                    if (orders.isEmpty()) {
                        // Show "No Orders" layout
                        setContentView(R.layout.activity_orders_empty);
                    } else {
                        // Load orders into the RecyclerView
                        ordersAdapter = new OrdersAdapter(orders, OrdersActivity.this);
                        ordersRecyclerView.setAdapter(ordersAdapter);
                    }
                } else {
                    Toast.makeText(OrdersActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                Toast.makeText(OrdersActivity.this, "Network error. Try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // When order is clicked, navigate to the order details activity
    public void onOrderClick(Order order) {
        Intent intent = new Intent(this, OrderDetailsActivity.class);
        intent.putExtra("order", new Gson().toJson(order));
        startActivity(intent);
    }
}
