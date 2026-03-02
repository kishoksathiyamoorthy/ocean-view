package com.oceanview.dao;

import com.oceanview.model.User;

/**
 * User DAO Interface.
 * Extends GenericDAO with User-specific operations.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public interface UserDAO extends GenericDAO<User> {
    
    /**
     * Find user by username
     * 
     * @param username username to search
     * @return User if found, null otherwise
     */
    User findByUsername(String username);
    
    /**
     * Authenticate user
     * 
     * @param username username
     * @param password password
     * @return User if authentication successful, null otherwise
     */
    User authenticate(String username, String password);
    
    /**
     * Check if username exists
     * 
     * @param username username to check
     * @return true if exists, false otherwise
     */
    boolean usernameExists(String username);
}
