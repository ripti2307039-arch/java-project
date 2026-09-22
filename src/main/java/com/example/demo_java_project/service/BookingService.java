package com.example.demo_java_project.service;

import com.example.demo_java_project.dao.BookingDAO;
import com.example.demo_java_project.model.Booking;
import com.example.demo_java_project.model.BookingStatus;

import java.util.List;
import java.util.Optional;

public class BookingService {

    private final BookingDAO bookingDAO;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
    }

    public List<Booking> getBookingsForUser(int userId) {
        return bookingDAO.findByUserId(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.findAll();
    }

    public Optional<Booking> getBookingById(int id) {
        return bookingDAO.findById(id);
    }

    public boolean hasConflict(int resourceId, String startTime, String endTime) {
        return bookingDAO.hasConflict(resourceId, startTime, endTime);
    }

    public int createPendingBooking(int userId, int resourceId, String startTime, String endTime) {
        Booking booking = new Booking(userId, resourceId, startTime, endTime);
        return bookingDAO.createBooking(booking);
    }

    public boolean confirmBooking(int bookingId) {
        return bookingDAO.updateStatus(bookingId, BookingStatus.CONFIRMED);
    }

    public boolean rejectBooking(int bookingId) {
        return bookingDAO.updateStatus(bookingId, BookingStatus.REJECTED);
    }

    public boolean cancelBooking(int bookingId) {
        return bookingDAO.updateStatus(bookingId, BookingStatus.CANCELLED);
    }

    public boolean deleteBooking(int id) {
        return bookingDAO.deleteBooking(id);
    }
}