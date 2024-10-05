package com.example.orbitmobile.api;

import com.example.orbitmobile.models.LoginRequest;
import com.example.orbitmobile.models.LoginSuccessResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {
    @POST("user/login")
    Call<LoginSuccessResponse> login(@Body LoginRequest loginRequest);  // Use LoginSuccessResponse for 200 OK
}
