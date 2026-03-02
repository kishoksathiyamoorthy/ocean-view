package com.oceanview.dao;

import java.util.List;

/**
 * Generic DAO Interface.
 * Defines standard CRUD operations for all data access objects.
 * 
 * Design Pattern: DAO (Data Access Object)
 * - Separates data persistence logic from business logic
 * - Provides abstract interface for database operations
 * - Enables easy switching between different data sources
 * 
 * @param <T> Entity type
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public interface GenericDAO<T> {
    
    /**
     * Create a new entity
     * 
     * @param entity entity to create
     * @return created entity with generated ID
     */
    T create(T entity);
    
    /**
     * Find entity by ID
     * 
     * @param id entity ID
     * @return entity if found, null otherwise
     */
    T findById(int id);
    
    /**
     * Get all entities
     * 
     * @return list of all entities
     */
    List<T> findAll();
    
    /**
     * Update an existing entity
     * 
     * @param entity entity to update
     * @return true if updated successfully, false otherwise
     */
    boolean update(T entity);
    
    /**
     * Delete entity by ID
     * 
     * @param id entity ID
     * @return true if deleted successfully, false otherwise
     */
    boolean delete(int id);
}
