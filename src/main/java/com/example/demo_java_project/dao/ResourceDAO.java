package com.example.demo_java_project.dao;

import com.example.demo_java_project.database.DatabaseConnection;
import com.example.demo_java_project.model.Resource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles all database operations related to the "resources" table.
 */
public class ResourceDAO {

    public int createResource(Resource resource) {
        String sql = "INSERT INTO resources (name, type, location, description) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, resource.getName());
            stmt.setString(2, resource.getType());
            stmt.setString(3, resource.getLocation());
            stmt.setString(4, resource.getDescription());

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println(" Error creating resource: " + e.getMessage());
        }

        return -1;
    }

    public Optional<Resource> findById(int id) {
        String sql = "SELECT * FROM resources WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToResource(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding resource by id: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Returns all resources — used to populate the Dashboard.
     */
    public List<Resource> findAll() {
        List<Resource> resources = new ArrayList<>();
        String sql = "SELECT * FROM resources ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                resources.add(mapRowToResource(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all resources: " + e.getMessage());
        }

        return resources;
    }

    public boolean updateResource(Resource resource) {
        String sql = "UPDATE resources SET name = ?, type = ?, location = ?, description = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, resource.getName());
            stmt.setString(2, resource.getType());
            stmt.setString(3, resource.getLocation());
            stmt.setString(4, resource.getDescription());
            stmt.setInt(5, resource.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating resource: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteResource(int id) {
        String sql = "DELETE FROM resources WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println(" Error deleting resource: " + e.getMessage());
            return false;
        }
    }

    private Resource mapRowToResource(ResultSet rs) throws SQLException {
        return new Resource(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getString("location"),
                rs.getString("description"),
                rs.getString("created_at")
        );
    }
}