package com.example.demo_java_project.dao;

import com.example.demo_java_project.database.DatabaseConnection;
import com.example.demo_java_project.model.Booking;
import com.example.demo_java_project.model.BookingStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class BookingDAO {

    public int createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, resource_id, start_time, end_time, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getResourceId());
            stmt.setString(3, booking.getStartTime());
            stmt.setString(4, booking.getEndTime());
            stmt.setString(5, booking.getStatus().name());

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println(" Error creating booking: " + e.getMessage());
        }

        return -1;
    }


    public boolean hasConflict(int resourceId, String startTime, String endTime) {
        String sql = """
            SELECT COUNT(*) AS conflict_count
            FROM bookings
            WHERE resource_id = ?
              AND status = 'CONFIRMED'
              AND start_time < ?
              AND end_time > ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, resourceId);
            stmt.setString(2, endTime);
            stmt.setString(3, startTime);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("conflict_count") > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println(" Error checking booking conflict: " + e.getMessage());
        }

        // If something went wrong, we play it safe and assume there IS a conflict,
        // so we never accidentally double-book due to an error.
        return true;
    }

    public Optional<Booking> findById(int id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToBooking(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println(" Error finding booking by id: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Returns all bookings made by a specific user — used for "My Bookings" page.
     */
    public List<Booking> findByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY start_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapRowToBooking(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching bookings for user: " + e.getMessage());
        }

        return bookings;
    }

    /**
     * Returns ALL bookings — used by the Admin panel.
     */
    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY start_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                bookings.add(mapRowToBooking(rs));
            }

        } catch (SQLException e) {
            System.err.println(" Error fetching all bookings: " + e.getMessage());
        }

        return bookings;
    }

    /**
     * Updates just the status of a booking (e.g. PENDING -> CONFIRMED, or -> REJECTED).
     * This will be called by BookingTask during the concurrency-safe booking process.
     */
    public boolean updateStatus(int bookingId, BookingStatus newStatus) {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus.name());
            stmt.setInt(2, bookingId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating booking status: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBooking(int id) {
        String sql = "DELETE FROM bookings WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println(" Error deleting booking: " + e.getMessage());
            return false;
        }
    }

    private Booking mapRowToBooking(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("resource_id"),
                rs.getString("start_time"),
                rs.getString("end_time"),
                BookingStatus.valueOf(rs.getString("status")),
                rs.getString("created_at")
        );
    }
}