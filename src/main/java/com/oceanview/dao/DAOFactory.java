package com.oceanview.dao;

/**
 * DAO Factory Class.
 * Implements Factory Pattern to create DAO instances.
 * Provides centralized creation of data access objects.
 * 
 * Design Pattern: Factory
 * - Centralizes DAO object creation
 * - Decouples client code from concrete DAO implementations
 * - Enables easy switching between different DAO implementations
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public class DAOFactory {
    
    // Singleton instance
    private static DAOFactory instance;
    
    // DAO instances (cached for reuse)
    private UserDAO userDAO;
    private GuestDAO guestDAO;
    private RoomDAO roomDAO;
    private ReservationDAO reservationDAO;
    private BillDAO billDAO;
    
    /**
     * Private constructor for singleton pattern
     */
    private DAOFactory() {
    }
    
    /**
     * Get singleton instance of DAOFactory
     * 
     * @return DAOFactory instance
     */
    public static synchronized DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }
    
    /**
     * Get UserDAO instance
     * 
     * @return UserDAO implementation
     */
    public UserDAO getUserDAO() {
        if (userDAO == null) {
            userDAO = new UserDAOImpl();
        }
        return userDAO;
    }
    
    /**
     * Get GuestDAO instance
     * 
     * @return GuestDAO implementation
     */
    public GuestDAO getGuestDAO() {
        if (guestDAO == null) {
            guestDAO = new GuestDAOImpl();
        }
        return guestDAO;
    }
    
    /**
     * Get RoomDAO instance
     * 
     * @return RoomDAO implementation
     */
    public RoomDAO getRoomDAO() {
        if (roomDAO == null) {
            roomDAO = new RoomDAOImpl();
        }
        return roomDAO;
    }
    
    /**
     * Get ReservationDAO instance
     * 
     * @return ReservationDAO implementation
     */
    public ReservationDAO getReservationDAO() {
        if (reservationDAO == null) {
            reservationDAO = new ReservationDAOImpl();
        }
        return reservationDAO;
    }
    
    /**
     * Get BillDAO instance
     * 
     * @return BillDAO implementation
     */
    public BillDAO getBillDAO() {
        if (billDAO == null) {
            billDAO = new BillDAOImpl();
        }
        return billDAO;
    }
}
