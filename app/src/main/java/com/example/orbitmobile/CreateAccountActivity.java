package com.example.orbitmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orbitmobile.api.UserApi;
import com.example.orbitmobile.models.CreateAccountRequest;
import com.example.orbitmobile.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, passwordInput;
    private Button continueButton;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Initialize UI elements
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        continueButton = findViewById(R.id.continue_button);
        backButton = findViewById(R.id.back_button);

        // Back button listener
        backButton.setOnClickListener(v -> finish());

        // Continue button listener (creates the account)
        continueButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(CreateAccountActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                createAccount(name, email, password);
            }
        });
    }

    private void createAccount(String name, String email, String password) {
        UserApi userApi = ApiClient.getRetrofitInstance().create(UserApi.class);

        // Create the request object
        CreateAccountRequest createAccountRequest = new CreateAccountRequest(email, name, password, "Customer", true);

        // Send the request to create a new account
        userApi.createAccount(createAccountRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && response.code() == 201) {
                    // Account created successfully, redirect to login
                    Toast.makeText(CreateAccountActivity.this, "Request sent successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();  // Finish the current activity
                } else {
                    // Account creation failed
                    Toast.makeText(CreateAccountActivity.this, "Failed to send Request. Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Network failure or request error
                Toast.makeText(CreateAccountActivity.this, "Error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
