package com.example.orbitmobile.models;

import java.util.List;

public class Cart {
    private String id;
    private List<CartItem> cartItems;
    private LoginSuccessResponse customer; // customer obj
    private double cartPrice;
    private String address;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public LoginSuccessResponse getCustomer() {
        return customer;
    }

    public void setCustomer(LoginSuccessResponse customer) {
        this.customer = customer;
    }

    public double getCartPrice() {
        return cartPrice;
    }

    public void setCartPrice(double cartPrice) {
        this.cartPrice = cartPrice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
