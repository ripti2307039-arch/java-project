package com.example.demo_java_project.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Notification {

    private int id;

    @JsonProperty("user_id")
    private int userId;

    private String message;

    @JsonProperty("is_read")
    private boolean isRead;

    @JsonProperty("created_at")
    private String createdAt;

    public Notification() {
    }

    public Notification(int id, int userId, String message, boolean isRead, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Convenience constructor for creating a NEW notification
    public Notification(int userId, String message) {
        this.userId = userId;
        this.message = message;
        this.isRead = false;
    }

    // ---- Getters and Setters ----

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Notification{id=" + id + ", userId=" + userId + ", message='" + message + "', isRead=" + isRead + "}";
    }
}