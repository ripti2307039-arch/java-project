package com.example.demo_java_project.dao;

import com.example.demo_java_project.database.DatabaseConnection;
import com.example.demo_java_project.model.Resource;
import com.example.demo_java_project.model.User;
import com.example.demo_java_project.security.PasswordUtil;

import java.util.List;

public class DaoTest {
    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();

        UserDAO userDAO = new UserDAO();
        ResourceDAO resourceDAO = new ResourceDAO();

        User testUser = new User("Test User", "test2@example.com", PasswordUtil.hashPassword("mypassword123"), "USER");
        int newUserId = userDAO.createUser(testUser);
        System.out.println("Created user with id: " + newUserId);

        Resource testResource = new Resource("Conference Room A", "Meeting Room", "3rd Floor", "Has a projector");
        int newResourceId = resourceDAO.createResource(testResource);
        System.out.println("Created resource with id: " + newResourceId);

        List<User> allUsers = userDAO.findAll();
        System.out.println("All users in database:");
        for (User u : allUsers) {
            System.out.println("  " + u);
        }

        List<Resource> allResources = resourceDAO.findAll();
        System.out.println("All resources in database:");
        for (Resource r : allResources) {
            System.out.println("  " + r);
        }

        boolean matches = PasswordUtil.verifyPassword("mypassword123", testUser.getPasswordHash());
        System.out.println("Password matches: " + matches);
    }
}