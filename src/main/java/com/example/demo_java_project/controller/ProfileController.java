package com.example.demo_java_project.controller;

import com.example.demo_java_project.SlotSyncApplication;
import com.example.demo_java_project.dao.UserDAO;
import com.example.demo_java_project.model.Notification;
import com.example.demo_java_project.model.User;
import com.example.demo_java_project.service.AuthService;
import com.example.demo_java_project.service.NotificationService;
import com.example.demo_java_project.session.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    @FXML
    private VBox notificationsContainer;

    @FXML
    private Label notificationCountLabel;

    private final UserDAO userDAO = new UserDAO();
    private final AuthService authService = new AuthService();
    private final NotificationService notificationService = new NotificationService();

    private static final DateTimeFormatter DB_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);

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

        loadNotifications();
    }

    private void loadNotifications() {
        notificationsContainer.getChildren().clear();

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        List<Notification> notifications = new ArrayList<>(
                notificationService.getNotificationsForUser(currentUser.getId()));
        notifications.sort((a, b) -> Integer.compare(b.getId(), a.getId()));

        long unreadCount = notifications.stream().filter(n -> !n.isRead()).count();
        notificationCountLabel.setText(unreadCount + " unread");

        if (notifications.isEmpty()) {
            Label emptyLabel = new Label("No notifications yet. Use Sync Notifications on the Dashboard to fetch some.");
            emptyLabel.setWrapText(true);
            emptyLabel.setStyle("-fx-text-fill: #9c9ab8; -fx-font-size: 13px;");
            notificationsContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Notification notification : notifications) {
            notificationsContainer.getChildren().add(buildNotificationCard(notification));
        }
    }

    private VBox buildNotificationCard(Notification notification) {
        boolean unread = !notification.isRead();

        VBox card = new VBox(8);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle(unread
                ? "-fx-background-color: #202040; -fx-background-radius: 12; -fx-border-color: #7c5cff; -fx-border-radius: 12;"
                : "-fx-background-color: #1b1b2f; -fx-background-radius: 12; -fx-border-color: #2a2a45; -fx-border-radius: 12;");

        Label messageLabel = new Label(notification.getMessage());
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setStyle(unread
                ? "-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold;"
                : "-fx-text-fill: #9c9ab8; -fx-font-size: 14px;");
        HBox.setHgrow(messageLabel, Priority.ALWAYS);

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        if (unread) {
            Label newBadge = new Label("NEW");
            newBadge.setStyle("-fx-background-color: #7c5cff; -fx-text-fill: #ffffff; -fx-font-size: 10px; "
                    + "-fx-font-weight: bold; -fx-padding: 2 8 2 8; -fx-background-radius: 10;");
            topRow.getChildren().add(newBadge);
        }
        topRow.getChildren().add(messageLabel);

        Label timeLabel = new Label(formatCreatedAt(notification.getCreatedAt()));
        timeLabel.setStyle("-fx-text-fill: #7f7ca0; -fx-font-size: 11px;");

        HBox bottomRow = new HBox(10);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        bottomRow.getChildren().addAll(timeLabel, spacer);

        if (unread) {
            Button markReadButton = new Button("Mark as read");
            markReadButton.getStyleClass().add("book-now-button");
            markReadButton.setOnAction(event -> {
                if (notificationService.markAsRead(notification.getId())) {
                    loadNotifications();
                }
            });
            bottomRow.getChildren().add(markReadButton);
        }

        card.getChildren().addAll(topRow, bottomRow);
        return card;
    }

    private String formatCreatedAt(String createdAt) {
        if (createdAt == null || createdAt.isBlank()) {
            return "";
        }
        try {
            LocalDateTime utcTime = LocalDateTime.parse(createdAt, DB_TIME_FORMAT);
            ZonedDateTime localTime = utcTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault());
            return localTime.format(DISPLAY_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            return createdAt;
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