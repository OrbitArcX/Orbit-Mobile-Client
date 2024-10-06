package com.example.orbitmobile.api;

import com.example.orbitmobile.models.CartRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CartApi {

    // POST request to add items to the cart
    @POST("order/cart")
    Call<Void> addToCart(@Body CartRequest cartRequest);
}
