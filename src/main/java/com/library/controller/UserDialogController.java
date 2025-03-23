package com.library.controller;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.library.model.User;
import com.library.util.PasswordUtil;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class UserDialogController {
    
    public static Optional<User> showAddDialog() {
        // Create the dialog
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Enter user details");
        
        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Create form fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        ComboBox<User.UserType> userTypeCombo = new ComboBox<>();
        userTypeCombo.setItems(FXCollections.observableArrayList(User.UserType.values()));
        userTypeCombo.setValue(User.UserType.MEMBER);
        
        ComboBox<User.UserStatus> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList(User.UserStatus.values()));
        statusCombo.setValue(User.UserStatus.ACTIVE);
        
        // Add fields to grid
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("User Type:"), 0, 2);
        grid.add(userTypeCombo, 1, 2);
        grid.add(new Label("Status:"), 0, 3);
        grid.add(statusCombo, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the username field by default
        usernameField.requestFocus();
        
        // Convert the result to a user when the OK button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                // Validate input
                if (usernameField.getText().trim().isEmpty() || passwordField.getText().isEmpty()) {
                    return null;
                }
                
                User user = new User();
                user.setId(UUID.randomUUID().toString());
                user.setUsername(usernameField.getText().trim());
                user.setPassword(PasswordUtil.hashPassword(passwordField.getText()));
                user.setUserType(userTypeCombo.getValue());
                user.setStatus(statusCombo.getValue());
                user.setCreatedAt(LocalDateTime.now());
                
                return user;
            }
            return null;
        });
        
        return dialog.showAndWait();
    }
    
    public static Optional<User> showEditDialog(User user) {
        // Create the dialog
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user details");
        
        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Create form fields
        TextField usernameField = new TextField(user.getUsername());
        
        ComboBox<User.UserType> userTypeCombo = new ComboBox<>();
        userTypeCombo.setItems(FXCollections.observableArrayList(User.UserType.values()));
        userTypeCombo.setValue(user.getUserType());
        
        ComboBox<User.UserStatus> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList(User.UserStatus.values()));
        statusCombo.setValue(user.getStatus());
        
        // Add fields to grid
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("User Type:"), 0, 1);
        grid.add(userTypeCombo, 1, 1);
        grid.add(new Label("Status:"), 0, 2);
        grid.add(statusCombo, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the username field by default
        usernameField.requestFocus();
        
        // Convert the result to a user when the OK button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                // Validate input
                if (usernameField.getText().trim().isEmpty()) {
                    return null;
                }
                
                User updatedUser = new User();
                updatedUser.setId(user.getId());
                updatedUser.setUsername(usernameField.getText().trim());
                updatedUser.setPassword(user.getPassword()); // Keep existing password
                updatedUser.setUserType(userTypeCombo.getValue());
                updatedUser.setStatus(statusCombo.getValue());
                updatedUser.setCreatedAt(user.getCreatedAt());
               
                return updatedUser;
            }
            return null;
        });
        
        return dialog.showAndWait();
    }
}