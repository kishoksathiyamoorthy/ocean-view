package com.oceanview.service;

import com.oceanview.dao.DAOFactory;
import com.oceanview.dao.GuestDAO;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomDAO;
import com.oceanview.dao.BillDAO;
import com.oceanview.model.Bill;
import com.oceanview.model.Guest;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;

import java.util.Date;
import java.util.List;

/**
 * Reservation Service.
 * Handles all reservation-related business logic.
 * Acts as a facade between controllers and DAOs.
 * 
 * Design Pattern: Service/Facade
 * - Provides simplified interface for complex operations
 * - Encapsulates business logic
 * - Coordinates between multiple DAOs
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public class ReservationService {
    
    private static ReservationService instance;
    private final ReservationDAO reservationDAO;
    private final GuestDAO guestDAO;
    private final RoomDAO roomDAO;
    private final BillDAO billDAO;
    
    private ReservationService() {
        DAOFactory factory = DAOFactory.getInstance();
        this.reservationDAO = factory.getReservationDAO();
        this.guestDAO = factory.getGuestDAO();
        this.roomDAO = factory.getRoomDAO();
        this.billDAO = factory.getBillDAO();
    }
    
    public static synchronized ReservationService getInstance() {
        if (instance == null) {
            instance = new ReservationService();
        }
        return instance;
    }
    
    // ==================== GUEST OPERATIONS ====================
    
    /**
     * Create a new guest
     */
    public Guest createGuest(Guest guest) {
        return guestDAO.create(guest);
    }
    
    /**
     * Update guest information
     */
    public boolean updateGuest(Guest guest) {
        return guestDAO.update(guest);
    }
    
    /**
     * Find guest by ID
     */
    public Guest getGuestById(int guestId) {
        return guestDAO.findById(guestId);
    }
    
    /**
     * Find guest by contact number
     */
    public Guest getGuestByContact(String contactNumber) {
        return guestDAO.findByContactNumber(contactNumber);
    }
    
    /**
     * Search guests by name
     */
    public List<Guest> searchGuestsByName(String name) {
        return guestDAO.searchByName(name);
    }
    
    /**
     * Get all guests
     */
    public List<Guest> getAllGuests() {
        return guestDAO.findAll();
    }
    
    /**
     * Delete guest
     */
    public boolean deleteGuest(int guestId) {
        return guestDAO.delete(guestId);
    }
    
    // ==================== ROOM OPERATIONS ====================
    
    /**
     * Create a new room
     */
    public Room createRoom(Room room) {
        return roomDAO.create(room);
    }
    
    /**
     * Update room information
     */
    public boolean updateRoom(Room room) {
        return roomDAO.update(room);
    }
    
    /**
     * Find room by ID
     */
    public Room getRoomById(int roomId) {
        return roomDAO.findById(roomId);
    }
    
    /**
     * Find room by room number
     */
    public Room getRoomByNumber(String roomNumber) {
        return roomDAO.findByRoomNumber(roomNumber);
    }
    
    /**
     * Get all rooms
     */
    public List<Room> getAllRooms() {
        return roomDAO.findAll();
    }
    
    /**
     * Get available rooms
     */
    public List<Room> getAvailableRooms() {
        return roomDAO.findAvailableRooms();
    }
    
    /**
     * Get rooms by type
     */
    public List<Room> getRoomsByType(String roomType) {
        return roomDAO.findByRoomType(roomType);
    }
    
    /**
     * Get available rooms for date range
     */
    public List<Room> getAvailableRoomsForDates(Date checkIn, Date checkOut) {
        return roomDAO.findAvailableRoomsForDates(checkIn, checkOut);
    }
    
    /**
     * Get available rooms by type for date range
     */
    public List<Room> getAvailableRoomsByTypeForDates(String roomType, Date checkIn, Date checkOut) {
        return roomDAO.findAvailableRoomsByTypeForDates(roomType, checkIn, checkOut);
    }
    
    /**
     * Delete room
     */
    public boolean deleteRoom(int roomId) {
        return roomDAO.delete(roomId);
    }
    
    // ==================== RESERVATION OPERATIONS ====================
    
    /**
     * Create a new reservation
     * This is the main booking operation
     */
    public Reservation createReservation(Reservation reservation) {
        // Validate room availability
        Room room = roomDAO.findById(reservation.getRoomId());
        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }
        
        // Calculate total amount
        long nights = reservation.getNumberOfNights();
        reservation.setTotalAmount(nights * room.getRatePerNight());
        
        // Set default status
        if (reservation.getStatus() == null) {
            reservation.setStatus("CONFIRMED");
        }
        
        // Create reservation
        Reservation created = reservationDAO.create(reservation);
        
        if (created != null) {
            // Load full details
            created.setGuest(guestDAO.findById(created.getGuestId()));
            created.setRoom(room);
        }
        
        return created;
    }
    
    /**
     * Update reservation
     */
    public boolean updateReservation(Reservation reservation) {
        return reservationDAO.update(reservation);
    }
    
    /**
     * Find reservation by ID
     */
    public Reservation getReservationById(int reservationId) {
        return reservationDAO.findById(reservationId);
    }
    
    /**
     * Find reservation by reservation number
     */
    public Reservation getReservationByNumber(String reservationNumber) {
        return reservationDAO.findByReservationNumber(reservationNumber);
    }
    
    /**
     * Get all reservations
     */
    public List<Reservation> getAllReservations() {
        return reservationDAO.findAllWithDetails();
    }
    
    /**
     * Get reservations by status
     */
    public List<Reservation> getReservationsByStatus(String status) {
        return reservationDAO.findByStatus(status);
    }
    
    /**
     * Get reservations by guest
     */
    public List<Reservation> getReservationsByGuest(int guestId) {
        return reservationDAO.findByGuestId(guestId);
    }
    
    /**
     * Get reservations by date range
     */
    public List<Reservation> getReservationsByDateRange(Date startDate, Date endDate) {
        return reservationDAO.findByDateRange(startDate, endDate);
    }
    
    /**
     * Get today's check-ins
     */
    public List<Reservation> getTodayCheckIns() {
        return reservationDAO.findTodayCheckIns();
    }
    
    /**
     * Get today's check-outs
     */
    public List<Reservation> getTodayCheckOuts() {
        return reservationDAO.findTodayCheckOuts();
    }
    
    /**
     * Check-in guest
     */
    public boolean checkIn(int reservationId) {
        return reservationDAO.updateStatus(reservationId, "CHECKED_IN");
    }
    
    /**
     * Check-out guest
     */
    public boolean checkOut(int reservationId) {
        return reservationDAO.updateStatus(reservationId, "CHECKED_OUT");
    }
    
    /**
     * Cancel reservation
     */
    public boolean cancelReservation(int reservationId) {
        return reservationDAO.updateStatus(reservationId, "CANCELLED");
    }
    
    /**
     * Delete reservation
     */
    public boolean deleteReservation(int reservationId) {
        return reservationDAO.delete(reservationId);
    }
    
    /**
     * Get reservation statistics
     */
    public int getReservationCount(String status) {
        return reservationDAO.getReservationCount(status);
    }
    
    // ==================== BILLING OPERATIONS ====================
    
    /**
     * Generate bill for reservation
     */
    public Bill generateBill(int reservationId, double discountPercent) {
        Reservation reservation = reservationDAO.findById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found");
        }
        
        Room room = roomDAO.findById(reservation.getRoomId());
        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }
        
        // Check if bill already exists
        Bill existingBill = billDAO.findByReservationId(reservationId);
        if (existingBill != null) {
            return existingBill;
        }
        
        // Create new bill
        Bill bill = new Bill();
        bill.setReservationId(reservationId);
        bill.calculateBill(reservation.getNumberOfNights(), room.getRatePerNight(), discountPercent);
        bill.setPaymentStatus("PENDING");
        
        Bill createdBill = billDAO.create(bill);
        if (createdBill != null) {
            createdBill.setReservation(reservation);
        }
        
        return createdBill;
    }
    
    /**
     * Get bill by ID
     */
    public Bill getBillById(int billId) {
        return billDAO.findById(billId);
    }
    
    /**
     * Get bill by reservation ID
     */
    public Bill getBillByReservation(int reservationId) {
        Bill bill = billDAO.findByReservationId(reservationId);
        if (bill != null) {
            bill.setReservation(reservationDAO.findById(reservationId));
        }
        return bill;
    }
    
    /**
     * Get bill by bill number
     */
    public Bill getBillByNumber(String billNumber) {
        return billDAO.findByBillNumber(billNumber);
    }
    
    /**
     * Get all bills
     */
    public List<Bill> getAllBills() {
        return billDAO.findAll();
    }
    
    /**
     * Update payment status
     */
    public boolean updatePaymentStatus(int billId, String paymentStatus, String paymentMethod) {
        return billDAO.updatePaymentStatus(billId, paymentStatus, paymentMethod);
    }
    
    /**
     * Get total revenue
     */
    public double getTotalRevenue(String paymentStatus) {
        return billDAO.getTotalRevenue(paymentStatus);
    }
    
    /**
     * Get pending bills
     */
    public List<Bill> getPendingBills() {
        return billDAO.findByPaymentStatus("PENDING");
    }
}
