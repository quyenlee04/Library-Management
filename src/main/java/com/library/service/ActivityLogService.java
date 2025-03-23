package com.library.service;

import java.util.List;

import com.library.dao.ActivityLogDAO;
import com.library.model.ActivityLog;

public class ActivityLogService {
    private static ActivityLogService instance;
    private ActivityLogDAO activityLogDAO;
    private AuthService authService;
    
    private ActivityLogService() {
        activityLogDAO = ActivityLogDAO.getInstance();
        authService = AuthService.getInstance();
    }
    
    public static ActivityLogService getInstance() {
        if (instance == null) {
            instance = new ActivityLogService();
        }
        return instance;
    }
    
    public boolean logActivity(String action, String details) {
        String username = authService.getCurrentUser() != null ? 
                          authService.getCurrentUser().getUsername() : "System";
        
        ActivityLog log = new ActivityLog(username, action, details);
        return activityLogDAO.logActivity(log);
    }
    
    /**
     * Retrieves the most recent activity logs
     * @param limit Maximum number of records to return
     * @return List of recent activity logs
     */
    public List<ActivityLog> getRecentActivities(int limit) {
        return activityLogDAO.getRecentActivities(limit);
    }
}