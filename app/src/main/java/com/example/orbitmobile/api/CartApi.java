package com.example.orbitmobile.api;

import com.example.orbitmobile.models.Cart;
import com.example.orbitmobile.models.CartRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CartApi {

    // POST request to create a cart and add items
    @POST("order/cart")
    Call<Void> addToCart(@Body CartRequest cartRequest);

    // GET cart details of that customer
    @GET("order/cart/customer/{customerId}")
    Call<Cart> getCartByCustomerId(@Path("customerId") String customerId);

    // PUT request to update the cart
    @PUT("order/cart/{cartId}")
    Call<Cart> updateCart(@Path("cartId") String cartId, @Body Cart cart);

    // DELETE cart by ID
    @DELETE("order/cart/{cartId}")
    Call<Void> deleteCartById(@Path("cartId") String cartId);
}
