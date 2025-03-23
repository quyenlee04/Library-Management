package com.library.controller;

import com.library.config.AppConfig;
import com.library.util.AlertUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SystemConfigController implements Initializable {

    @FXML private TextField libraryNameField;
    @FXML private TextField contactEmailField;
    @FXML private TextField contactPhoneField;
    @FXML private TextField maxBorrowDaysField;
    @FXML private TextField maxBooksPerUserField;
    @FXML private TextField finePerDayField;
    @FXML private TextField passwordMinLengthField;
    @FXML private TextField sessionTimeoutField;
    @FXML private CheckBox requireStrongPasswordCheckbox;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadConfigValues();
    }
    
    private void loadConfigValues() {
        // Load general settings
        libraryNameField.setText(AppConfig.getProperty("library.name", "Library Management System"));
        contactEmailField.setText(AppConfig.getProperty("library.email", "library@example.com"));
        contactPhoneField.setText(AppConfig.getProperty("library.phone", ""));
        
        // Load borrowing rules
        maxBorrowDaysField.setText(AppConfig.getProperty("borrow.maxDays", "14"));
        maxBooksPerUserField.setText(AppConfig.getProperty("borrow.maxBooks", "5"));
        finePerDayField.setText(AppConfig.getProperty("borrow.finePerDay", "5000"));
        
        // Load security settings
        passwordMinLengthField.setText(AppConfig.getProperty("security.passwordMinLength", "8"));
        sessionTimeoutField.setText(AppConfig.getProperty("security.sessionTimeout", "30"));
        requireStrongPasswordCheckbox.setSelected(
                Boolean.parseBoolean(AppConfig.getProperty("security.requireStrongPassword", "true")));
    }
    
    @FXML
    private void handleSaveChanges(ActionEvent event) {
        try {
            // Validate input fields
            validateNumericField(maxBorrowDaysField, "Max Borrow Days");
            validateNumericField(maxBooksPerUserField, "Max Books Per User");
            validateNumericField(finePerDayField, "Fine Per Day");
            validateNumericField(passwordMinLengthField, "Password Min Length");
            validateNumericField(sessionTimeoutField, "Session Timeout");
            
            // Save general settings
            AppConfig.setProperty("library.name", libraryNameField.getText().trim());
            AppConfig.setProperty("library.email", contactEmailField.getText().trim());
            AppConfig.setProperty("library.phone", contactPhoneField.getText().trim());
            
            // Save borrowing rules
            AppConfig.setProperty("borrow.maxDays", maxBorrowDaysField.getText().trim());
            AppConfig.setProperty("borrow.maxBooks", maxBooksPerUserField.getText().trim());
            AppConfig.setProperty("borrow.finePerDay", finePerDayField.getText().trim());
            
            // Save security settings
            AppConfig.setProperty("security.passwordMinLength", passwordMinLengthField.getText().trim());
            AppConfig.setProperty("security.sessionTimeout", sessionTimeoutField.getText().trim());
            AppConfig.setProperty("security.requireStrongPassword", 
                    String.valueOf(requireStrongPasswordCheckbox.isSelected()));
            
            // Save to file
            AppConfig.saveProperties();
            
            AlertUtil.showInformation("Success", "Configuration saved successfully");
        } catch (IllegalArgumentException e) {
            AlertUtil.showError("Validation Error", e.getMessage());
        } catch (IOException e) {
            AlertUtil.showError("Error", "Failed to save configuration: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleResetDefaults(ActionEvent event) {
        if (AlertUtil.showConfirmation("Reset Defaults", "Are you sure you want to reset all settings to default values?")) {
            // Reset general settings
            libraryNameField.setText("Library Management System");
            contactEmailField.setText("library@example.com");
            contactPhoneField.setText("");
            
            // Reset borrowing rules
            maxBorrowDaysField.setText("14");
            maxBooksPerUserField.setText("5");
            finePerDayField.setText("5000");
            
            // Reset security settings
            passwordMinLengthField.setText("8");
            sessionTimeoutField.setText("30");
            requireStrongPasswordCheckbox.setSelected(true);
        }
    }
    
    private void validateNumericField(TextField field, String fieldName) {
        String value = field.getText().trim();
        try {
            int numValue = Integer.parseInt(value);
            if (numValue <= 0) {
                throw new IllegalArgumentException(fieldName + " must be a positive number");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number");
        }
    }
}