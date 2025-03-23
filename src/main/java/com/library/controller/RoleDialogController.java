package com.library.controller;

import com.library.model.Role;
import com.library.service.RoleService;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class RoleDialogController {
    
    public static Optional<Role> showAddDialog() {
        // Create the dialog
        Dialog<Role> dialog = new Dialog<>();
        dialog.setTitle("Add New Role");
        dialog.setHeaderText("Enter role details");
        
        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Create form fields
        TextField nameField = new TextField();
        nameField.setPromptText("Role name");
        
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Role description");
        
        // Create permissions list
        ListView<String> permissionsList = new ListView<>();
        permissionsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Get all available permissions
        List<String> allPermissions = RoleService.getInstance().getAllPermissions();
        permissionsList.setItems(FXCollections.observableArrayList(allPermissions));
        
        // Add fields to grid
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Permissions:"), 0, 2);
        
        VBox permissionsBox = new VBox(5);
        permissionsBox.getChildren().add(permissionsList);
        grid.add(permissionsBox, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the name field by default
        nameField.requestFocus();
        
        // Convert the result to a role when the OK button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                // Validate input
                if (nameField.getText().trim().isEmpty()) {
                    return null;
                }
                
                Role role = new Role();
                role.setId(UUID.randomUUID().toString());
                role.setName(nameField.getText().trim());
                role.setDescription(descriptionField.getText().trim());
                
                // Get selected permissions
                List<String> selectedPermissions = new ArrayList<>(
                        permissionsList.getSelectionModel().getSelectedItems());
                role.setPermissions(selectedPermissions);
                
                return role;
            }
            return null;
        });
        
        return dialog.showAndWait();
    }
    
    public static Optional<Role> showEditDialog(Role role) {
        // Create the dialog
        Dialog<Role> dialog = new Dialog<>();
        dialog.setTitle("Edit Role");
        dialog.setHeaderText("Edit role details");
        
        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Create form fields
        TextField nameField = new TextField(role.getName());
        TextField descriptionField = new TextField(role.getDescription());
        
        // Create permissions list
        ListView<String> permissionsList = new ListView<>();
        permissionsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Get all available permissions
        List<String> allPermissions = RoleService.getInstance().getAllPermissions();
        permissionsList.setItems(FXCollections.observableArrayList(allPermissions));
        
        // Pre-select existing permissions
        for (String permission : role.getPermissions()) {
            int index = allPermissions.indexOf(permission);
            if (index >= 0) {
                permissionsList.getSelectionModel().select(index);
            }
        }
        
        // Add fields to grid
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Permissions:"), 0, 2);
        
        VBox permissionsBox = new VBox(5);
        permissionsBox.getChildren().add(permissionsList);
        grid.add(permissionsBox, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the name field by default
        nameField.requestFocus();
        
        // Convert the result to a role when the OK button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                // Validate input
                if (nameField.getText().trim().isEmpty()) {
                    return null;
                }
                
                Role updatedRole = new Role();
                updatedRole.setId(role.getId());
                updatedRole.setName(nameField.getText().trim());
                updatedRole.setDescription(descriptionField.getText().trim());
                
                // Get selected permissions
                List<String> selectedPermissions = new ArrayList<>(
                        permissionsList.getSelectionModel().getSelectedItems());
                updatedRole.setPermissions(selectedPermissions);
                
                return updatedRole;
            }
            return null;
        });
        
        return dialog.showAndWait();
    }
}