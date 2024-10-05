package com.example.orbitmobile.models;

public class LoginResponse {
    private String status;  // success or error
    private String message; // the Error massges are sent from backend "User does not exist", "Incorrect password", "Waiting for account activation"
    private String customerId;

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
