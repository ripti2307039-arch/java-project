package com.example.demo_java_project.controller;

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

public class LoginController {

    @FXML
    private HBox rootPane;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Hyperlink signupLink;

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
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        hideMessage();
        setLoading(true);

        Task<AuthService.AuthResult> loginTask = new Task<>() {
            @Override
            protected AuthService.AuthResult call() {
                return authService.login(email, password);
            }
        };

        loginTask.setOnSucceeded(event -> {
            setLoading(false);
            AuthService.AuthResult result = loginTask.getValue();
            if (result.isSuccess()) {
                showSuccess("Login successful. Welcome " + result.getUser().getFullName());
            } else {
                showError(result.getErrorMessage());
            }
        });

        loginTask.setOnFailed(event -> {
            setLoading(false);
            showError("Something went wrong. Please try again");
        });

        Thread loginThread = new Thread(loginTask);
        loginThread.setDaemon(true);
        loginThread.start();
    }

    @FXML
    private void handleSignupLink() {
        showError("Signup screen will be available in the next phase");
    }

    private void setLoading(boolean loading) {
        loginButton.setDisable(loading);
        loadingIndicator.setVisible(loading);
        loadingIndicator.setManaged(loading);
    }

    private void showError(String message) {
        errorLabel.getStyleClass().remove("success-label");
        if (!errorLabel.getStyleClass().contains("error-label")) {
            errorLabel.getStyleClass().add("error-label");
        }
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void showSuccess(String message) {
        errorLabel.getStyleClass().remove("error-label");
        if (!errorLabel.getStyleClass().contains("success-label")) {
            errorLabel.getStyleClass().add("success-label");
        }
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideMessage() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}