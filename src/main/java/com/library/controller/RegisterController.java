package com.library.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.library.model.Reader;
import com.library.model.User;
import com.library.service.AuthService;
import com.library.service.ReaderService;
import com.library.util.AlertUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class RegisterController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressArea;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Button cancelButton;
    @FXML private Hyperlink loginLink;
    
    private AuthService authService;
    private ReaderService readerService;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        authService = AuthService.getInstance();
        readerService = ReaderService.getInstance();
        
        // Thiết lập ngày mặc định là ngày hiện tại
        birthDatePicker.setValue(LocalDate.now());
    }
    
    @FXML
    private void handleRegister(ActionEvent event) {
        if (validateInputs()) {
            // Đăng ký tài khoản người dùng
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            
            // Change READER to MEMBER
            boolean userCreated = authService.register(username, password, User.UserType.MEMBER);
            
            if (userCreated) {
                // Lấy ID của user vừa tạo
                User newUser = authService.getUserByUsername(username).orElse(null);
                
                if (newUser != null) {
                    // Tạo thông tin độc giả
                    Reader reader = new Reader();
                    reader.setTenDocGia(nameField.getText().trim());
                    reader.setEmail(emailField.getText().trim());
                    reader.setSoDienThoai(phoneField.getText().trim());
                   // Make sure to set birth date
                    reader.setTaiKhoanID(newUser.getId());
                    
                    // Print debug information
                    System.out.println("Creating reader for user ID: " + newUser.getId());
                    
                    boolean readerCreated = readerService.addReader(reader);
                    
                    if (readerCreated) {
                        System.out.println("Reader created successfully with ID: " + reader.getMaDocGia());
                        AlertUtil.showInformation("Đăng ký thành công", 
                                "Tài khoản đã được tạo thành công. Vui lòng đăng nhập để tiếp tục.");
                        
                        // Chuyển đến màn hình đăng nhập
                        navigateToLogin();
                    } else {
                        // Xóa tài khoản user nếu không tạo được reader
                        authService.deleteUser(newUser.getId());
                        AlertUtil.showError("Lỗi", "Không thể tạo thông tin độc giả. Vui lòng thử lại sau.");
                    }
                } else {
                    AlertUtil.showError("Lỗi", "Không thể tạo tài khoản. Vui lòng thử lại sau.");
                }
            } else {
                AlertUtil.showError("Lỗi", "Tên đăng nhập đã tồn tại hoặc không thể tạo tài khoản.");
            }
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }
    
    @FXML
    private void handleLoginLink(ActionEvent event) {
        navigateToLogin();
    }
    
    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();
        
        if (nameField.getText().trim().isEmpty()) {
            errorMessage.append("Họ tên không được để trống\n");
        }
        
        if (emailField.getText().trim().isEmpty()) {
            errorMessage.append("Email không được để trống\n");
        } else if (!isValidEmail(emailField.getText().trim())) {
            errorMessage.append("Email không hợp lệ\n");
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            errorMessage.append("Số điện thoại không được để trống\n");
        } else if (!isValidPhone(phoneField.getText().trim())) {
            errorMessage.append("Số điện thoại không hợp lệ\n");
        }
        
        if (birthDatePicker.getValue() == null) {
            errorMessage.append("Ngày sinh không được để trống\n");
        } else if (birthDatePicker.getValue().isAfter(LocalDate.now())) {
            errorMessage.append("Ngày sinh không thể là ngày trong tương lai\n");
        }
        
        if (usernameField.getText().trim().isEmpty()) {
            errorMessage.append("Tên đăng nhập không được để trống\n");
        } else if (usernameField.getText().trim().length() < 4) {
            errorMessage.append("Tên đăng nhập phải có ít nhất 4 ký tự\n");
        }
        
        if (passwordField.getText().isEmpty()) {
            errorMessage.append("Mật khẩu không được để trống\n");
        } else if (passwordField.getText().length() < 8) {
            errorMessage.append("Mật khẩu phải có ít nhất 8 ký tự\n");
        }
        
        if (confirmPasswordField.getText().isEmpty()) {
            errorMessage.append("Xác nhận mật khẩu không được để trống\n");
        } else if (!confirmPasswordField.getText().equals(passwordField.getText())) {
            errorMessage.append("Xác nhận mật khẩu không khớp với mật khẩu\n");
        }
        
        if (errorMessage.length() > 0) {
            AlertUtil.showError("Lỗi nhập liệu", errorMessage.toString());
            return false;
        }
        
        return true;
    }
    
    private boolean isValidEmail(String email) {
        // Kiểm tra định dạng email đơn giản
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    private boolean isValidPhone(String phone) {
        // Kiểm tra định dạng số điện thoại (chỉ chấp nhận số và có độ dài từ 10-12 ký tự)
        String phoneRegex = "^[0-9]{10,12}$";
        return phone.matches(phoneRegex);
    }
    
    private void navigateToLogin() {
        try {
            URL resourceUrl = getClass().getResource("/fxml/login.fxml");
            if (resourceUrl == null) {
                throw new Exception("FXML file not found at path: /fxml/login.fxml");
            }
            
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            BorderPane loginPane = loader.load();
            BorderPane currentPane = (BorderPane) registerButton.getScene().getRoot();
            currentPane.getScene().setRoot(loginPane);
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Lỗi", "Không thể chuyển đến trang đăng nhập: " + e.getMessage());
        }
    }
    
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}