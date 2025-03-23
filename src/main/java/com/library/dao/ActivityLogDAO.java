package com.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.library.model.ActivityLog;
import com.library.util.DBUtil;

public class ActivityLogDAO {
    private static ActivityLogDAO instance;
    
    private ActivityLogDAO() {
        // Private constructor for singleton pattern
    }
    
    public static ActivityLogDAO getInstance() {
        if (instance == null) {
            instance = new ActivityLogDAO();
        }
        return instance;
    }
    
    public boolean logActivity(ActivityLog log) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "INSERT INTO nhatkyhoatdong (username, action, details, timestamp) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, log.getUsername());
            stmt.setString(2, log.getAction());
            stmt.setString(3, log.getDetails());
            stmt.setTimestamp(4, Timestamp.valueOf(log.getTimestamp()));
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error logging activity: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Retrieves the most recent activity logs
     * @param limit Maximum number of records to return
     * @return List of recent activity logs
     */
    public List<ActivityLog> getRecentActivities(int limit) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<ActivityLog> activities = new ArrayList<>();
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT * FROM nhatkyhoatdong ORDER BY timestamp DESC LIMIT ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                ActivityLog log = mapResultSetToActivityLog(rs);
                activities.add(log);
            }
        } catch (SQLException e) {
            System.err.println("Error getting recent activities: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return activities;
    }
    
    private ActivityLog mapResultSetToActivityLog(ResultSet rs) throws SQLException {
        ActivityLog log = new ActivityLog();
        log.setId(rs.getInt("id"));
        log.setUsername(rs.getString("username"));
        log.setAction(rs.getString("action"));
        log.setDetails(rs.getString("details"));
        
        Timestamp timestamp = rs.getTimestamp("timestamp");
        if (timestamp != null) {
            log.setTimestamp(timestamp.toLocalDateTime());
        }
        
        return log;
    }
    
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) DBUtil.closeConnection(conn);
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}