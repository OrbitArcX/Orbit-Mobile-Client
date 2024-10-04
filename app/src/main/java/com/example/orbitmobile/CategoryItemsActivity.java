package com.example.orbitmobile;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orbitmobile.adapters.ProductAdapter;
import com.example.orbitmobile.api.ProductApi;
import com.example.orbitmobile.models.Category;
import com.example.orbitmobile.models.Product;
import com.example.orbitmobile.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class CategoryItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private Category category;
    private TextView categoryTitle;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_items);

        category = getIntent().getParcelableExtra("category");
        categoryTitle = findViewById(R.id.category_items_title);  // Find the title TextView
        backButton = findViewById(R.id.back_button);  // Find the back button

        // Handle the back button click
        backButton.setOnClickListener(v -> {
            finish();  // Close the activity and return to the previous screen
        });

        recyclerViewProducts = findViewById(R.id.recycler_view_products);
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));

        loadProductsByCategory(category);
    }

    private void loadProductsByCategory(Category category) {
        ProductApi productApi = ApiClient.getRetrofitInstance().create(ProductApi.class);
        productApi.getProductsByCategory(category.getId()).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();

                    // Dynamically update the title with category name and product count
                    String titleText = category.getName() + " (" + products.size() + ")";
                    categoryTitle.setText(titleText);  // Update the TextView

                    // Set up the product adapter with the fetched products
                    productAdapter = new ProductAdapter(products, CategoryItemsActivity.this);
                    recyclerViewProducts.setAdapter(productAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }
}
