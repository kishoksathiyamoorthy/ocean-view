package com.oceanview.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Manager using Singleton Pattern.
 * Provides centralized database connection management for the application.
 * Uses MySQL database for data persistence.
 * 
 * Design Pattern: Singleton
 * - Ensures only one instance of the database connection manager exists
 * - Provides global point of access to the database connection
 * - Thread-safe implementation
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public class DatabaseConnection {
    
    // Singleton instance
    private static DatabaseConnection instance;
    
    // Database connection
    private Connection connection;
    
    // MySQL Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/oceanviewdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "8385";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    /**
     * Private constructor to prevent instantiation
     * Initializes the database connection
     */
    private DatabaseConnection() {
        try {
            // Load the MySQL driver
            Class.forName(DB_DRIVER);
            // Establish connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("MySQL Database connection established successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get the singleton instance of DatabaseConnection
     * Thread-safe implementation using double-checked locking
     * 
     * @return DatabaseConnection instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Get the database connection
     * 
     * @return Connection object
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Error getting connection: " + e.getMessage());
        }
        return connection;
    }
    
    /**
     * Close the database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
    /**
     * Reset the singleton instance (useful for testing)
     */
    public static synchronized void resetInstance() {
        if (instance != null) {
            instance.closeConnection();
            instance = null;
        }
    }
}
