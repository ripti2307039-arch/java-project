package com.example.demo_java_project.controller;

import com.example.demo_java_project.SlotSyncApplication;
import com.example.demo_java_project.concurrency.BookingTask;
import com.example.demo_java_project.model.Resource;
import com.example.demo_java_project.model.User;
import com.example.demo_java_project.service.AuthService;
import com.example.demo_java_project.service.BookingService;
import com.example.demo_java_project.service.NotificationService;
import com.example.demo_java_project.service.ResourceService;
import com.example.demo_java_project.session.SessionManager;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    private Button syncButton;

    @FXML
    private FlowPane resourceContainer;

    private final ResourceService resourceService = new ResourceService();
    private final AuthService authService = new AuthService();
    private final BookingService bookingService = new BookingService();
    private final NotificationService notificationService = new NotificationService();

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

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

    // ---------- Small helpers for the AM/PM time ComboBoxes ----------

    private ComboBox<Integer> buildHourComboBox() {
        ComboBox<Integer> hourBox = new ComboBox<>(
                FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
        hourBox.setPromptText("Hour");
        hourBox.setMaxWidth(Double.MAX_VALUE);
        return hourBox;
    }

    private ComboBox<Integer> buildMinuteComboBox() {
        // 5-minute steps: 00, 05, 10, ... 55
        ComboBox<Integer> minuteBox = new ComboBox<>();
        for (int m = 0; m < 60; m += 5) {
            minuteBox.getItems().add(m);
        }
        minuteBox.setPromptText("Minute");
        minuteBox.setMaxWidth(Double.MAX_VALUE);
        minuteBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Integer value) {
                return value == null ? "" : String.format("%02d", value);
            }

            @Override
            public Integer fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
        });
        return minuteBox;
    }

    private ComboBox<String> buildAmPmComboBox() {
        ComboBox<String> ampmBox = new ComboBox<>(
                FXCollections.observableArrayList("AM", "PM"));
        ampmBox.setPromptText("AM/PM");
        ampmBox.setMaxWidth(Double.MAX_VALUE);
        return ampmBox;
    }

    /**
     * Converts 12-hour (hour 1-12, am/pm) to 24-hour format.
     * 12 AM -> 0, 12 PM -> 12, otherwise straightforward.
     */
    private int to24Hour(int hour12, String amPm) {
        int hour = hour12 % 12; // 12 becomes 0
        if ("PM".equals(amPm)) {
            hour += 12;
        }
        return hour;
    }

    private void openBookingDialog(Resource resource) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Book " + resource.getName());
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/com.example.demo_java_project/css/dashboard.css").toExternalForm());
        dialog.getDialogPane().setStyle("-fx-background-color: #16162a;");

        VBox content = new VBox(12);
        content.setPadding(new Insets(10));

        // ---- Start date ----
        Label startDateLabel = new Label("Start Date");
        startDateLabel.setStyle("-fx-text-fill: #b8b4d6; -fx-font-size: 12px;");
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Select start date");
        startDatePicker.setMaxWidth(Double.MAX_VALUE);
        startDatePicker.setEditable(false);
        startDatePicker.getEditor().setDisable(true);

        // ---- Start time (hour / minute / AM-PM) ----
        Label startTimeLabel = new Label("Start Time");
        startTimeLabel.setStyle("-fx-text-fill: #b8b4d6; -fx-font-size: 12px;");
        ComboBox<Integer> startHourBox = buildHourComboBox();
        ComboBox<Integer> startMinuteBox = buildMinuteComboBox();
        ComboBox<String> startAmPmBox = buildAmPmComboBox();
        HBox startTimeRow = new HBox(8, startHourBox, startMinuteBox, startAmPmBox);
        HBox.setHgrow(startHourBox, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(startMinuteBox, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(startAmPmBox, javafx.scene.layout.Priority.ALWAYS);

        // ---- End date ----
        Label endDateLabel = new Label("End Date");
        endDateLabel.setStyle("-fx-text-fill: #b8b4d6; -fx-font-size: 12px;");
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Select end date");
        endDatePicker.setMaxWidth(Double.MAX_VALUE);
        endDatePicker.setEditable(false);
        endDatePicker.getEditor().setDisable(true);

        // ---- End time (hour / minute / AM-PM) ----
        Label endTimeLabel = new Label("End Time");
        endTimeLabel.setStyle("-fx-text-fill: #b8b4d6; -fx-font-size: 12px;");
        ComboBox<Integer> endHourBox = buildHourComboBox();
        ComboBox<Integer> endMinuteBox = buildMinuteComboBox();
        ComboBox<String> endAmPmBox = buildAmPmComboBox();
        HBox endTimeRow = new HBox(8, endHourBox, endMinuteBox, endAmPmBox);
        HBox.setHgrow(endHourBox, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(endMinuteBox, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(endAmPmBox, javafx.scene.layout.Priority.ALWAYS);

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 12px;");

        content.getChildren().addAll(
                startDateLabel, startDatePicker, startTimeLabel, startTimeRow,
                endDateLabel, endDatePicker, endTimeLabel, endTimeRow,
                statusLabel);
        dialog.getDialogPane().setContent(content);

        ButtonType confirmButtonType = new ButtonType("Confirm Booking", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> null);

        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.addEventFilter(javafx.event.ActionEvent.ACTION, actionEvent -> {

            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            Integer startHour = startHourBox.getValue();
            Integer startMinute = startMinuteBox.getValue();
            String startAmPm = startAmPmBox.getValue();

            Integer endHour = endHourBox.getValue();
            Integer endMinute = endMinuteBox.getValue();
            String endAmPm = endAmPmBox.getValue();

            // ---- Validation ----
            if (startDate == null || endDate == null) {
                statusLabel.setText("Please select both start and end date");
                actionEvent.consume();
                return;
            }

            if (startHour == null || startMinute == null || startAmPm == null) {
                statusLabel.setText("Please select start time (hour, minute, AM/PM)");
                actionEvent.consume();
                return;
            }

            if (endHour == null || endMinute == null || endAmPm == null) {
                statusLabel.setText("Please select end time (hour, minute, AM/PM)");
                actionEvent.consume();
                return;
            }

            LocalTime startTime = LocalTime.of(to24Hour(startHour, startAmPm), startMinute);
            LocalTime endTime = LocalTime.of(to24Hour(endHour, endAmPm), endMinute);

            String start = startDate.format(DATE_FORMAT) + " " + startTime.format(TIME_FORMAT);
            String end = endDate.format(DATE_FORMAT) + " " + endTime.format(TIME_FORMAT);

            if (end.compareTo(start) <= 0) {
                statusLabel.setText("End date/time must be after start date/time");
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

    @FXML
    private void handleSyncNotifications() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        syncButton.setDisable(true);
        syncButton.setText("Syncing...");

        Task<Integer> syncTask = new Task<>() {
            @Override
            protected Integer call() {
                return notificationService.fetchExternalNotifications(currentUser.getId(), 5);
            }
        };

        syncTask.setOnSucceeded(event -> {
            syncButton.setDisable(false);
            syncButton.setText("Sync Notifications");
            int count = syncTask.getValue();
            showAlert(Alert.AlertType.INFORMATION, "Sync Complete", count + " notifications fetched and saved");
        });

        syncTask.setOnFailed(event -> {
            syncButton.setDisable(false);
            syncButton.setText("Sync Notifications");
            showAlert(Alert.AlertType.ERROR, "Sync Failed", "Could not fetch notifications. Check your internet connection");
        });

        Thread syncThread = new Thread(syncTask);
        syncThread.setDaemon(true);
        syncThread.start();
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