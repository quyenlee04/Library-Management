package com.library.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.library.dao.UserDAO;
import com.library.model.User;
import com.library.util.PasswordUtil;

public class AuthService {
    private static AuthService instance;
    private UserDAO userDAO;
    private User currentUser;

    private AuthService() {
        userDAO = UserDAO.getInstance();
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public boolean login(String username, String password) {
        Optional<User> userOpt = userDAO.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            if (PasswordUtil.verifyPassword(password, user.getPassword())) {
                currentUser = user;
                
                // Remove the lastLogin update
                // user.setLastLogin(LocalDateTime.now());
                // userDAO.update(user);
                
                return true;
            }
        }
        
        return false;
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean register(String username, String password, User.UserType userType) {
        // Check if username already exists
        if (userDAO.findByUsername(username).isPresent()) {
            return false;
        }
        
        // Create new user
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(PasswordUtil.hashPassword(password));
        newUser.setUserType(userType);
        newUser.setStatus(User.UserStatus.ACTIVE);
        newUser.setCreatedAt(LocalDateTime.now());
        
        // Save user to database
        return userDAO.save(newUser);
    }
    
    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) {
            return false;
        }
        
        // Verify old password
        if (PasswordUtil.verifyPassword(oldPassword, currentUser.getPassword())) {
            // Update password
            currentUser.setPassword(PasswordUtil.hashPassword(newPassword));
            return userDAO.update(currentUser);
        }
        
        return false;
    }
    
    /**
     * Verifies if the provided password matches the stored password for a user
     * @param username The username to check
     * @param password The password to verify
     * @return true if password matches
     */
    public boolean verifyPassword(String username, String password) {
        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return PasswordUtil.verifyPassword(password, user.getPassword());
        }
        return false;
    }
    
    /**
     * Lấy thông tin người dùng theo tên đăng nhập
     * @param username Tên đăng nhập cần tìm
     * @return Optional chứa thông tin người dùng nếu tìm thấy
     */
    public Optional<User> getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }
    
    /**
     * Xóa người dùng theo ID
     * @param userId ID của người dùng cần xóa
     * @return true nếu xóa thành công
     */
    public boolean deleteUser(String userId) {
        return userDAO.delete(userId);
    }
}