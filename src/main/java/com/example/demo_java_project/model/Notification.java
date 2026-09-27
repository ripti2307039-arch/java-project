package com.example.demo_java_project.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Notification extends BaseEntity {

    @JsonProperty("user_id")
    private int userId;

    private String message;

    @JsonProperty("is_read")
    private boolean isRead;

    public Notification() {
    }

    public Notification(int id, int userId, String message, boolean isRead, String createdAt) {
        super(id, createdAt);
        this.userId = userId;
        this.message = message;
        this.isRead = isRead;
    }

    // Convenience constructor for creating a NEW notification
    public Notification(int userId, String message) {
        this.userId = userId;
        this.message = message;
        this.isRead = false;
    }

    // ---- Getters and Setters ----

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

    @Override
    public String getSummary() {
        return (isRead ? "[Read] " : "[Unread] ") + message;
    }

    @Override
    public String toString() {
        return "Notification{id=" + id + ", userId=" + userId + ", message='" + message + "', isRead=" + isRead + "}";
    }
}