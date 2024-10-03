package com.example.orbitmobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.orbitmobile.api.ProductApi;
import com.example.orbitmobile.models.Product;
import com.example.orbitmobile.network.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private ProductApi productApi; // for testing remove later
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);



        // Initialize the API client
        productApi = ApiClient.getRetrofitInstance().create(ProductApi.class);

        // Fetch products from the backend
        getProductsFromBackend();
    }


    private void getProductsFromBackend() {
            productApi.getProducts().enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                    if (response.isSuccessful()) {
                        List<Product> productList = response.body();
                        Log.d("InIf", "Product list is null");

                        // Log the product list for debugging
                        if (productList != null) {
                            for (Product product : productList) {
                                Log.d("ProductList", "Product Name: " + product.getName() + ", Price: " + product.getPrice());
                            }
                        } else {
                            Log.d("ProductList", "Product list is null");
                        }

                        // Handle the retrieved products (e.g., display them in a RecyclerView)
                    } else {
                        Toast.makeText(HomeActivity.this, "Failed to fetch products", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Product>> call, Throwable t) {
                    Toast.makeText(HomeActivity.this, "Error11: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });




//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }
}