package com.library.controller;

import com.library.service.AuthService;
import com.library.view.ViewManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class MemberDashboardController implements Initializable {
    
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