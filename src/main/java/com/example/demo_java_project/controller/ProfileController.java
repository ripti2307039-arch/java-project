package com.example.demo_java_project.controller;

import com.example.demo_java_project.SlotSyncApplication;
import com.example.demo_java_project.model.User;
import com.example.demo_java_project.service.AuthService;
import com.example.demo_java_project.dao.UserDAO;
import com.example.demo_java_project.session.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class ProfileController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button adminButton;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private Label messageLabel;

    private final UserDAO userDAO = new UserDAO();
    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            welcomeLabel.setText("Hello, " + currentUser.getFullName());
            adminButton.setVisible(currentUser.isAdmin());
            adminButton.setManaged(currentUser.isAdmin());
            fullNameField.setText(currentUser.getFullName());
            emailField.setText(currentUser.getEmail());
        }
    }

    @FXML
    private void handleSave() {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        String newName = fullNameField.getText();
        String newEmail = emailField.getText();

        if (newName.isBlank() || newEmail.isBlank()) {
            showMessage("Name and email cannot be empty");
            return;
        }

        currentUser.setFullName(newName);
        currentUser.setEmail(newEmail);

        boolean updated = userDAO.updateUser(currentUser);

        if (updated) {
            welcomeLabel.setText("Hello, " + currentUser.getFullName());
            showMessage("Profile updated successfully");
        } else {
            showMessage("Failed to update profile");
        }
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }

    @FXML
    private void handleDashboardNav() {
        try {
            SlotSyncApplication.setRoot("dashboard", 1100, 700);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMyBookingsNav() {
        try {
            SlotSyncApplication.setRoot("my-bookings", 1100, 700);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfileNav() {
    }

    @FXML
    private void handleAdminNav() {
        try {
            SlotSyncApplication.setRoot("admin-dashboard", 1100, 700);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        authService.logout();
        try {
            SlotSyncApplication.setRoot("login", 1000, 650);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}