package com.example.demo_java_project.model;

public enum BookingStatus {
    PENDING,     // Booking request created, waiting for confirmation
    CONFIRMED,   // Booking successfully locked in (no conflict found)
    CANCELLED,   // Booking was cancelled by the user or admin
    REJECTED     // Booking failed because the slot was already taken
}