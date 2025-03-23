package com.library.view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewManager {
    private static ViewManager instance;
    private Stage primaryStage;

    private ViewManager() {
        // Private constructor for singleton pattern
    }

    public static ViewManager getInstance() {
        if (instance == null) {
            instance = new ViewManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public void switchToLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());
            
            primaryStage.setTitle("Library Management System - Login");
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void switchToAdminDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_dashboard.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/admin.css").toExternalForm());
            
            primaryStage.setTitle("Library Management System - Admin Dashboard");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void switchToUserManagementView() {
        loadView("/fxml/admin_user_management.fxml", "User Management");
    }
    
    public void switchToRoleManagementView() {
        loadView("/fxml/admin_role_management.fxml", "Role Management");
    }
    
    public void switchToBookManagementView() {
        loadView("/fxml/book_management.fxml", "Book Management");
    }
    
    public void switchToBorrowingManagementView() {
        loadView("/fxml/admin_borrowing_management.fxml", "Borrowing Management");
    }
    
    public void switchToSystemConfigView() {
        loadView("/fxml/admin_system_config.fxml", "System Configuration");
    }
    public void switchToRegistrationView() {
        loadView("/fxml/registration.fxml", "Registration"); 
    }
    
    public void switchToBookInventoryReportView() {
        loadView("/fxml/admin_book_inventory_report.fxml", "Book Inventory Report");
    }
    
    public void switchToBorrowingHistoryReportView() {
        loadView("/fxml/admin_borrowing_history_report.fxml", "Borrowing History Report");
    }
    
    public void switchToOverdueReportView() {
        loadView("/fxml/admin_overdue_report.fxml", "Overdue Books Report");
    }
    
    public void switchToUserActivityReportView() {
        loadView("/fxml/admin_user_activity_report.fxml", "User Activity Report");
    }
    
    public void switchToProcessBorrowingView() {
        loadView("/fxml/process_borrowing.fxml", "Process Borrowing");
    }
    
    public void switchToProcessReturnView() {
        loadView("/fxml/process_return.fxml", "Process Return");
    }
    public void switchToLibrarianView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/librarian_dashboard.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());
            
            primaryStage.setTitle("Library Management System - Librarian Dashboard");
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading librarian view: " + e.getMessage());
        }
    }
    
    public void switchToLibrarianDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/librarian_dashboard.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());
            
            primaryStage.setTitle("Library Management System - Librarian Dashboard");
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading librarian dashboard: " + e.getMessage());
        }
    }
    
    
    public void switchToMemberView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/member_dashboard.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());
            
            primaryStage.setTitle("Library Management System - Member Dashboard");
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading member view: " + e.getMessage());
        }
    }
    private void loadView(String fxmlPath, String title) {
        try {
            // Check if resource exists before attempting to load it
            java.net.URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("Resource not found: " + fxmlPath);
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            
            // Check if CSS resource exists
            java.net.URL cssResource = getClass().getResource("/css/admin.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            } else {
                System.err.println("CSS resource not found: /css/admin.css");
            }
            
            // Use the primary stage instead of creating a new one
            primaryStage.setTitle("Library Management System - " + title);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + fxmlPath + " - " + e.getMessage());
        }
    }
}
