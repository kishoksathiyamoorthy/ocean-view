package com.oceanview.service;

import com.oceanview.dao.DAOFactory;
import com.oceanview.dao.UserDAO;
import com.oceanview.model.User;

import java.util.List;

/**
 * Authentication Service.
 * Handles user authentication and session management.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public class AuthService {
    
    private static AuthService instance;
    private final UserDAO userDAO;
    private User currentUser;
    
    private AuthService() {
        this.userDAO = DAOFactory.getInstance().getUserDAO();
    }
    
    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }
    
    /**
     * Authenticate user with username and password
     * 
     * @param username username
     * @param password password
     * @return User if authentication successful, null otherwise
     */
    public User login(String username, String password) {
        User user = userDAO.authenticate(username, password);
        if (user != null) {
            this.currentUser = user;
        }
        return user;
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        this.currentUser = null;
    }
    
    /**
     * Get currently logged in user
     * 
     * @return current user or null
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if user is logged in
     * 
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Check if current user has admin role
     * 
     * @return true if admin, false otherwise
     */
    public boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }
    
    /**
     * Register new user
     * 
     * @param user user to register
     * @return registered user with ID
     */
    public User registerUser(User user) {
        if (userDAO.usernameExists(user.getUsername())) {
            return null;
        }
        return userDAO.create(user);
    }
    
    /**
     * Update user profile
     * 
     * @param user user to update
     * @return true if updated, false otherwise
     */
    public boolean updateUser(User user) {
        return userDAO.update(user);
    }
    
    /**
     * Get all users (admin function)
     * 
     * @return list of all users
     */
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }
    
    /**
     * Find user by ID
     * 
     * @param userId user ID
     * @return User if found
     */
    public User getUserById(int userId) {
        return userDAO.findById(userId);
    }
    
    /**
     * Delete user (admin function)
     * 
     * @param userId user ID to delete
     * @return true if deleted
     */
    public boolean deleteUser(int userId) {
        return userDAO.delete(userId);
    }
}
