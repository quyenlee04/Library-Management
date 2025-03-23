package com.library.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.library.service.AuthService;
import com.library.view.ViewManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class AdminDashboardController implements Initializable {
    
    @FXML private Label statusLabel;
    
    private AuthService authService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authService = AuthService.getInstance();
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        authService.logout();
        ViewManager.getInstance().switchToLoginView();
    }
    
    @FXML
    private void handleManageUsers(ActionEvent event) {
        ViewManager.getInstance().switchToUserManagementView();
    }
    
    @FXML
    private void handleManageBooks(ActionEvent event) {
        ViewManager.getInstance().switchToBookManagementView();
    }
    
    @FXML
    private void handleSettings(ActionEvent event) {
        ViewManager.getInstance().switchToSystemConfigView();
    }
    
    @FXML
    private void handleExit(ActionEvent event) {
        System.exit(0);
    }
    
    @FXML
    private void handleAbout(ActionEvent event) {
        // Show about dialog
    }
    
    @FXML
    private void handleUserManagement(ActionEvent event) {
        ViewManager.getInstance().switchToUserManagementView();
    }
    
    @FXML
    private void handleBookManagement(ActionEvent event) {
        ViewManager.getInstance().switchToBookManagementView();
    }
    
    @FXML
    private void handleBorrowingManagement(ActionEvent event) {
        // Navigate to borrowing management view
    }
    
    @FXML
    private void handleReports(ActionEvent event) {
        // Navigate to reports view
    }
}
