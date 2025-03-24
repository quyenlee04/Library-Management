package com.library.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.library.dao.UserDAO;
import com.library.model.User;
import com.library.util.PasswordUtil;

public class UserService {
    private static UserService instance;
    private final UserDAO userDAO;
    
    private UserService() {
        userDAO = UserDAO.getInstance();
    }
    
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
    
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }
    
    public Optional<User> getUserById(String id) {
        return userDAO.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }
    
    public List<User> findUsersByUsername(String searchTerm) {
        return getAllUsers().stream()
                .filter(user -> user.getUsername().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public boolean saveUser(User user) {
        return userDAO.save(user);
    }
    
    public boolean updateUser(User user) {
        return userDAO.update(user);
    }
    
    public boolean deleteUser(String id) {
        return userDAO.delete(id);
    }
    
// From UserService.java
public boolean resetPassword(String userId) {
    Optional<User> userOpt = getUserById(userId);
    if (userOpt.isPresent()) {
        User user = userOpt.get();
        // Set default password (e.g., "password123")
        user.setPassword(PasswordUtil.hashPassword("password123"));
        return updateUser(user);
    }
    return false;
}
    
    public int getTotalUsers() {
        return getAllUsers().size();
    }
    
    /**
     * Updates a user's password
     * @param userId The ID of the user
     * @param newPassword The new password (unhashed)
     * @return true if password was updated successfully
     */
    public boolean updatePassword(String userId, String newPassword) {
        Optional<User> userOpt = getUserById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(PasswordUtil.hashPassword(newPassword));
            return updateUser(user);
        }
        return false;
    }
}