package com.example.demo_java_project.concurrency;

import com.example.demo_java_project.dao.BookingDAO;
import com.example.demo_java_project.model.Booking;
import com.example.demo_java_project.model.BookingStatus;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

public class BookingTask implements Callable<BookingTask.BookingResult> {

    private final int userId;
    private final int resourceId;
    private final String startTime;
    private final String endTime;
    private final BookingDAO bookingDAO;

    public BookingTask(int userId, int resourceId, String startTime, String endTime) {
        this.userId = userId;
        this.resourceId = resourceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingDAO = new BookingDAO();
    }

    @Override
    public BookingResult call() {
        ReentrantLock lock = BookingLockManager.getInstance().getLockForResource(resourceId);

        lock.lock();
        try {
            boolean conflict = bookingDAO.hasConflict(resourceId, startTime, endTime);

            if (conflict) {
                return BookingResult.failure("This slot is already booked. Please choose a different time");
            }

            Booking booking = new Booking(userId, resourceId, startTime, endTime);
            booking.setStatus(BookingStatus.CONFIRMED);
            int newBookingId = bookingDAO.createBooking(booking);

            if (newBookingId == -1) {
                return BookingResult.failure("Failed to save booking. Please try again");
            }

            return BookingResult.success(newBookingId);

        } finally {
            lock.unlock();
        }
    }

    public static class BookingResult {
        private final boolean success;
        private final String errorMessage;
        private final int bookingId;

        private BookingResult(boolean success, String errorMessage, int bookingId) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.bookingId = bookingId;
        }

        public static BookingResult success(int bookingId) {
            return new BookingResult(true, null, bookingId);
        }

        public static BookingResult failure(String errorMessage) {
            return new BookingResult(false, errorMessage, -1);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public int getBookingId() {
            return bookingId;
        }
    }
}