package com.example.demo_java_project;

import com.example.demo_java_project.database.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SlotSyncApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        DatabaseConnection.initializeDatabase();
        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(
                SlotSyncApplication.class.getResource("/com.example.demo_java_project/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 650);

        stage.setTitle("SlotSync");
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxmlFileName, int width, int height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                SlotSyncApplication.class.getResource("/com.example.demo_java_project/fxml/" + fxmlFileName + ".fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}