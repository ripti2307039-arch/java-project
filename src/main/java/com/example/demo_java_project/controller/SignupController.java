package com.example.demo_java_project.controller;

import com.example.demo_java_project.SlotSyncApplication;
import com.example.demo_java_project.service.AuthService;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.IOException;

public class SignupController {

    @FXML
    private HBox rootPane;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button signupButton;

    @FXML
    private Label messageLabel;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Hyperlink loginLink;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        rootPane.setOpacity(0);
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(700), rootPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
        hideMessage();
    }

    @FXML
    private void handleSignup() {
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        hideMessage();

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        setLoading(true);

        Task<AuthService.AuthResult> signupTask = new Task<>() {
            @Override
            protected AuthService.AuthResult call() {
                return authService.signup(fullName, email, password);
            }
        };

        signupTask.setOnSucceeded(event -> {
            setLoading(false);
            AuthService.AuthResult result = signupTask.getValue();
            if (result.isSuccess()) {
                showSuccess("Account created. You can now log in");
            } else {
                showError(result.getErrorMessage());
            }
        });

        signupTask.setOnFailed(event -> {
            setLoading(false);
            showError("Something went wrong. Please try again");
        });

        Thread signupThread = new Thread(signupTask);
        signupThread.setDaemon(true);
        signupThread.start();
    }

    @FXML
    private void handleLoginLink() {
        try {
            SlotSyncApplication.setRoot("login", 1000, 650);
        } catch (IOException e) {
            showError("Unable to load login screen");
        }
    }

    private void setLoading(boolean loading) {
        signupButton.setDisable(loading);
        loadingIndicator.setVisible(loading);
        loadingIndicator.setManaged(loading);
    }

    private void showError(String message) {
        messageLabel.getStyleClass().remove("success-label");
        if (!messageLabel.getStyleClass().contains("error-label")) {
            messageLabel.getStyleClass().add("error-label");
        }
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }

    private void showSuccess(String message) {
        messageLabel.getStyleClass().remove("error-label");
        if (!messageLabel.getStyleClass().contains("success-label")) {
            messageLabel.getStyleClass().add("success-label");
        }
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }

    private void hideMessage() {
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
    }
}