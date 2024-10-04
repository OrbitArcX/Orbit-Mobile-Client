package com.example.orbitmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orbitmobile.adapters.CategoryAdapter;
import com.example.orbitmobile.adapters.ProductAdapter;
import com.example.orbitmobile.api.CategoryApi;
import com.example.orbitmobile.api.ProductApi;
import com.example.orbitmobile.models.Category;
import com.example.orbitmobile.models.Product;
import com.example.orbitmobile.network.ApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCategories;
    private RecyclerView recyclerViewTopSelling;
    private RecyclerView recyclerViewNewIn;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter topSellingAdapter;
    private ProductAdapter newInAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Setting up RecyclerViews
        recyclerViewCategories = findViewById(R.id.recycler_view_categories);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerViewTopSelling = findViewById(R.id.recycler_view_top_selling);
        recyclerViewTopSelling.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerViewNewIn = findViewById(R.id.recycler_view_new_in);
        recyclerViewNewIn.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Load data from the API
        loadCategories();
        loadTopSelling();
        loadProducts();

        // Set up cart button
        ImageButton cartButton = findViewById(R.id.cart_button);
        cartButton.setOnClickListener(v -> {
            // Navigate to Cart Activity
            startActivity(new Intent(HomeActivity.this, CartActivity.class));
        });

        // Set up profile picture button
        findViewById(R.id.profile_picture).setOnClickListener(v -> {
            // Navigate to Profile Activity
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        // Bottom Navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    // Already in HomeActivity
                    return true;
                case R.id.nav_search:
                    startActivity(new Intent(HomeActivity.this, SearchActivity.class));
                    return true;
                case R.id.nav_orders:
                    startActivity(new Intent(HomeActivity.this, OrdersActivity.class));
                    return true;
                case R.id.nav_profile:
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    return true;
                default:
                    return false;
            }
        });
    }

    // Method to load categories from the API
    private void loadCategories() {
        CategoryApi categoryApi = ApiClient.getRetrofitInstance().create(CategoryApi.class);
        categoryApi.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    categoryAdapter = new CategoryAdapter(categories, HomeActivity.this);
                    recyclerViewCategories.setAdapter(categoryAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                // Handle failure (show error message or log)
            }
        });
    }


    // Method to load top-selling products from the API
    private void loadTopSelling() {
        ProductApi productApi = ApiClient.getRetrofitInstance().create(ProductApi.class);
        productApi.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();

                    // Filter the products where vendor rating is above 4.0 (Top-Selling)
                    List<Product> topSellingProducts = new ArrayList<>();
                    for (Product product : products) {
                        if (product.getVendor().getRating() >= 0.0) {
                            topSellingProducts.add(product);
                        }
                    }

                    // Set the filtered top-selling products to the adapter
                    topSellingAdapter = new ProductAdapter(topSellingProducts, HomeActivity.this);
                    recyclerViewTopSelling.setAdapter(topSellingAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                // Handle failure (show error message or log)
            }
        });
    }


    // Method to load new-in products from the API
    private void loadProducts() {
        ProductApi productApi = ApiClient.getRetrofitInstance().create(ProductApi.class);
        productApi.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> newInProducts = response.body();
                    newInAdapter = new ProductAdapter(newInProducts, HomeActivity.this);
                    recyclerViewNewIn.setAdapter(newInAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                // Handle failure (show error message or log)
            }
        });
    }
}
