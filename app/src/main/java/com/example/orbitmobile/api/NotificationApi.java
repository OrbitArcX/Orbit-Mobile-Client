package com.example.orbitmobile.api;

import com.example.orbitmobile.models.Notification;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NotificationApi {

    // API to get notifications by user ID
    @GET("/api/notification/user/{userId}")
    Call<List<Notification>> getNotificationsByUserId(@Path("userId") String userId);
}
