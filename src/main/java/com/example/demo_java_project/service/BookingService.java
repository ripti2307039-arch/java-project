package com.example.demo_java_project.service;

import com.example.demo_java_project.concurrency.BookingTask;
import com.example.demo_java_project.dao.BookingDAO;
import com.example.demo_java_project.model.Booking;
import com.example.demo_java_project.model.BookingStatus;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BookingService {

    private final BookingDAO bookingDAO;
    private final ExecutorService bookingExecutor;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.bookingExecutor = Executors.newFixedThreadPool(4);
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

    public BookingTask.BookingResult submitBookingRequest(int userId, int resourceId, String startTime, String endTime) {
        BookingTask task = new BookingTask(userId, resourceId, startTime, endTime);
        Future<BookingTask.BookingResult> future = bookingExecutor.submit(task);

        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return BookingTask.BookingResult.failure("Booking was interrupted. Please try again");
        } catch (ExecutionException e) {
            return BookingTask.BookingResult.failure("An unexpected error occurred while booking");
        }
    }

    public boolean cancelBooking(int bookingId) {
        return bookingDAO.updateStatus(bookingId, BookingStatus.CANCELLED);
    }

    public boolean deleteBooking(int id) {
        return bookingDAO.deleteBooking(id);
    }

    public void shutdown() {
        bookingExecutor.shutdown();
    }
}