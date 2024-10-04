package com.example.orbitmobile.api;



import com.example.orbitmobile.models.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryApi {

    // Get a list of products
    @GET("products/category")
    Call<List<Category>> getCategories();
}
