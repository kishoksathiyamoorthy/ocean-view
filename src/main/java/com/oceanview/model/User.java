package com.oceanview.model;

import java.io.Serializable;

/**
 * Model class representing a system user for authentication.
 * Implements Serializable for data persistence.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String role; // ADMIN, RECEPTIONIST, MANAGER
    private boolean active;
    
    // Default constructor
    public User() {
    }
    
    // Parameterized constructor
    public User(int userId, String username, String password, String fullName, String role, boolean active) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.active = active;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "User{" + "userId=" + userId + ", username=" + username + 
               ", fullName=" + fullName + ", role=" + role + ", active=" + active + '}';
    }
}
