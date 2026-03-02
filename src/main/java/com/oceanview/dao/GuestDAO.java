package com.oceanview.dao;

import com.oceanview.model.Guest;

import java.util.List;

/**
 * Guest DAO Interface.
 * Extends GenericDAO with Guest-specific operations.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public interface GuestDAO extends GenericDAO<Guest> {
    
    /**
     * Find guest by contact number
     * 
     * @param contactNumber contact number to search
     * @return Guest if found, null otherwise
     */
    Guest findByContactNumber(String contactNumber);
    
    /**
     * Find guest by NIC number
     * 
     * @param nicNumber NIC number to search
     * @return Guest if found, null otherwise
     */
    Guest findByNIC(String nicNumber);
    
    /**
     * Search guests by name (partial match)
     * 
     * @param name name to search
     * @return list of matching guests
     */
    List<Guest> searchByName(String name);
    
    /**
     * Find guest by email
     * 
     * @param email email to search
     * @return Guest if found, null otherwise
     */
    Guest findByEmail(String email);
}
