package com.oceanview.dao;

import com.oceanview.model.Guest;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import com.oceanview.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Reservation DAO Implementation.
 * Implements ReservationDAO interface with Derby database operations.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public class ReservationDAOImpl implements ReservationDAO {
    
    private final Connection connection;
    private final GuestDAO guestDAO;
    private final RoomDAO roomDAO;
    
    public ReservationDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.guestDAO = new GuestDAOImpl();
        this.roomDAO = new RoomDAOImpl();
    }
    
    @Override
    public Reservation create(Reservation reservation) {
        String sql = "INSERT INTO reservations (reservation_number, guest_id, room_id, check_in_date, " +
                     "check_out_date, number_of_guests, status, total_amount, special_requests) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Generate reservation number if not provided
            if (reservation.getReservationNumber() == null || reservation.getReservationNumber().isEmpty()) {
                reservation.setReservationNumber(generateReservationNumber());
            }
            
            stmt.setString(1, reservation.getReservationNumber());
            stmt.setInt(2, reservation.getGuestId());
            stmt.setInt(3, reservation.getRoomId());
            stmt.setDate(4, new java.sql.Date(reservation.getCheckInDate().getTime()));
            stmt.setDate(5, new java.sql.Date(reservation.getCheckOutDate().getTime()));
            stmt.setInt(6, reservation.getNumberOfGuests());
            stmt.setString(7, reservation.getStatus() != null ? reservation.getStatus() : "CONFIRMED");
            stmt.setDouble(8, reservation.getTotalAmount());
            stmt.setString(9, reservation.getSpecialRequests());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reservation.setReservationId(generatedKeys.getInt(1));
                    }
                }
            }
            return reservation;
        } catch (SQLException e) {
            System.err.println("Error creating reservation: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public Reservation findById(int id) {
        String sql = "SELECT * FROM reservations WHERE reservation_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Reservation reservation = mapResultSetToReservation(rs);
                    // Load guest and room details
                    reservation.setGuest(guestDAO.findById(reservation.getGuestId()));
                    reservation.setRoom(roomDAO.findById(reservation.getRoomId()));
                    return reservation;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding reservation: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public Reservation findByReservationNumber(String reservationNumber) {
        String sql = "SELECT * FROM reservations WHERE reservation_number = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, reservationNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Reservation reservation = mapResultSetToReservation(rs);
                    // Load guest and room details
                    reservation.setGuest(guestDAO.findById(reservation.getGuestId()));
                    reservation.setRoom(roomDAO.findById(reservation.getRoomId()));
                    return reservation;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding reservation by number: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<Reservation> findByGuestId(int guestId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE guest_id = ? ORDER BY check_in_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, guestId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = mapResultSetToReservation(rs);
                    reservation.setGuest(guestDAO.findById(reservation.getGuestId()));
                    reservation.setRoom(roomDAO.findById(reservation.getRoomId()));
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding reservations by guest: " + e.getMessage());
        }
        return reservations;
    }
    
    @Override
    public List<Reservation> findByStatus(String status) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE status = ? ORDER BY check_in_date";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = mapResultSetToReservation(rs);
                    reservation.setGuest(guestDAO.findById(reservation.getGuestId()));
                    reservation.setRoom(roomDAO.findById(reservation.getRoomId()));
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding reservations by status: " + e.getMessage());
        }
        return reservations;
    }
    
    @Override
    public List<Reservation> findByDateRange(Date startDate, Date endDate) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE check_in_date >= ? AND check_in_date <= ? ORDER BY check_in_date";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(startDate.getTime()));
            stmt.setDate(2, new java.sql.Date(endDate.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = mapResultSetToReservation(rs);
                    reservation.setGuest(guestDAO.findById(reservation.getGuestId()));
                    reservation.setRoom(roomDAO.findById(reservation.getRoomId()));
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding reservations by date range: " + e.getMessage());
        }
        return reservations;
    }
    
    @Override
    public List<Reservation> findTodayCheckIns() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE check_in_date = CURRENT_DATE AND status = 'CONFIRMED'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Reservation reservation = mapResultSetToReservation(rs);
                reservation.setGuest(guestDAO.findById(reservation.getGuestId()));
                reservation.setRoom(roomDAO.findById(reservation.getRoomId()));
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.err.println("Error finding today's check-ins: " + e.getMessage());
        }
        return reservations;
    }
    
    @Override
    public List<Reservation> findTodayCheckOuts() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE check_out_date = CURRENT_DATE AND status = 'CHECKED_IN'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Reservation reservation = mapResultSetToReservation(rs);
                reservation.setGuest(guestDAO.findById(reservation.getGuestId()));
                reservation.setRoom(roomDAO.findById(reservation.getRoomId()));
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.err.println("Error finding today's check-outs: " + e.getMessage());
        }
        return reservations;
    }
    
    @Override
    public boolean updateStatus(int reservationId, String status) {
        String sql = "UPDATE reservations SET status = ? WHERE reservation_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, reservationId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating reservation status: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String generateReservationNumber() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        
        String sql = "SELECT MAX(CAST(SUBSTR(reservation_number, 10) AS INT)) FROM reservations " +
                     "WHERE reservation_number LIKE 'OVR-" + year + "-%'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int nextNumber = 1;
            if (rs.next()) {
                int maxNumber = rs.getInt(1);
                if (!rs.wasNull()) {
                    nextNumber = maxNumber + 1;
                }
            }
            
            return String.format("OVR-%d-%04d", year, nextNumber);
        } catch (SQLException e) {
            System.err.println("Error generating reservation number: " + e.getMessage());
            // Fallback: use timestamp
            return "OVR-" + year + "-" + System.currentTimeMillis() % 10000;
        }
    }
    
    @Override
    public int getReservationCount(String status) {
        String sql;
        if (status != null && !status.isEmpty()) {
            sql = "SELECT COUNT(*) FROM reservations WHERE status = ?";
        } else {
            sql = "SELECT COUNT(*) FROM reservations";
        }
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (status != null && !status.isEmpty()) {
                stmt.setString(1, status);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting reservation count: " + e.getMessage());
        }
        return 0;
    }
    
    @Override
    public List<Reservation> findAll() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations ORDER BY created_date DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all reservations: " + e.getMessage());
        }
        return reservations;
    }
    
    @Override
    public List<Reservation> findAllWithDetails() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, g.first_name, g.last_name, g.contact_number, g.email, " +
                     "rm.room_number, rm.room_type, rm.rate_per_night " +
                     "FROM reservations r " +
                     "JOIN guests g ON r.guest_id = g.guest_id " +
                     "JOIN rooms rm ON r.room_id = rm.room_id " +
                     "ORDER BY r.created_date DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Reservation reservation = mapResultSetToReservation(rs);
                
                // Create and set guest
                Guest guest = new Guest();
                guest.setGuestId(reservation.getGuestId());
                guest.setFirstName(rs.getString("first_name"));
                guest.setLastName(rs.getString("last_name"));
                guest.setContactNumber(rs.getString("contact_number"));
                guest.setEmail(rs.getString("email"));
                reservation.setGuest(guest);
                
                // Create and set room
                Room room = new Room();
                room.setRoomId(reservation.getRoomId());
                room.setRoomNumber(rs.getString("room_number"));
                room.setRoomType(rs.getString("room_type"));
                room.setRatePerNight(rs.getDouble("rate_per_night"));
                reservation.setRoom(room);
                
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.err.println("Error finding all reservations with details: " + e.getMessage());
        }
        return reservations;
    }
    
    @Override
    public boolean update(Reservation reservation) {
        String sql = "UPDATE reservations SET guest_id = ?, room_id = ?, check_in_date = ?, " +
                     "check_out_date = ?, number_of_guests = ?, status = ?, total_amount = ?, " +
                     "special_requests = ? WHERE reservation_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reservation.getGuestId());
            stmt.setInt(2, reservation.getRoomId());
            stmt.setDate(3, new java.sql.Date(reservation.getCheckInDate().getTime()));
            stmt.setDate(4, new java.sql.Date(reservation.getCheckOutDate().getTime()));
            stmt.setInt(5, reservation.getNumberOfGuests());
            stmt.setString(6, reservation.getStatus());
            stmt.setDouble(7, reservation.getTotalAmount());
            stmt.setString(8, reservation.getSpecialRequests());
            stmt.setInt(9, reservation.getReservationId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating reservation: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM reservations WHERE reservation_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Map ResultSet to Reservation object
     */
    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setReservationId(rs.getInt("reservation_id"));
        reservation.setReservationNumber(rs.getString("reservation_number"));
        reservation.setGuestId(rs.getInt("guest_id"));
        reservation.setRoomId(rs.getInt("room_id"));
        reservation.setCheckInDate(rs.getDate("check_in_date"));
        reservation.setCheckOutDate(rs.getDate("check_out_date"));
        reservation.setNumberOfGuests(rs.getInt("number_of_guests"));
        reservation.setStatus(rs.getString("status"));
        reservation.setTotalAmount(rs.getDouble("total_amount"));
        reservation.setCreatedDate(rs.getTimestamp("created_date"));
        reservation.setSpecialRequests(rs.getString("special_requests"));
        return reservation;
    }
}
