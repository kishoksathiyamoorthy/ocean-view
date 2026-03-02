package com.oceanview.dao;

import com.oceanview.model.Room;
import com.oceanview.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Room DAO Implementation.
 * Implements RoomDAO interface with Derby database operations.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public class RoomDAOImpl implements RoomDAO {
    
    private final Connection connection;
    
    public RoomDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    @Override
    public Room create(Room room) {
        String sql = "INSERT INTO rooms (room_number, room_type, rate_per_night, max_occupancy, " +
                     "description, available, amenities) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomType());
            stmt.setDouble(3, room.getRatePerNight());
            stmt.setInt(4, room.getMaxOccupancy());
            stmt.setString(5, room.getDescription());
            stmt.setBoolean(6, room.isAvailable());
            stmt.setString(7, room.getAmenities());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        room.setRoomId(generatedKeys.getInt(1));
                    }
                }
            }
            return room;
        } catch (SQLException e) {
            System.err.println("Error creating room: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public Room findById(int id) {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding room: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public Room findByRoomNumber(String roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, roomNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding room by number: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<Room> findAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE available = true ORDER BY room_number";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding available rooms: " + e.getMessage());
        }
        return rooms;
    }
    
    @Override
    public List<Room> findByRoomType(String roomType) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE room_type = ? ORDER BY room_number";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, roomType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapResultSetToRoom(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding rooms by type: " + e.getMessage());
        }
        return rooms;
    }
    
    @Override
    public List<Room> findAvailableRoomsForDates(Date checkIn, Date checkOut) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.* FROM rooms r WHERE r.available = true AND r.room_id NOT IN (" +
                     "SELECT DISTINCT room_id FROM reservations " +
                     "WHERE status NOT IN ('CANCELLED', 'CHECKED_OUT') " +
                     "AND ((check_in_date <= ? AND check_out_date >= ?) " +
                     "OR (check_in_date <= ? AND check_out_date >= ?) " +
                     "OR (check_in_date >= ? AND check_out_date <= ?))) " +
                     "ORDER BY r.room_type, r.room_number";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            java.sql.Date sqlCheckIn = new java.sql.Date(checkIn.getTime());
            java.sql.Date sqlCheckOut = new java.sql.Date(checkOut.getTime());
            
            stmt.setDate(1, sqlCheckIn);
            stmt.setDate(2, sqlCheckIn);
            stmt.setDate(3, sqlCheckOut);
            stmt.setDate(4, sqlCheckOut);
            stmt.setDate(5, sqlCheckIn);
            stmt.setDate(6, sqlCheckOut);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapResultSetToRoom(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding available rooms for dates: " + e.getMessage());
        }
        return rooms;
    }
    
    @Override
    public List<Room> findAvailableRoomsByTypeForDates(String roomType, Date checkIn, Date checkOut) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.* FROM rooms r WHERE r.available = true AND r.room_type = ? " +
                     "AND r.room_id NOT IN (" +
                     "SELECT DISTINCT room_id FROM reservations " +
                     "WHERE status NOT IN ('CANCELLED', 'CHECKED_OUT') " +
                     "AND ((check_in_date <= ? AND check_out_date >= ?) " +
                     "OR (check_in_date <= ? AND check_out_date >= ?) " +
                     "OR (check_in_date >= ? AND check_out_date <= ?))) " +
                     "ORDER BY r.room_number";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            java.sql.Date sqlCheckIn = new java.sql.Date(checkIn.getTime());
            java.sql.Date sqlCheckOut = new java.sql.Date(checkOut.getTime());
            
            stmt.setString(1, roomType);
            stmt.setDate(2, sqlCheckIn);
            stmt.setDate(3, sqlCheckIn);
            stmt.setDate(4, sqlCheckOut);
            stmt.setDate(5, sqlCheckOut);
            stmt.setDate(6, sqlCheckIn);
            stmt.setDate(7, sqlCheckOut);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapResultSetToRoom(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding available rooms by type for dates: " + e.getMessage());
        }
        return rooms;
    }
    
    @Override
    public boolean updateAvailability(int roomId, boolean available) {
        String sql = "UPDATE rooms SET available = ? WHERE room_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, available);
            stmt.setInt(2, roomId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating room availability: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all rooms: " + e.getMessage());
        }
        return rooms;
    }
    
    @Override
    public boolean update(Room room) {
        String sql = "UPDATE rooms SET room_number = ?, room_type = ?, rate_per_night = ?, " +
                     "max_occupancy = ?, description = ?, available = ?, amenities = ? WHERE room_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomType());
            stmt.setDouble(3, room.getRatePerNight());
            stmt.setInt(4, room.getMaxOccupancy());
            stmt.setString(5, room.getDescription());
            stmt.setBoolean(6, room.isAvailable());
            stmt.setString(7, room.getAmenities());
            stmt.setInt(8, room.getRoomId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating room: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM rooms WHERE room_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting room: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Map ResultSet to Room object
     */
    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setRoomType(rs.getString("room_type"));
        room.setRatePerNight(rs.getDouble("rate_per_night"));
        room.setMaxOccupancy(rs.getInt("max_occupancy"));
        room.setDescription(rs.getString("description"));
        room.setAvailable(rs.getBoolean("available"));
        room.setAmenities(rs.getString("amenities"));
        return room;
    }
}
