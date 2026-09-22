package com.example.demo_java_project.dao;

import com.example.demo_java_project.database.DatabaseConnection;
import com.example.demo_java_project.model.Resource;
import com.example.demo_java_project.model.User;

import java.util.List;

/**
 * TEMPORARY test class to verify DAO methods work correctly.
 * We will delete this once the real UI is calling these DAOs.
 */
public class DaoTest {
    public static void main(String[] args) {
        // Make sure tables exist first
        DatabaseConnection.initializeDatabase();

        UserDAO userDAO = new UserDAO();
        ResourceDAO resourceDAO = new ResourceDAO();

        // Try creating a test user (plain text password for now —
        // we will replace this with a real BCrypt hash in Phase 5)
        User testUser = new User("Test User", "test@example.com", "temp_password_hash", "USER");
        int newUserId = userDAO.createUser(testUser);
        System.out.println("Created user with id: " + newUserId);

        // Try creating a test resource
        Resource testResource = new Resource("Conference Room A", "Meeting Room", "3rd Floor", "Has a projector");
        int newResourceId = resourceDAO.createResource(testResource);
        System.out.println("Created resource with id: " + newResourceId);

        // Fetch all users and print them
        List<User> allUsers = userDAO.findAll();
        System.out.println("All users in database:");
        for (User u : allUsers) {
            System.out.println("  " + u);
        }

        // Fetch all resources and print them
        List<Resource> allResources = resourceDAO.findAll();
        System.out.println("All resources in database:");
        for (Resource r : allResources) {
            System.out.println("  " + r);
        }
    }
}