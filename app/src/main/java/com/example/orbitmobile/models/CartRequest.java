package com.example.orbitmobile.models;

import java.util.List;

public class CartRequest {
    private LoginSuccessResponse customer;
    private double cartPrice;
    private String address;
    private List<CartItem> cartItems;

    public CartRequest(LoginSuccessResponse customer, double cartPrice, String address, List<CartItem> cartItems) {
        this.customer = customer;
        this.cartPrice = cartPrice;
        this.address = address;
        this.cartItems = cartItems;
    }

    // Getters and setters
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

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
}

