package com.library.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.library.service.AuthService;
import com.library.view.ViewManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class LibrarianDashboardController implements Initializable {
    
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
}