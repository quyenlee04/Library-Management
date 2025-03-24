package com.library.util;

/**
 * Quản lý thông tin phiên đăng nhập của người dùng
 */
public class SessionManager {
    
    private static String currentUserId;
    private static String currentUserRole;
    private static boolean isLoggedIn = false;
    
    /**
     * Thiết lập thông tin người dùng đăng nhập
     * 
     * @param userId ID của người dùng
     * @param role Vai trò của người dùng (ADMIN, READER, etc.)
     */
    public static void login(String userId, String role) {
        currentUserId = userId;
        currentUserRole = role;
        isLoggedIn = true;
    }
    
    /**
     * Đăng xuất người dùng hiện tại
     */
    public static void logout() {
        currentUserId = null;
        currentUserRole = null;
        isLoggedIn = false;
    }

    
    /**
     * Sets the ID of the currently logged-in user/reader
     * @param userId The ID to set
     */
    public static void setCurrentUserId(String userId) {
        currentUserId = userId;
    }
    
    /**
     * Gets the ID of the currently logged-in user/reader
     * @return The current user ID
     */
    public static String getCurrentUserId() {
        return currentUserId;
    }
    
    /**
     * Lấy vai trò của người dùng hiện tại
     * 
     * @return Vai trò của người dùng đang đăng nhập, hoặc null nếu chưa đăng nhập
     */
    public static String getCurrentUserRole() {
        return currentUserRole;
    }
    
    /**
     * Kiểm tra người dùng đã đăng nhập chưa
     * 
     * @return true nếu người dùng đã đăng nhập, false nếu chưa
     */
    public static boolean isLoggedIn() {
        return isLoggedIn;
    }
    
    /**
     * Kiểm tra người dùng hiện tại có phải là admin không
     * 
     * @return true nếu người dùng hiện tại là admin, false nếu không phải
     */
    public static boolean isAdmin() {
        return "ADMIN".equals(currentUserRole);
    }
    
    /**
     * Kiểm tra người dùng hiện tại có phải là độc giả không
     * 
     * @return true nếu người dùng hiện tại là độc giả, false nếu không phải
     */
    public static boolean isReader() {
        return "READER".equals(currentUserRole);
    }
}