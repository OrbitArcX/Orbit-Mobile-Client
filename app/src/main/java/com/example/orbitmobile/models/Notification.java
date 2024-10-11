package com.example.orbitmobile.models;

public class Notification {

    private String id;
    private String title;
    private String body;
    private boolean seenStatus;
    private String createdAt;

    public Notification(String id, String title, String body, boolean seenStatus, String createdAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.seenStatus = seenStatus;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public boolean isSeenStatus() {
        return seenStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
