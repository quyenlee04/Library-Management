package com.library;

import com.library.view.ViewManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set the primary stage in ViewManager
        ViewManager.getInstance().setPrimaryStage(primaryStage);
        
        // Load the login view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        
        // Set up the scene
        Scene scene = new Scene(root);
        if (getClass().getResource("/css/login.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());
        }
        // Configure the stage
        primaryStage.setTitle("Library Management System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
