package com.oceanview.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Model class representing a hotel reservation.
 * Links guests to rooms with booking details and dates.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public class Reservation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int reservationId;
    private String reservationNumber; // Unique reservation number (e.g., OVR-2026-0001)
    private int guestId;
    private int roomId;
    private Date checkInDate;
    private Date checkOutDate;
    private int numberOfGuests;
    private String status; // CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED
    private double totalAmount;
    private Date createdDate;
    private String specialRequests;
    
    // Additional fields for display purposes
    private Guest guest;
    private Room room;
    
    // Default constructor
    public Reservation() {
        this.createdDate = new Date();
        this.status = "CONFIRMED";
    }
    
    // Parameterized constructor
    public Reservation(int reservationId, String reservationNumber, int guestId, int roomId,
                       Date checkInDate, Date checkOutDate, int numberOfGuests, String status,
                       double totalAmount, Date createdDate, String specialRequests) {
        this.reservationId = reservationId;
        this.reservationNumber = reservationNumber;
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdDate = createdDate;
        this.specialRequests = specialRequests;
    }

    // Getters and Setters
    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
    
    /**
     * Calculate the number of nights for the stay
     * @return number of nights
     */
    public long getNumberOfNights() {
        if (checkInDate != null && checkOutDate != null) {
            long diffInMillies = checkOutDate.getTime() - checkInDate.getTime();
            return diffInMillies / (1000 * 60 * 60 * 24);
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Reservation{" + "reservationId=" + reservationId + 
               ", reservationNumber=" + reservationNumber + ", status=" + status + '}';
    }
}
