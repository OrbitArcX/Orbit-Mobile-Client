package com.example.orbitmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orbitmobile.adapters.ProductAdapter;
import com.example.orbitmobile.api.CategoryApi;
import com.example.orbitmobile.api.ProductApi;
import com.example.orbitmobile.models.Category;
import com.example.orbitmobile.models.Product;
import com.example.orbitmobile.network.ApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements SortOptionsDialogFragment.OnSortOptionSelectedListener {

    private EditText searchInput;
    private RecyclerView recyclerViewSearchResults;
    private LinearLayout noResultsLayout;
    private Button sortButton;
    private ProductAdapter productAdapter;
    private String currentQuery = "";        // To hold current search query
    private String currentCategoryId = null; // To hold current category id
    private String currentSortBy = "";       // To hold current sorting option
    private boolean isAscending = true;      // Sorting order

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.search_input);
        recyclerViewSearchResults = findViewById(R.id.recycler_view_search_results);
        noResultsLayout = findViewById(R.id.no_results_layout);
        sortButton = findViewById(R.id.sort_button);

        recyclerViewSearchResults.setLayoutManager(new GridLayoutManager(this, 2));

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        // Handle sorting button click to show sort options
        sortButton.setOnClickListener(v -> showSortOptions());

        // Trigger search when user clicks the search button on the keyboard
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            currentQuery = searchInput.getText().toString().trim();
            if (!currentQuery.isEmpty()) {
                convertCategoryNameToIdAndSearch(currentQuery);
            } else {
                searchProducts(currentQuery, currentCategoryId, currentSortBy, isAscending);
            }
            return false;
        });

        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Highlight the Search tab when in SearchActivity
        bottomNavigationView.setSelectedItemId(R.id.nav_search);  // Add this line

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    startActivity(new Intent(SearchActivity.this, HomeActivity.class));
                    break;
                case R.id.nav_search:
                    // Already in SearchActivity, no action needed
                    break;
                case R.id.nav_orders:
                    startActivity(new Intent(SearchActivity.this, OrdersActivity.class));
                    break;
                case R.id.nav_profile:
                    startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
                    break;
            }
            return true;
        });
    }

    // Convert category name to category ID, then search
    private void convertCategoryNameToIdAndSearch(String query) {
        CategoryApi categoryApi = ApiClient.getRetrofitInstance().create(CategoryApi.class);
        categoryApi.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    for (Category category : categories) {
                        if (category.getName().equalsIgnoreCase(query)) {
                            currentCategoryId = category.getId();
                            break;
                        }
                    }
                }
                searchProducts(currentQuery, currentCategoryId, currentSortBy, isAscending);
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                searchProducts(currentQuery, currentCategoryId, currentSortBy, isAscending);
            }
        });
    }

    // Search products based on query, categoryId, and sorting options
    private void searchProducts(String query, String categoryId, String sortBy, boolean isAscending) {
        ProductApi productApi = ApiClient.getRetrofitInstance().create(ProductApi.class);
        productApi.searchProducts(query, null, null, null, categoryId, null, null, sortBy, isAscending).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    if (products.isEmpty()) {
                        noResultsLayout.setVisibility(View.VISIBLE);
                        recyclerViewSearchResults.setVisibility(View.GONE);
                    } else {
                        noResultsLayout.setVisibility(View.GONE);
                        recyclerViewSearchResults.setVisibility(View.VISIBLE);
                        productAdapter = new ProductAdapter(products, SearchActivity.this);
                        recyclerViewSearchResults.setAdapter(productAdapter);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }

    // Show sorting options dialog
    private void showSortOptions() {
        SortOptionsDialogFragment dialogFragment = new SortOptionsDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "SortOptionsDialog");
    }

    @Override
    public void onSortOptionSelected(String sortBy, boolean isAscending) {
        currentSortBy = sortBy;
        this.isAscending = isAscending;
        searchProducts(currentQuery, currentCategoryId, currentSortBy, isAscending);
    }
}
