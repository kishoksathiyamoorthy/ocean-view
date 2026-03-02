package com.oceanview.dao;

import com.oceanview.model.Room;

import java.util.Date;
import java.util.List;

/**
 * Room DAO Interface.
 * Extends GenericDAO with Room-specific operations.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public interface RoomDAO extends GenericDAO<Room> {
    
    /**
     * Find room by room number
     * 
     * @param roomNumber room number to search
     * @return Room if found, null otherwise
     */
    Room findByRoomNumber(String roomNumber);
    
    /**
     * Find all available rooms
     * 
     * @return list of available rooms
     */
    List<Room> findAvailableRooms();
    
    /**
     * Find rooms by type
     * 
     * @param roomType room type to search
     * @return list of rooms of specified type
     */
    List<Room> findByRoomType(String roomType);
    
    /**
     * Find available rooms for a date range
     * 
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @return list of available rooms
     */
    List<Room> findAvailableRoomsForDates(Date checkIn, Date checkOut);
    
    /**
     * Find available rooms by type for a date range
     * 
     * @param roomType room type
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @return list of available rooms
     */
    List<Room> findAvailableRoomsByTypeForDates(String roomType, Date checkIn, Date checkOut);
    
    /**
     * Update room availability
     * 
     * @param roomId room ID
     * @param available availability status
     * @return true if updated, false otherwise
     */
    boolean updateAvailability(int roomId, boolean available);
}
