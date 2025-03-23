package com.library.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.library.model.User;
import com.library.service.AuthService;
import com.library.util.AlertUtil;
import com.library.view.ViewManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController implements Initializable {
    @FXML
    private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerLink;

    private AuthService authService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authService = AuthService.getInstance();
        
        // Add key event handler to enable login with Enter key
        passwordField.setOnKeyPressed(this::handleEnterKeyPressed);
    }
    
    private void handleEnterKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin(new ActionEvent());
        }
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            AlertUtil.showError("Login Error", "Please enter both username and password.");
            return;
        }
        
        // Attempt login
        boolean loginSuccess = authService.login(username, password);
        
        if (loginSuccess) {
            // Remember Me functionality
            // Note: Since saveRememberMePreference doesn't exist, we'll need to handle this differently
            // or implement the method in AuthService
            
            // Navigate to appropriate dashboard based on user type
            User currentUser = authService.getCurrentUser();
            if (currentUser != null) {
                switch (currentUser.getUserType()) {
                    case ADMIN:
                        ViewManager.getInstance().switchToAdminDashboard();
                        break;
                    case LIBRARIAN:
                        ViewManager.getInstance().switchToLibrarianView();
                        break;
                    case MEMBER:
                        ViewManager.getInstance().switchToMemberView();
                        break;
                    default:
                        AlertUtil.showError("Unknown User Type", "The system cannot determine the appropriate view for your user type.");
                        break;
                }
            }
        } else {
            AlertUtil.showError("Login Failed", "Invalid username or password.");
        }
    }
    
    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            // Switch to the registration view
            ViewManager.getInstance().switchToRegistrationView();
        } catch (Exception e) {
            AlertUtil.showError("Error", "Could not open registration form: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
