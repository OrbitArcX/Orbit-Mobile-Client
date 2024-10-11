package com.example.orbitmobile.api;

import com.example.orbitmobile.models.RatingRequest;
import com.example.orbitmobile.models.RatingResponse;
import com.example.orbitmobile.models.Review;
import com.example.orbitmobile.models.Vendor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface VendorApi {

    // Reference : https://square.github.io/retrofit/
    @GET("/api/user/{vendorId}")
    Call<Vendor> getVendorById(@Path("vendorId") String vendorId);
    // get ratings by id
    @GET("rating/vendor/{vendorId}")
    Call<List<Review>> getVendorReviews(@Path("vendorId") String vendorId);

    @POST("/api/rating")
    Call<RatingResponse> submitRating(@Body RatingRequest ratingRequest);
}
