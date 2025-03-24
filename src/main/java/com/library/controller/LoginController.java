package com.library.controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.library.model.Reader;
import com.library.model.User;
import com.library.service.AuthService;
import com.library.service.ReaderService;
import com.library.util.AlertUtil;
import com.library.util.SessionManager;
import com.library.view.ViewManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // Add this import for standard JavaFX FXMLLoader
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

public class LoginController implements Initializable {
    @FXML
    private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerLink;

    private AuthService authService;
    private ReaderService readerService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authService = AuthService.getInstance();
        readerService = ReaderService.getInstance();
        
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
                handleSuccessfulLogin(currentUser);
            }
        } 
        
        else{
            // Check if the account is locked
            if (authService.isCurrentUserLocked()) {
                AlertUtil.showError("Account Locked", "Your account has been locked. Please contact the administrator for assistance.");
            } else {
            AlertUtil.showError("Login Failed", "Invalid username or password.");
        }

    }}

    
    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            // Load the register FXML file
            URL resourceUrl = getClass().getResource("/fxml/auth/register.fxml");
            if (resourceUrl == null) {
                throw new Exception("FXML file not found at path: /fxml/auth/register.fxml");
            }
            
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            BorderPane registerPane = loader.load();
            
            // Get current scene and change its root
            Scene currentScene = registerLink.getScene();
            currentScene.setRoot(registerPane);
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Lỗi", "Không thể mở màn hình đăng ký: " + e.getMessage());
        }
    }

    private void handleSuccessfulLogin(User user) {
        // Lưu ID người dùng vào SessionManager
        if (user.getUserType() == User.UserType.MEMBER) {
            // Lấy thông tin Reader từ User
            Optional<Reader> reader = readerService.getReaderById(user.getId());
            if (reader.isPresent()) {
                SessionManager.setCurrentUserId(reader.get().getMaDocGia());
                System.out.println("Đã lưu ID độc giả: " + reader.get().getMaDocGia());
            } else {
                System.out.println("Không tìm thấy thông tin độc giả cho user: " + user.getId());
                // Set the user ID directly if reader not found
                SessionManager.setCurrentUserId(user.getId());
                
                // You might want to create a reader record here or redirect to a profile completion page
                // For now, we'll just continue with the login process
            }
        }
        
        // Chuyển hướng dựa trên vai trò
        switch (user.getUserType()) {
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
}
