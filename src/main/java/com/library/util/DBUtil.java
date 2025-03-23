package com.library.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    private static DBUtil instance;
    private String url;
    private String username;
    private String password;
    
    private DBUtil() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Load database properties
            Properties props = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties");
            
            if (input == null) {
                System.err.println("Unable to find database.properties file");
                // Use default values if properties file is not found
                url = "jdbc:mysql://localhost:3306/quanlythuvien?useSSL=false&serverTimezone=UTC";
                username = "root";
                password = "";
            } else {
                props.load(input);
                url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/quanlythuvien?useSSL=false&serverTimezone=UTC");
                username = props.getProperty("db.username", "root");
                password = props.getProperty("db.password", "");
                input.close();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error loading database properties: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static DBUtil getInstance() {
        if (instance == null) {
            instance = new DBUtil();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        if (url == null || username == null) {
            throw new SQLException("Database configuration is not properly initialized");
        }
        return DriverManager.getConnection(url, username, password);
    }
    
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}