package com.example.demo_java_project.controller;

import com.example.demo_java_project.SlotSyncApplication;
import com.example.demo_java_project.concurrency.BookingTask;
import com.example.demo_java_project.model.Resource;
import com.example.demo_java_project.model.User;
import com.example.demo_java_project.service.AuthService;
import com.example.demo_java_project.service.BookingService;
import com.example.demo_java_project.service.ResourceService;
import com.example.demo_java_project.session.SessionManager;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class DashboardController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label resourceCountLabel;

    @FXML
    private Button adminButton;

    @FXML
    private FlowPane resourceContainer;

    private final ResourceService resourceService = new ResourceService();
    private final AuthService authService = new AuthService();
    private final BookingService bookingService = new BookingService();

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            welcomeLabel.setText("Hello, " + currentUser.getFullName());
            adminButton.setVisible(currentUser.isAdmin());
            adminButton.setManaged(currentUser.isAdmin());
        }

        loadResources();
    }

    private void loadResources() {
        resourceContainer.getChildren().clear();

        List<Resource> resources = resourceService.getAllResources();
        resourceCountLabel.setText(resources.size() + " resources");

        if (resources.isEmpty()) {
            Label emptyLabel = new Label("No resources available yet");
            emptyLabel.getStyleClass().add("empty-state-label");
            resourceContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Resource resource : resources) {
            resourceContainer.getChildren().add(buildResourceCard(resource));
        }
    }

    private VBox buildResourceCard(Resource resource) {
        VBox card = new VBox(12);
        card.getStyleClass().add("resource-card");

        HBox topRow = new HBox(12);
        VBox iconBadge = new VBox();
        iconBadge.getStyleClass().add("resource-icon-badge");
        iconBadge.setAlignment(Pos.CENTER);

        Label iconLabel = new Label(resource.getName().substring(0, 1).toUpperCase());
        iconLabel.getStyleClass().add("resource-icon-text");
        iconBadge.getChildren().add(iconLabel);

        VBox nameBox = new VBox(4);
        Label nameLabel = new Label(resource.getName());
        nameLabel.getStyleClass().add("resource-name");
        Label typeLabel = new Label(resource.getType());
        typeLabel.getStyleClass().add("resource-type");
        nameBox.getChildren().addAll(nameLabel, typeLabel);

        topRow.getChildren().addAll(iconBadge, nameBox);

        Label locationLabel = new Label(
                resource.getLocation() != null && !resource.getLocation().isBlank()
                        ? resource.getLocation()
                        : "Location not specified");
        locationLabel.getStyleClass().add("resource-location");

        Label descriptionLabel = new Label(
                resource.getDescription() != null && !resource.getDescription().isBlank()
                        ? resource.getDescription()
                        : "No description available");
        descriptionLabel.getStyleClass().add("resource-description");
        descriptionLabel.setWrapText(true);

        Button bookButton = new Button("Book Now");
        bookButton.getStyleClass().add("book-now-button");
        bookButton.setMaxWidth(Double.MAX_VALUE);
        bookButton.setOnAction(event -> openBookingDialog(resource));

        card.getChildren().addAll(topRow, locationLabel, descriptionLabel, bookButton);
        card.setPadding(new Insets(20));

        return card;
    }

    private void openBookingDialog(Resource resource) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Book " + resource.getName());
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/com.example.demo_java_project/css/dashboard.css").toExternalForm());
        dialog.getDialogPane().setStyle("-fx-background-color: #16162a;");

        VBox content = new VBox(12);
        content.setPadding(new Insets(10));

        Label startLabel = new Label("Start Time (YYYY-MM-DD HH:MM)");
        startLabel.setStyle("-fx-text-fill: #b8b4d6; -fx-font-size: 12px;");
        TextField startField = new TextField();
        startField.setPromptText("2026-01-15 10:00");

        Label endLabel = new Label("End Time (YYYY-MM-DD HH:MM)");
        endLabel.setStyle("-fx-text-fill: #b8b4d6; -fx-font-size: 12px;");
        TextField endField = new TextField();
        endField.setPromptText("2026-01-15 11:00");

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 12px;");

        content.getChildren().addAll(startLabel, startField, endLabel, endField, statusLabel);
        dialog.getDialogPane().setContent(content);

        ButtonType confirmButtonType = new ButtonType("Confirm Booking", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> null);

        Optional<Void> result;
        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.addEventFilter(javafx.event.ActionEvent.ACTION, actionEvent -> {
            String start = startField.getText();
            String end = endField.getText();

            if (start.isBlank() || end.isBlank()) {
                statusLabel.setText("Both start and end time are required");
                actionEvent.consume();
                return;
            }

            actionEvent.consume();
            confirmButton.setDisable(true);
            statusLabel.setStyle("-fx-text-fill: #9c9ab8; -fx-font-size: 12px;");
            statusLabel.setText("Checking availability and booking...");

            User currentUser = SessionManager.getInstance().getCurrentUser();

            Task<BookingTask.BookingResult> task = new Task<>() {
                @Override
                protected BookingTask.BookingResult call() {
                    return bookingService.submitBookingRequest(
                            currentUser.getId(), resource.getId(), start, end);
                }
            };

            task.setOnSucceeded(e -> {
                BookingTask.BookingResult bookingResult = task.getValue();
                confirmButton.setDisable(false);

                if (bookingResult.isSuccess()) {
                    dialog.close();
                    showAlert(Alert.AlertType.INFORMATION, "Booking Confirmed",
                            "Your booking for " + resource.getName() + " has been confirmed");
                } else {
                    statusLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 12px;");
                    statusLabel.setText(bookingResult.getErrorMessage());
                }
            });

            task.setOnFailed(e -> {
                confirmButton.setDisable(false);
                statusLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 12px;");
                statusLabel.setText("Something went wrong. Please try again");
            });

            Thread bookingThread = new Thread(task);
            bookingThread.setDaemon(true);
            bookingThread.start();
        });

        dialog.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleDashboardNav() {
        loadResources();
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
        try {
            SlotSyncApplication.setRoot("profile", 1100, 700);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleAdminNav() {
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