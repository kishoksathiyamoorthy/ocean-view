package com.oceanview.dao;

import com.oceanview.model.Guest;
import com.oceanview.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Guest DAO Implementation.
 * Implements GuestDAO interface with Derby database operations.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public class GuestDAOImpl implements GuestDAO {
    
    private final Connection connection;
    
    public GuestDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    @Override
    public Guest create(Guest guest) {
        String sql = "INSERT INTO guests (first_name, last_name, address, contact_number, email, nic_number) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, guest.getFirstName());
            stmt.setString(2, guest.getLastName());
            stmt.setString(3, guest.getAddress());
            stmt.setString(4, guest.getContactNumber());
            stmt.setString(5, guest.getEmail());
            stmt.setString(6, guest.getNicNumber());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        guest.setGuestId(generatedKeys.getInt(1));
                    }
                }
            }
            return guest;
        } catch (SQLException e) {
            System.err.println("Error creating guest: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public Guest findById(int id) {
        String sql = "SELECT * FROM guests WHERE guest_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGuest(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding guest: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public Guest findByContactNumber(String contactNumber) {
        String sql = "SELECT * FROM guests WHERE contact_number = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, contactNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGuest(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding guest by contact: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public Guest findByNIC(String nicNumber) {
        String sql = "SELECT * FROM guests WHERE nic_number = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nicNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGuest(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding guest by NIC: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public Guest findByEmail(String email) {
        String sql = "SELECT * FROM guests WHERE email = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGuest(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding guest by email: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<Guest> searchByName(String name) {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests WHERE LOWER(first_name) LIKE ? OR LOWER(last_name) LIKE ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + name.toLowerCase() + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    guests.add(mapResultSetToGuest(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching guests: " + e.getMessage());
        }
        return guests;
    }
    
    @Override
    public List<Guest> findAll() {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests ORDER BY guest_id DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                guests.add(mapResultSetToGuest(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all guests: " + e.getMessage());
        }
        return guests;
    }
    
    @Override
    public boolean update(Guest guest) {
        String sql = "UPDATE guests SET first_name = ?, last_name = ?, address = ?, " +
                     "contact_number = ?, email = ?, nic_number = ? WHERE guest_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, guest.getFirstName());
            stmt.setString(2, guest.getLastName());
            stmt.setString(3, guest.getAddress());
            stmt.setString(4, guest.getContactNumber());
            stmt.setString(5, guest.getEmail());
            stmt.setString(6, guest.getNicNumber());
            stmt.setInt(7, guest.getGuestId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating guest: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM guests WHERE guest_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting guest: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Map ResultSet to Guest object
     */
    private Guest mapResultSetToGuest(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setGuestId(rs.getInt("guest_id"));
        guest.setFirstName(rs.getString("first_name"));
        guest.setLastName(rs.getString("last_name"));
        guest.setAddress(rs.getString("address"));
        guest.setContactNumber(rs.getString("contact_number"));
        guest.setEmail(rs.getString("email"));
        guest.setNicNumber(rs.getString("nic_number"));
        return guest;
    }
}
