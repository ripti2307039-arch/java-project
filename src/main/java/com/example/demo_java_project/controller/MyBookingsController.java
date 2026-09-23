package com.example.demo_java_project.controller;

import com.example.demo_java_project.SlotSyncApplication;
import com.example.demo_java_project.model.Booking;
import com.example.demo_java_project.model.Resource;
import com.example.demo_java_project.model.User;
import com.example.demo_java_project.service.AuthService;
import com.example.demo_java_project.service.BookingService;
import com.example.demo_java_project.service.ResourceService;
import com.example.demo_java_project.session.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MyBookingsController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button adminButton;

    @FXML
    private VBox bookingsContainer;

    private final BookingService bookingService = new BookingService();
    private final ResourceService resourceService = new ResourceService();
    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            welcomeLabel.setText("Hello, " + currentUser.getFullName());
            adminButton.setVisible(currentUser.isAdmin());
            adminButton.setManaged(currentUser.isAdmin());
        }

        loadBookings();
    }

    private void loadBookings() {
        bookingsContainer.getChildren().clear();

        User currentUser = SessionManager.getInstance().getCurrentUser();
        List<Booking> bookings = bookingService.getBookingsForUser(currentUser.getId());

        if (bookings.isEmpty()) {
            Label emptyLabel = new Label("You have not made any bookings yet");
            emptyLabel.getStyleClass().add("empty-state-label");
            bookingsContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Booking booking : bookings) {
            bookingsContainer.getChildren().add(buildBookingRow(booking));
        }
    }

    private HBox buildBookingRow(Booking booking) {
        HBox row = new HBox(16);
        row.getStyleClass().add("resource-card");
        row.setPadding(new Insets(18));

        Optional<Resource> resourceOptional = resourceService.getResourceById(booking.getResourceId());
        String resourceName = resourceOptional.map(Resource::getName).orElse("Unknown Resource");

        VBox infoBox = new VBox(4);
        Label nameLabel = new Label(resourceName);
        nameLabel.getStyleClass().add("resource-name");
        Label timeLabel = new Label(booking.getStartTime() + " to " + booking.getEndTime());
        timeLabel.getStyleClass().add("resource-location");
        Label statusLabel = new Label(booking.getStatus().name());
        statusLabel.getStyleClass().add("resource-type");
        infoBox.getChildren().addAll(nameLabel, timeLabel, statusLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("book-now-button");
        cancelButton.setOnAction(event -> {
            bookingService.cancelBooking(booking.getId());
            loadBookings();
        });

        row.getChildren().addAll(infoBox, spacer, cancelButton);
        return row;
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
        loadBookings();
    }

    @FXML
    private void handleProfileNav() {
        try {
            SlotSyncApplication.setRoot("profile", 1100, 700);
        } catch (IOException e) {
            e.printStackTrace();
        }
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