package com.example.orbitmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orbitmobile.api.UserApi;
import com.example.orbitmobile.models.LoginRequest;
import com.example.orbitmobile.models.LoginSuccessResponse;
import com.example.orbitmobile.network.ApiClient;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private TextView errorMessage;
    private Button continueButton;
    private TextView createAccountLink;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        errorMessage = findViewById(R.id.error_message);
        continueButton = findViewById(R.id.continue_button);
        createAccountLink = findViewById(R.id.create_one);

        // Sign-in button click listener
        continueButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                errorMessage.setText("Please fill in both fields");
                errorMessage.setVisibility(View.VISIBLE);
            } else {
                signInUser(email, password);
            }
        });

        // Create account link listener
        createAccountLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        });
    }

    // Sign-in API call
    private void signInUser(String email, String password) {
        UserApi userApi = ApiClient.getRetrofitInstance().create(UserApi.class);

        // Create login request with email and password
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Send login request
        userApi.login(loginRequest).enqueue(new Callback<LoginSuccessResponse>() {
            @Override
            public void onResponse(Call<LoginSuccessResponse> call, Response<LoginSuccessResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Successful login, process response
                    LoginSuccessResponse loginResponse = response.body();
                    saveUserObject(loginResponse);  // Save the entire user object
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Handle 404 (Not Found) and 400 (Bad Request) errors
                    if (response.code() == 404) {
                        errorMessage.setText("User not found. Please check your email.");
                    } else if (response.code() == 400) {
                        errorMessage.setText("Incorrect password or account not approved.");
                    } else {
                        errorMessage.setText("Login failed. Please try again.");
                    }
                    errorMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<LoginSuccessResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Save the entire user object to SharedPreferences
    private void saveUserObject(LoginSuccessResponse loginResponse) {
        SharedPreferences sharedPreferences = getSharedPreferences("OrbitPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convert the loginResponse object to a JSON string
        String userJson = gson.toJson(loginResponse);

        // Save the JSON string in SharedPreferences
        editor.putString("userObject", userJson);
        editor.apply();
    }

    // Method to retrieve the saved user object from SharedPreferences
    private LoginSuccessResponse getUserObject() {
        SharedPreferences sharedPreferences = getSharedPreferences("OrbitPrefs", MODE_PRIVATE);

        // Get the JSON string from SharedPreferences
        String userJson = sharedPreferences.getString("userObject", null);

        // Convert the JSON string back to a LoginSuccessResponse object
        if (userJson != null) {
            return gson.fromJson(userJson, LoginSuccessResponse.class);
        }
        return null;
    }
}
