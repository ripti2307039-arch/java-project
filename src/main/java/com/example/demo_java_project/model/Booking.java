package com.example.demo_java_project.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Booking {

    private int id;

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("resource_id")
    private int resourceId;

    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("end_time")
    private String endTime;

    private BookingStatus status;

    @JsonProperty("created_at")
    private String createdAt;

    public Booking() {
    }

    public Booking(int id, int userId, int resourceId, String startTime,
                   String endTime, BookingStatus status, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.resourceId = resourceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Convenience constructor for creating a NEW booking request
    public Booking(int userId, int resourceId, String startTime, String endTime) {
        this.userId = userId;
        this.resourceId = resourceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = BookingStatus.PENDING;
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

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Booking{id=" + id + ", userId=" + userId + ", resourceId=" + resourceId +
                ", startTime='" + startTime + "', endTime='" + endTime + "', status=" + status + "}";
    }
}