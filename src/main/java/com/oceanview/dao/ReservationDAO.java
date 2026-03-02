package com.oceanview.dao;

import com.oceanview.model.Reservation;

import java.util.Date;
import java.util.List;

/**
 * Reservation DAO Interface.
 * Extends GenericDAO with Reservation-specific operations.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public interface ReservationDAO extends GenericDAO<Reservation> {
    
    /**
     * Find reservation by reservation number
     * 
     * @param reservationNumber reservation number to search
     * @return Reservation if found, null otherwise
     */
    Reservation findByReservationNumber(String reservationNumber);
    
    /**
     * Find reservations by guest ID
     * 
     * @param guestId guest ID
     * @return list of reservations for the guest
     */
    List<Reservation> findByGuestId(int guestId);
    
    /**
     * Find reservations by status
     * 
     * @param status reservation status
     * @return list of reservations with the specified status
     */
    List<Reservation> findByStatus(String status);
    
    /**
     * Find reservations for a date range
     * 
     * @param startDate start date
     * @param endDate end date
     * @return list of reservations within the date range
     */
    List<Reservation> findByDateRange(Date startDate, Date endDate);
    
    /**
     * Find today's check-ins
     * 
     * @return list of reservations checking in today
     */
    List<Reservation> findTodayCheckIns();
    
    /**
     * Find today's check-outs
     * 
     * @return list of reservations checking out today
     */
    List<Reservation> findTodayCheckOuts();
    
    /**
     * Update reservation status
     * 
     * @param reservationId reservation ID
     * @param status new status
     * @return true if updated, false otherwise
     */
    boolean updateStatus(int reservationId, String status);
    
    /**
     * Generate next reservation number
     * 
     * @return next reservation number in format OVR-YYYY-NNNN
     */
    String generateReservationNumber();
    
    /**
     * Get reservation count for reporting
     * 
     * @param status optional status filter
     * @return count of reservations
     */
    int getReservationCount(String status);
    
    /**
     * Get all reservations with guest and room details
     * 
     * @return list of reservations with full details
     */
    List<Reservation> findAllWithDetails();
}
