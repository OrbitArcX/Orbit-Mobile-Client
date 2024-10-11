package com.example.orbitmobile.models;

import java.util.List;

public class OrderRequest {

    private LoginSuccessResponse customer;  // customer obj
    private List<CartItem> cartItems;
    private double cartPrice;
    private String address;

    public OrderRequest(LoginSuccessResponse customer, List<CartItem> cartItems, double cartPrice, String address) {
        this.customer = customer;
        this.cartItems = cartItems;
        this.cartPrice = cartPrice;
        this.address = address;
    }

    public LoginSuccessResponse getCustomer() {
        return customer;
    }

    public void setCustomer(LoginSuccessResponse customer) {
        this.customer = customer;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
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
