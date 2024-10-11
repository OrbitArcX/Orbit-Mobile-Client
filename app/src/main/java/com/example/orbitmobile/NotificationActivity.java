package com.example.orbitmobile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orbitmobile.adapters.NotificationAdapter;
import com.example.orbitmobile.models.LoginSuccessResponse;
import com.example.orbitmobile.models.Notification;
import com.example.orbitmobile.network.ApiClient;
import com.example.orbitmobile.api.NotificationApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView notificationRecyclerView;
    private NotificationAdapter notificationAdapter;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Initialize RecyclerView
        notificationRecyclerView = findViewById(R.id.notification_recycler_view);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        backButton = findViewById(R.id.back_button);
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Load notifications from API
        loadNotifications();
    }

    private void loadNotifications() {
//        String userId = "67010aaf05351c513e7a4f7e";

        String userJson = getSharedPreferences("OrbitPrefs", MODE_PRIVATE)
                .getString("userObject", null);
        LoginSuccessResponse user = new Gson().fromJson(userJson, LoginSuccessResponse.class);
        String userId = user.getId();

        NotificationApi notificationApi = ApiClient.getRetrofitInstance().create(NotificationApi.class);
        notificationApi.getNotificationsByUserId(userId).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(@NonNull Call<List<Notification>> call, @NonNull Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Notification> notifications = response.body();
                    if (notifications.isEmpty()) {
                        Toast.makeText(NotificationActivity.this, "No notifications available", Toast.LENGTH_SHORT).show();
                    } else {
                        // Set up adapter with the notifications
                        notificationAdapter = new NotificationAdapter(notifications, NotificationActivity.this);
                        notificationRecyclerView.setAdapter(notificationAdapter);
                    }
                } else {
                    Toast.makeText(NotificationActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Notification>> call, @NonNull Throwable t) {
                Toast.makeText(NotificationActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
