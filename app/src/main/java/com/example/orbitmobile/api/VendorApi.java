package com.example.orbitmobile.api;

import com.example.orbitmobile.models.Review;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface VendorApi {

    // Reference : https://square.github.io/retrofit/
    // get ratings by id
    @GET("rating/vendor/{vendorId}")
    Call<List<Review>> getVendorReviews(@Path("vendorId") String vendorId);
}
