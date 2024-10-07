package com.example.orbitmobile;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.orbitmobile.api.UserApi;
import com.example.orbitmobile.models.LoginSuccessResponse;
import com.example.orbitmobile.network.ApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileName, profileEmail;
    private LoginSuccessResponse user;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI elements
        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);
        View updateNameContainer = findViewById(R.id.update_name_container);
        View deactivateAccountContainer = findViewById(R.id.deactivate_account_container);
        View signOutContainer = findViewById(R.id.sign_out_container);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load user object from shared preferences
        user = getUserObject();
        if (user != null) {
            profileName.setText(user.getName());
            profileEmail.setText(user.getEmail());
        }

        // Update Name functionality
        updateNameContainer.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setTitle("Update Name");
            final View customLayout = getLayoutInflater().inflate(R.layout.dialog_update_name, null);
            builder.setView(customLayout);

            builder.setPositiveButton("Update", (dialog, which) -> {
                TextView nameInput = customLayout.findViewById(R.id.name_input);
                String updatedName = nameInput.getText().toString().trim();
                if (!updatedName.isEmpty()) {
                    user.setName(updatedName);
                    updateUserProfile(user);  // Update profile via API
                    profileName.setText(updatedName);
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        });

        // Deactivate Account functionality
        deactivateAccountContainer.setOnClickListener(v -> {
            // Call the function to ask for the password and proceed with deactivation
            askForPasswordAndDeactivate(user);
        });

        // Sign Out functionality with confirmation
        signOutContainer.setOnClickListener(v -> {
            new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Clear user object from shared preferences and redirect to login page
                        getSharedPreferences("OrbitPrefs", MODE_PRIVATE).edit().remove("userObject").apply();
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Set up Bottom Navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    finish();
                    break;
                case R.id.nav_search:
                    startActivity(new Intent(ProfileActivity.this, SearchActivity.class));
                    finish();
                    break;
                case R.id.nav_orders:
                    startActivity(new Intent(ProfileActivity.this, OrdersActivity.class));
                    finish();
                    break;
                case R.id.nav_profile:
                    // Already in ProfileActivity
                    break;
            }
            return true;
        });

        // Highlight the Profile item in the navigation bar
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }

    // Retrieve user object from shared preferences
    private LoginSuccessResponse getUserObject() {
        SharedPreferences sharedPreferences = getSharedPreferences("OrbitPrefs", MODE_PRIVATE);
        String userJson = sharedPreferences.getString("userObject", null);
        if (userJson != null) {
            return gson.fromJson(userJson, LoginSuccessResponse.class);
        }
        return null;
    }

    // Save user object to shared preferences
    private void saveUserObject(LoginSuccessResponse user) {
        SharedPreferences sharedPreferences = getSharedPreferences("OrbitPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userObject", gson.toJson(user));
        editor.apply();
    }

    // Update user profile via API
    private void updateUserProfile(LoginSuccessResponse user) {
        UserApi userApi = ApiClient.getRetrofitInstance().create(UserApi.class);
        userApi.updateUser(user.getId(), user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    saveUserObject(user);  // Save updated user object
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to update profile. Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error updating profile. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Deactivate account via API
    private void deactivateAccount(LoginSuccessResponse user) {
        UserApi userApi = ApiClient.getRetrofitInstance().create(UserApi.class);

        // Set the status to false before making the API call
        user.setStatus(false);

        userApi.updateUser(user.getId(), user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Clear user object and redirect to login page after deactivating
                    getSharedPreferences("OrbitPrefs", MODE_PRIVATE).edit().remove("userObject").apply();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to deactivate account. Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error deactivating account. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Ask for password before deactivating the account
    private void askForPasswordAndDeactivate(LoginSuccessResponse user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Enter Password to Deactivate");

        // Custom layout for password input
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_password_input, null);
        builder.setView(customLayout);

        builder.setPositiveButton("Deactivate", (dialog, which) -> {
            TextView passwordInput = customLayout.findViewById(R.id.password_input);
            String enteredPassword = passwordInput.getText().toString().trim();

            if (!enteredPassword.isEmpty()) {
                user.setPassword(enteredPassword);
                deactivateAccount(user);  // Proceed with deactivation
            } else {
                Toast.makeText(ProfileActivity.this, "Password is required.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
