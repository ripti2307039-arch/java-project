package com.example.demo_java_project.service;

import com.example.demo_java_project.database.DatabaseConnection;

public class ServiceTest {
    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();

        AuthService authService = new AuthService();

        AuthService.AuthResult signupResult = authService.signup("Service Test", "servicetest@example.com", "password123");
        System.out.println("Signup success: " + signupResult.isSuccess());
        if (!signupResult.isSuccess()) {
            System.out.println("Signup error: " + signupResult.getErrorMessage());
        }

        AuthService.AuthResult loginResult = authService.login("servicetest@example.com", "password123");
        System.out.println("Login success: " + loginResult.isSuccess());
        if (loginResult.isSuccess()) {
            System.out.println("Logged in as: " + loginResult.getUser().getFullName());
        }

        AuthService.AuthResult wrongLoginResult = authService.login("servicetest@example.com", "wrongpassword");
        System.out.println("Wrong password login success: " + wrongLoginResult.isSuccess());
        System.out.println("Wrong password error: " + wrongLoginResult.getErrorMessage());
    }
}