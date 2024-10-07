package com.example.orbitmobile.api;

import com.example.orbitmobile.models.CreateAccountRequest;
import com.example.orbitmobile.models.LoginRequest;
import com.example.orbitmobile.models.LoginSuccessResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi {

    // Reference : https://square.github.io/retrofit/

    @POST("user/login")
    Call<LoginSuccessResponse> login(@Body LoginRequest loginRequest);  // Use LoginSuccessResponse for 200 OK

    @POST("user")
    Call<Void> createAccount(@Body CreateAccountRequest createAccountRequest);  // Send the request to create a new account

    @PUT("user/{id}")
    Call<Void> updateUser(@Path("id") String id, @Body LoginSuccessResponse user);

}
