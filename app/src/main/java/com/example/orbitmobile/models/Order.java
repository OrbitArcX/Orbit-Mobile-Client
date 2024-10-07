package com.example.orbitmobile.models;

import java.util.List;

public class Order {
    private String id;
    private List<OrderItem> orderItems;
    private LoginSuccessResponse customer;  // Use LoginSuccessResponse for customer data
    private double orderPrice;
    private boolean cancelRequest;
    private String cancelReason;
    private String csrCancelReason;
    private String address;
    private String status;
    private String createdAt;
    private String updatedAt;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public LoginSuccessResponse getCustomer() {
        return customer;
    }

    public void setCustomer(LoginSuccessResponse customer) {
        this.customer = customer;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public boolean isCancelRequest() {
        return cancelRequest;
    }

    public void setCancelRequest(boolean cancelRequest) {
        this.cancelRequest = cancelRequest;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getCsrCancelReason() {
        return csrCancelReason;
    }

    public void setCsrCancelReason(String csrCancelReason) {
        this.csrCancelReason = csrCancelReason;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
