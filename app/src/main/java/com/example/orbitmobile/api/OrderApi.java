package com.example.orbitmobile.api;

import com.example.orbitmobile.models.Order;
import com.example.orbitmobile.models.OrderRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrderApi {

    // Create a new order
    @POST("order")
    Call<Void> createOrder(@Body OrderRequest orderRequest);

    @GET("order/customer/{customerId}")
    Call<List<Order>> getOrdersByCustomerId(@Path("customerId") String customerId);
}
