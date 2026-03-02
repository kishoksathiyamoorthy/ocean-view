package com.oceanview.model;

import java.io.Serializable;

/**
 * Model class representing a hotel room.
 * Contains room details including type and pricing.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public class Room implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int roomId;
    private String roomNumber;
    private String roomType; // SINGLE, DOUBLE, DELUXE, SUITE, FAMILY
    private double ratePerNight;
    private int maxOccupancy;
    private String description;
    private boolean available;
    private String amenities;
    
    // Default constructor
    public Room() {
    }
    
    // Parameterized constructor
    public Room(int roomId, String roomNumber, String roomType, double ratePerNight, 
                int maxOccupancy, String description, boolean available, String amenities) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.ratePerNight = ratePerNight;
        this.maxOccupancy = maxOccupancy;
        this.description = description;
        this.available = available;
        this.amenities = amenities;
    }

    // Getters and Setters
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(double ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public int getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    @Override
    public String toString() {
        return "Room{" + "roomId=" + roomId + ", roomNumber=" + roomNumber + 
               ", roomType=" + roomType + ", ratePerNight=" + ratePerNight + '}';
    }
}
