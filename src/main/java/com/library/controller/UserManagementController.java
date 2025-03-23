package com.library.controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import com.library.model.User;
import com.library.service.UserService;
import com.library.util.AlertUtil;
import com.library.view.ViewManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class UserManagementController implements Initializable {

    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<User> userTable;
    
    @FXML
    private TableColumn<User, String> idColumn;
    
    @FXML
    private TableColumn<User, String> usernameColumn;
    
    @FXML
    private TableColumn<User, String> userTypeColumn;
    
    @FXML
    private TableColumn<User, String> statusColumn;
    
    @FXML
    private TableColumn<User, String> createdAtColumn;
    
    @FXML
    private TableColumn<User, String> actionsColumn;
    
    @FXML
    private Label statusLabel;
    
    private UserService userService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = UserService.getInstance();
        
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        
        userTypeColumn.setCellValueFactory(data -> {
            User.UserType userType = data.getValue().getUserType();
            return new SimpleStringProperty(userType != null ? userType.toString() : "");
        });
        
        statusColumn.setCellValueFactory(data -> {
            User.UserStatus status = data.getValue().getStatus();
            return new SimpleStringProperty(status != null ? status.toString() : "");
        });
        
        createdAtColumn.setCellValueFactory(data -> {
            LocalDateTime createdAt = data.getValue().getCreatedAt();
            return new SimpleStringProperty(createdAt != null ? createdAt.format(DATE_FORMATTER) : "");
        });
        
        // Configure actions column with edit and delete buttons
        actionsColumn.setCellFactory(col -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    
                    Button editButton = new Button("Edit");
                    editButton.getStyleClass().add("button-secondary");
                    editButton.setOnAction(event -> handleEditUser(user));
                    
                    Button resetButton = new Button("Reset");
                    resetButton.getStyleClass().add("button-warning");
                    resetButton.setOnAction(event -> handleResetPassword(user));
                    
                    Button deleteButton = new Button("Delete");
                    deleteButton.getStyleClass().add("button-danger");
                    deleteButton.setOnAction(event -> handleDeleteUser(user));
                    
                    HBox buttons = new HBox(5, editButton, resetButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
        // Load users
        loadUsers();
    }
    
    /**
     * Handles the action when the "Back to Dashboard" button is clicked.
     * Navigates back to the admin dashboard.
     */
    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        ViewManager.getInstance().switchToAdminDashboard();
    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadUsers(); // Load all users if search term is empty
        } else {
            List<User> users = userService.findUsersByUsername(searchTerm);
            userTable.setItems(FXCollections.observableArrayList(users));
        }
    }
    
    @FXML
    private void handleAddUser(ActionEvent event) {
        UserDialogController.showAddDialog().ifPresent(user -> {
            if (userService.saveUser(user)) {
                AlertUtil.showInformation("Success", "User added successfully");
                loadUsers(); // Refresh the table
            } else {
                AlertUtil.showError("Error", "Failed to add user");
            }
        });
    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadUsers();
        statusLabel.setText("User list refreshed");
    }
    
    private void handleEditUser(User user) {
        UserDialogController.showEditDialog(user).ifPresent(updatedUser -> {
            if (userService.updateUser(updatedUser)) {
                AlertUtil.showInformation("Success", "User updated successfully");
                loadUsers(); // Refresh the table
            } else {
                AlertUtil.showError("Error", "Failed to update user");
            }
        });
    }
    
    private void handleDeleteUser(User user) {
        if (AlertUtil.showConfirmation("Confirm Delete", 
                "Are you sure you want to delete this user: " + user.getUsername() + "? This action cannot be undone.")) {
            if (userService.deleteUser(user.getId())) {
                AlertUtil.showInformation("Success", "User deleted successfully");
                loadUsers(); // Refresh the table
            } else {
                AlertUtil.showError("Error", "Failed to delete user");
            }
        }
    }
    
    private void handleResetPassword(User user) {
        if (AlertUtil.showConfirmation("Confirm Reset", 
                "Are you sure you want to reset the password for " + user.getUsername() + "? A new random password will be generated.")) {
            // Call resetPassword which returns a boolean indicating success
            boolean success = userService.resetPassword(user.getId());
            if (success) {
                AlertUtil.showInformation("Password Reset", 
                        "Password has been reset successfully. The new password is: password123");
            } else {
                AlertUtil.showError("Error", "Failed to reset password");
            }
        }
    }
    

    
    private void loadUsers() {
        List<User> users = userService.getAllUsers();
        userTable.setItems(FXCollections.observableArrayList(users));
    }
}