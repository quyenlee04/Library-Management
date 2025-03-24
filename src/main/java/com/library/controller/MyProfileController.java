package com.library.controller;

import com.library.model.Reader;
import com.library.model.User;
import com.library.service.AuthService;
import com.library.service.ReaderService;
import com.library.service.UserService;
import com.library.util.AlertUtil;
import com.library.util.SessionManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class MyProfileController implements Initializable {

    @FXML private Label readerIdLabel;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressArea;
    @FXML private DatePicker birthDatePicker;
    @FXML private Label usernameLabel;
    
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    
    @FXML private Button changePasswordButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    
    private ReaderService readerService;
    private UserService userService;
    private AuthService authService;
    private Reader currentReader;
    private User currentUser;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        readerService = ReaderService.getInstance();
        userService = UserService.getInstance();
        authService = AuthService.getInstance();
        
        // Tải thông tin người dùng hiện tại
        loadCurrentUserInfo();
    }
    
    private void loadCurrentUserInfo() {
        String currentUserId = SessionManager.getCurrentUserId();
        
        if (currentUserId != null && !currentUserId.isEmpty()) {
            // Lấy thông tin độc giả
            Optional<Reader> readerOpt = readerService.getReaderById(currentUserId);
            if (readerOpt.isPresent()) {
                currentReader = readerOpt.get();
                
                // Hiển thị thông tin độc giả
                readerIdLabel.setText(currentReader.getMaDocGia());
                nameField.setText(currentReader.getTenDocGia());
                emailField.setText(currentReader.getEmail());
                phoneField.setText(currentReader.getSoDienThoai());
                
                
                // Lấy thông tin tài khoản người dùng
                currentUser = authService.getCurrentUser();
                if (currentUser != null) {
                    usernameLabel.setText(currentUser.getUsername());
                }
            } else {
                // Thử lấy thông tin từ User hiện tại
                currentUser = authService.getCurrentUser();
                if (currentUser != null) {
                    // Tìm Reader dựa trên thông tin User
                    Optional<Reader> readerByUser = readerService.getReaderById(currentUser.getId());
                    if (readerByUser.isPresent()) {
                        currentReader = readerByUser.get();
                        
                        // Hiển thị thông tin độc giả
                        readerIdLabel.setText(currentReader.getMaDocGia());
                        nameField.setText(currentReader.getTenDocGia());
                        emailField.setText(currentReader.getEmail());
                        phoneField.setText(currentReader.getSoDienThoai());
                       
                        usernameLabel.setText(currentUser.getUsername());
                        
                        // Cập nhật SessionManager
                        SessionManager.setCurrentUserId(currentReader.getMaDocGia());
                    } else {
                        AlertUtil.showError("Lỗi", "Không tìm thấy thông tin độc giả cho tài khoản này");
                        closeWindow();
                    }
                } else {
                    AlertUtil.showError("Lỗi", "Không tìm thấy thông tin người dùng");
                    closeWindow();
                }
            }
        } else {
            AlertUtil.showError("Lỗi", "Vui lòng đăng nhập để xem thông tin cá nhân");
            closeWindow();
        }
    }
    
    @FXML
    private void handleSave(ActionEvent event) {
        if (validateInputs()) {
            // Cập nhật thông tin độc giả
            currentReader.setTenDocGia(nameField.getText().trim());
            currentReader.setEmail(emailField.getText().trim());
            currentReader.setSoDienThoai(phoneField.getText().trim());
            
            
            // Lưu thông tin vào cơ sở dữ liệu
            boolean success = readerService.updateReader(currentReader);
            
            if (success) {
                AlertUtil.showInformation("Thành công", "Thông tin cá nhân đã được cập nhật thành công");
            } else {
                AlertUtil.showError("Lỗi", "Không thể cập nhật thông tin cá nhân. Vui lòng thử lại sau.");
            }
        }
    }
    
    @FXML
    private void handleChangePassword(ActionEvent event) {
        if (validatePasswordChange()) {
            // Kiểm tra mật khẩu hiện tại
            String currentPassword = currentPasswordField.getText();
            if (!authService.verifyPassword(currentUser.getUsername(), currentPassword)) {
                AlertUtil.showError("Lỗi", "Mật khẩu hiện tại không chính xác");
                return;
            }
            
            // Cập nhật mật khẩu mới
            String newPassword = newPasswordField.getText();
            boolean success = userService.updatePassword(currentUser.getId(), newPassword);
            
            if (success) {
                AlertUtil.showInformation("Thành công", "Mật khẩu đã được cập nhật thành công");
                // Xóa các trường mật khẩu
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
            } else {
                AlertUtil.showError("Lỗi", "Không thể cập nhật mật khẩu. Vui lòng thử lại sau.");
            }
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
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
        
        if (errorMessage.length() > 0) {
            AlertUtil.showError("Lỗi nhập liệu", errorMessage.toString());
            return false;
        }
        
        return true;
    }
    
    private boolean validatePasswordChange() {
        StringBuilder errorMessage = new StringBuilder();
        
        if (currentPasswordField.getText().isEmpty()) {
            errorMessage.append("Mật khẩu hiện tại không được để trống\n");
        }
        
        if (newPasswordField.getText().isEmpty()) {
            errorMessage.append("Mật khẩu mới không được để trống\n");
        } else if (newPasswordField.getText().length() < 6) {
            errorMessage.append("Mật khẩu mới phải có ít nhất 6 ký tự\n");
        }
        
        if (confirmPasswordField.getText().isEmpty()) {
            errorMessage.append("Xác nhận mật khẩu không được để trống\n");
        } else if (!confirmPasswordField.getText().equals(newPasswordField.getText())) {
            errorMessage.append("Xác nhận mật khẩu không khớp với mật khẩu mới\n");
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
    
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}