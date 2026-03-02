package com.oceanview.dao;

import com.oceanview.model.Bill;

import java.util.List;

/**
 * Bill DAO Interface.
 * Extends GenericDAO with Bill-specific operations.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public interface BillDAO extends GenericDAO<Bill> {
    
    /**
     * Find bill by bill number
     * 
     * @param billNumber bill number to search
     * @return Bill if found, null otherwise
     */
    Bill findByBillNumber(String billNumber);
    
    /**
     * Find bill by reservation ID
     * 
     * @param reservationId reservation ID
     * @return Bill if found, null otherwise
     */
    Bill findByReservationId(int reservationId);
    
    /**
     * Find bills by payment status
     * 
     * @param paymentStatus payment status
     * @return list of bills with the specified status
     */
    List<Bill> findByPaymentStatus(String paymentStatus);
    
    /**
     * Update payment status
     * 
     * @param billId bill ID
     * @param paymentStatus new payment status
     * @param paymentMethod payment method
     * @return true if updated, false otherwise
     */
    boolean updatePaymentStatus(int billId, String paymentStatus, String paymentMethod);
    
    /**
     * Generate next bill number
     * 
     * @return next bill number in format BILL-YYYY-NNNN
     */
    String generateBillNumber();
    
    /**
     * Get total revenue
     * 
     * @param paymentStatus optional filter by payment status
     * @return total revenue amount
     */
    double getTotalRevenue(String paymentStatus);
}
