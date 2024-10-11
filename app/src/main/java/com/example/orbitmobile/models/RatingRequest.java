package com.example.orbitmobile.models;

public class RatingRequest {
    private float rating;
    private String comment;
    private Vendor vendor;
    private LoginSuccessResponse customer;

    public RatingRequest(float rating, String comment, Vendor vendor, LoginSuccessResponse customer) {
        this.rating = rating;
        this.comment = comment;
        this.vendor = vendor;
        this.customer = customer;
    }

    // Getters and Setters
    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public LoginSuccessResponse getCustomer() {
        return customer;
    }

    public void setCustomer(LoginSuccessResponse customer) {
        this.customer = customer;
    }
}
