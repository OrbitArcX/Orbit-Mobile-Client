package com.example.orbitmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

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
    private List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerViewCategories = findViewById(R.id.recycler_view_categories);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTopSelling = findViewById(R.id.recycler_view_top_selling);
        recyclerViewTopSelling.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewNewIn = findViewById(R.id.recycler_view_new_in);
        recyclerViewNewIn.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // TextView - See All Categories
        TextView seeAllCategories = findViewById(R.id.see_all_categories);
        seeAllCategories.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ShopByCategoryActivity.class);
            intent.putParcelableArrayListExtra("categories", new ArrayList<>(categories));
            startActivity(intent);
        });

        loadCategories();
        loadTopSelling();
        loadProducts();

        // cart button
        ImageButton cartButton = findViewById(R.id.cart_button);
        cartButton.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, CartActivity.class));
        });

        // profile button
        findViewById(R.id.profile_picture).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    // Already in Home
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

    // load categories from the API
    private void loadCategories() {
        CategoryApi categoryApi = ApiClient.getRetrofitInstance().create(CategoryApi.class);
        categoryApi.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();

                    // Pass the correct layout (using category_item.xml)
                    categoryAdapter = new CategoryAdapter(categories, HomeActivity.this, category -> {
                        Intent intent = new Intent(HomeActivity.this, CategoryItemsActivity.class);
                        intent.putExtra("category", category);
                        startActivity(intent);
                    }, R.layout.category_item);  // Use category_item.xml

                    recyclerViewCategories.setAdapter(categoryAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                // need to set error message or log
            }
        });
    }


    // load top-selling
    private void loadTopSelling() {
        ProductApi productApi = ApiClient.getRetrofitInstance().create(ProductApi.class);
        productApi.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();

                    // vendor rating is above 4.0 (Top-Selling)
                    List<Product> topSellingProducts = new ArrayList<>();
                    for (Product product : products) {
                        if (product.getVendor().getRating() >= 0.0) {
                            topSellingProducts.add(product);
                        }
                    }
                    topSellingAdapter = new ProductAdapter(topSellingProducts, HomeActivity.this);
                    recyclerViewTopSelling.setAdapter(topSellingAdapter);
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                // need to set error message or log
            }
        });
    }
    // load products from the API
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
                // need to set error message or log
            }
        });
    }
}
