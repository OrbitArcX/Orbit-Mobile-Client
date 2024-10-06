package com.example.orbitmobile.models;

import java.util.List;

public class OrderRequest {

    private LoginSuccessResponse customer;  // This is the customer object
    private List<CartItem> cartItems;
    private double totalPrice;
    private String address;

    public OrderRequest(LoginSuccessResponse customer, List<CartItem> cartItems, double totalPrice, String address) {
        this.customer = customer;
        this.cartItems = cartItems;
        this.totalPrice = totalPrice;
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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
