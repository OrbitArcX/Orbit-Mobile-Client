package com.example.orbitmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orbitmobile.adapters.CategoryAdapter;
import com.example.orbitmobile.models.Category;

import java.util.ArrayList;
import java.util.List;

public class ShopByCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCategories;
    private CategoryAdapter categoryAdapter;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_by_category);

        recyclerViewCategories = findViewById(R.id.recycler_view_categories);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));

        backButton = findViewById(R.id.back_button);  // Find the back button

        // Handle the back button click
        backButton.setOnClickListener(v -> {
            finish();  // Close the activity and return to the previous screen
        });
        // Retrieve categories from intent
        List<Category> categories = getIntent().getParcelableArrayListExtra("categories");

        if (categories != null) {
            categoryAdapter = new CategoryAdapter(categories, this, category -> {
                // Navigate to CategoryItemsActivity with the selected category
                Intent intent = new Intent(ShopByCategoryActivity.this, CategoryItemsActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }, R.layout.shop_category_item);  // Pass the new layout resource
            recyclerViewCategories.setAdapter(categoryAdapter);
        }
    }
}
