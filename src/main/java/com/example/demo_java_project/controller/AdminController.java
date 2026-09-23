package com.example.demo_java_project.controller;

import com.example.demo_java_project.SlotSyncApplication;
import com.example.demo_java_project.dao.UserDAO;
import com.example.demo_java_project.model.Booking;
import com.example.demo_java_project.model.Resource;
import com.example.demo_java_project.model.User;
import com.example.demo_java_project.service.AuthService;
import com.example.demo_java_project.service.BookingService;
import com.example.demo_java_project.service.ResourceService;
import com.example.demo_java_project.session.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.layout.Priority;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AdminController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label welcomeLabel;

    @FXML
    private VBox usersContainer;

    @FXML
    private VBox resourcesContainer;

    @FXML
    private VBox bookingsContainer;

    @FXML
    private TextField newResourceName;

    @FXML
    private TextField newResourceType;

    @FXML
    private TextField newResourceLocation;

    @FXML
    private TextField newResourceDescription;

    private final UserDAO userDAO = new UserDAO();
    private final ResourceService resourceService = new ResourceService();
    private final BookingService bookingService = new BookingService();
    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Hello, " + currentUser.getFullName());
        }

        loadUsers();
        loadResources();
        loadBookings();
    }

    private void loadUsers() {
        usersContainer.getChildren().clear();
        List<User> users = userDAO.findAll();

        if (users.isEmpty()) {
            usersContainer.getChildren().add(emptyLabel("No users found"));
            return;
        }

        for (User user : users) {
            HBox row = new HBox(16);
            row.getStyleClass().add("admin-row");

            VBox infoBox = new VBox(4);
            Label nameLabel = new Label(user.getFullName() + " (" + user.getRole() + ")");
            nameLabel.getStyleClass().add("resource-name");
            Label emailLabel = new Label(user.getEmail());
            emailLabel.getStyleClass().add("resource-location");
            infoBox.getChildren().addAll(nameLabel, emailLabel);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button deleteButton = new Button("Delete");
            deleteButton.getStyleClass().add("delete-button");
            deleteButton.setOnAction(e -> {
                userDAO.deleteUser(user.getId());
                loadUsers();
            });

            row.getChildren().addAll(infoBox, spacer, deleteButton);
            usersContainer.getChildren().add(row);
        }
    }

    private void loadResources() {
        resourcesContainer.getChildren().clear();
        List<Resource> resources = resourceService.getAllResources();

        if (resources.isEmpty()) {
            resourcesContainer.getChildren().add(emptyLabel("No resources found"));
            return;
        }

        for (Resource resource : resources) {
            HBox row = new HBox(16);
            row.getStyleClass().add("admin-row");

            VBox infoBox = new VBox(4);
            Label nameLabel = new Label(resource.getName());
            nameLabel.getStyleClass().add("resource-name");
            Label typeLabel = new Label(resource.getType() + " - " + resource.getLocation());
            typeLabel.getStyleClass().add("resource-location");
            infoBox.getChildren().addAll(nameLabel, typeLabel);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button deleteButton = new Button("Delete");
            deleteButton.getStyleClass().add("delete-button");
            deleteButton.setOnAction(e -> {
                resourceService.deleteResource(resource.getId());
                loadResources();
            });

            row.getChildren().addAll(infoBox, spacer, deleteButton);
            resourcesContainer.getChildren().add(row);
        }
    }

    private void loadBookings() {
        bookingsContainer.getChildren().clear();
        List<Booking> bookings = bookingService.getAllBookings();

        if (bookings.isEmpty()) {
            bookingsContainer.getChildren().add(emptyLabel("No bookings found"));
            return;
        }

        for (Booking booking : bookings) {
            HBox row = new HBox(16);
            row.getStyleClass().add("admin-row");

            Optional<Resource> resourceOptional = resourceService.getResourceById(booking.getResourceId());
            String resourceName = resourceOptional.map(Resource::getName).orElse("Unknown Resource");

            Optional<User> userOptional = userDAO.findById(booking.getUserId());
            String userName = userOptional.map(User::getFullName).orElse("Unknown User");

            VBox infoBox = new VBox(4);
            Label nameLabel = new Label(resourceName + " - " + userName);
            nameLabel.getStyleClass().add("resource-name");
            Label timeLabel = new Label(booking.getStartTime() + " to " + booking.getEndTime() + " (" + booking.getStatus() + ")");
            timeLabel.getStyleClass().add("resource-location");
            infoBox.getChildren().addAll(nameLabel, timeLabel);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button deleteButton = new Button("Delete");
            deleteButton.getStyleClass().add("delete-button");
            deleteButton.setOnAction(e -> {
                bookingService.deleteBooking(booking.getId());
                loadBookings();
            });

            row.getChildren().addAll(infoBox, spacer, deleteButton);
            bookingsContainer.getChildren().add(row);
        }
    }

    private Label emptyLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("empty-state-label");
        return label;
    }

    @FXML
    private void handleAddResource() {
        String name = newResourceName.getText();
        String type = newResourceType.getText();
        String location = newResourceLocation.getText();
        String description = newResourceDescription.getText();

        boolean added = resourceService.addResource(name, type, location, description);

        if (added) {
            newResourceName.clear();
            newResourceType.clear();
            newResourceLocation.clear();
            newResourceDescription.clear();
            loadResources();
        }
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
        try {
            SlotSyncApplication.setRoot("profile", 1100, 700);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdminNav() {
        loadUsers();
        loadResources();
        loadBookings();
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