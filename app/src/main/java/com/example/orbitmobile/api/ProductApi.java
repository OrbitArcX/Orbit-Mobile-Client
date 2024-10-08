package com.example.orbitmobile.api;

import com.example.orbitmobile.models.Product;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductApi {

    // Reference : https://square.github.io/retrofit/

    // Get a list of products
    @GET("products")
    Call<List<Product>> getProducts();

    @GET("products/getby/category/{categoryId}")
    Call<List<Product>> getProductsByCategory(@Path("categoryId") String categoryId);

    @GET("products/search")
    Call<List<Product>> searchProducts(
            @Query("name") String name,
            @Query("minPrice") Integer minPrice,
            @Query("maxPrice") Integer maxPrice,
            @Query("vendorId") String vendorId,
            @Query("categoryId") String categoryId,
            @Query("minRating") Integer minRating,
            @Query("maxRating") Integer maxRating,
            @Query("sortBy") String sortBy,
            @Query("isAscending") Boolean isAscending
    );
//    // Create a new product
//    @POST("products")
//    Call<Product> createProduct(@Body Product product);
//
//    // Update an existing product
//    @PUT("products/{id}")
//    Call<Product> updateProduct(@Path("id") int productId, @Body Product product);
//
//    // Delete a product
//    @DELETE("products/{id}")
//    Call<Void> deleteProduct(@Path("id") int productId);
}

