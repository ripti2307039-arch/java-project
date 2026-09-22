package com.example.demo_java_project.service;

import com.example.demo_java_project.dao.UserDAO;
import com.example.demo_java_project.model.User;
import com.example.demo_java_project.security.PasswordUtil;
import com.example.demo_java_project.session.SessionManager;

import java.util.Optional;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public AuthResult signup(String fullName, String email, String plainPassword) {
        if (fullName == null || fullName.isBlank()) {
            return AuthResult.failure("Full name cannot be empty");
        }

        if (email == null || email.isBlank()) {
            return AuthResult.failure("Email cannot be empty");
        }

        if (plainPassword == null || plainPassword.length() < 6) {
            return AuthResult.failure("Password must be at least 6 characters");
        }

        Optional<User> existingUser = userDAO.findByEmail(email);
        if (existingUser.isPresent()) {
            return AuthResult.failure("An account with this email already exists");
        }

        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        User newUser = new User(fullName, email, hashedPassword, "USER");
        int newUserId = userDAO.createUser(newUser);

        if (newUserId == -1) {
            return AuthResult.failure("Failed to create account, please try again");
        }

        newUser.setId(newUserId);
        return AuthResult.success(newUser);
    }

    public AuthResult login(String email, String plainPassword) {
        if (email == null || email.isBlank() || plainPassword == null || plainPassword.isBlank()) {
            return AuthResult.failure("Email and password cannot be empty");
        }

        Optional<User> userOptional = userDAO.findByEmail(email);
        if (userOptional.isEmpty()) {
            return AuthResult.failure("Invalid email or password");
        }

        User user = userOptional.get();
        boolean passwordMatches = PasswordUtil.verifyPassword(plainPassword, user.getPasswordHash());

        if (!passwordMatches) {
            return AuthResult.failure("Invalid email or password");
        }

        SessionManager.getInstance().login(user);
        return AuthResult.success(user);
    }

    public void logout() {
        SessionManager.getInstance().logout();
    }

    public static class AuthResult {
        private final boolean success;
        private final String errorMessage;
        private final User user;

        private AuthResult(boolean success, String errorMessage, User user) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.user = user;
        }

        public static AuthResult success(User user) {
            return new AuthResult(true, null, user);
        }

        public static AuthResult failure(String errorMessage) {
            return new AuthResult(false, errorMessage, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public User getUser() {
            return user;
        }
    }
}