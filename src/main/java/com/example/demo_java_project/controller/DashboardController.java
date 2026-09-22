package com.example.demo_java_project.controller;

import com.example.demo_java_project.SlotSyncApplication;
import com.example.demo_java_project.model.Resource;
import com.example.demo_java_project.model.User;
import com.example.demo_java_project.service.AuthService;
import com.example.demo_java_project.service.ResourceService;
import com.example.demo_java_project.session.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

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
        iconBadge.setAlignment(javafx.geometry.Pos.CENTER);

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

        card.getChildren().addAll(topRow, locationLabel, descriptionLabel, bookButton);
        card.setPadding(new Insets(20));

        return card;
    }

    @FXML
    private void handleDashboardNav() {
        loadResources();
    }

    @FXML
    private void handleMyBookingsNav() {
    }

    @FXML
    private void handleProfileNav() {
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