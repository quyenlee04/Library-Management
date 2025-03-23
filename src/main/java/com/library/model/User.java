package com.library.model;

import java.time.LocalDateTime;

public class User {
    private String id;
    private String username;
    private String password;
    private UserType userType;
    private UserStatus status;
    private LocalDateTime createdAt;
    // Remove the lastLogin field
    // private LocalDateTime lastLogin;

    public enum UserType {
        ADMIN, LIBRARIAN, MEMBER
    }

    public enum UserStatus {
        ACTIVE, LOCKED
    }

    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
        this.status = UserStatus.ACTIVE;
    }

    public User(String id, String username, String password, UserType userType) {
        this();
        this.id = id;
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Remove these methods
    // public LocalDateTime getLastLogin() {
    //     return lastLogin;
    // }
    //
    // public void setLastLogin(LocalDateTime lastLogin) {
    //     this.lastLogin = lastLogin;
    // }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", userType=" + userType +
                ", status=" + status +
                '}';
    }
}