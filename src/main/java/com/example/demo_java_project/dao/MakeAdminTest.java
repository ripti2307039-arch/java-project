package com.example.demo_java_project.dao;

import com.example.demo_java_project.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MakeAdminTest {
    public static void main(String[] args) {
        String sql = "UPDATE users SET role = 'ADMIN' WHERE full_name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "raian");
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}