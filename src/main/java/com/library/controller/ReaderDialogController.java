package com.library.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.library.model.Reader;
import com.library.service.ReaderService;
import com.library.util.AlertUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ReaderDialogController implements Initializable {

    @FXML private TextField txtName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private TextField txtAccountId;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    
    private Stage dialogStage;
    private Reader reader;
    private boolean confirmed = false;
    private String mode = "add"; // "add" or "edit"
    private ReaderService readerService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        readerService = ReaderService.getInstance();
        
        // Add validation listeners to text fields
        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            validateEmail(newValue);
        });
        
        txtPhone.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePhone(newValue);
        });
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public void setReader(Reader reader) {
        this.reader = reader;
        
        // Populate fields with reader data
        txtName.setText(reader.getTenDocGia());
        txtEmail.setText(reader.getEmail());
        txtPhone.setText(reader.getSoDienThoai());
        txtAccountId.setText(reader.getTaiKhoanID());
    }
    
    public void setMode(String mode) {
        this.mode = mode;
        
        if (mode.equals("add")) {
            reader = new Reader();
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    @FXML
    private void handleSave(ActionEvent event) {
        if (validateInput()) {
            // Update reader object with form data
            reader.setTenDocGia(txtName.getText().trim());
            reader.setEmail(txtEmail.getText().trim());
            reader.setSoDienThoai(txtPhone.getText().trim());
            reader.setTaiKhoanID(txtAccountId.getText().trim());
            
            try {
                boolean success;
                
                if (mode.equals("add")) {
                    success = readerService.addReader(reader);
                } else {
                    success = readerService.updateReader(reader);
                }
                
                if (success) {
                    confirmed = true;
                    dialogStage.close();
                } else {
                    AlertUtil.showError("Error", "Failed to save reader");
                }
            } catch (Exception e) {
                AlertUtil.showError("Error", "Failed to save reader: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        dialogStage.close();
    }
    
    private boolean validateInput() {
        String errorMessage = "";
        
        if (txtName.getText() == null || txtName.getText().trim().isEmpty()) {
            errorMessage += "Name is required\n";
        }
        
        if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty()) {
            errorMessage += "Email is required\n";
        } else if (!validateEmail(txtEmail.getText().trim())) {
            errorMessage += "Invalid email format\n";
        }
        
        if (txtPhone.getText() == null || txtPhone.getText().trim().isEmpty()) {
            errorMessage += "Phone number is required\n";
        } else if (!validatePhone(txtPhone.getText().trim())) {
            errorMessage += "Invalid phone number format\n";
        }
        
        if (errorMessage.isEmpty()) {
            return true;
        } else {
            AlertUtil.showError("Validation Error", errorMessage);
            return false;
        }
    }
    
    private boolean validateEmail(String email) {
        // Simple email validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
    
    private boolean validatePhone(String phone) {
        // Simple phone validation - adjust as needed for your country's format
        String phoneRegex = "^[0-9]{10,15}$";
        return phone.matches(phoneRegex);
    }
}