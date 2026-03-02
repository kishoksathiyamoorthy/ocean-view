package com.oceanview.model;

import java.io.Serializable;

/**
 * Model class representing a hotel guest.
 * Stores personal information of guests making reservations.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public class Guest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int guestId;
    private String firstName;
    private String lastName;
    private String address;
    private String contactNumber;
    private String email;
    private String nicNumber; // National Identity Card
    
    // Default constructor
    public Guest() {
    }
    
    // Parameterized constructor
    public Guest(int guestId, String firstName, String lastName, String address, 
                 String contactNumber, String email, String nicNumber) {
        this.guestId = guestId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
        this.nicNumber = nicNumber;
    }

    // Getters and Setters
    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNicNumber() {
        return nicNumber;
    }

    public void setNicNumber(String nicNumber) {
        this.nicNumber = nicNumber;
    }

    @Override
    public String toString() {
        return "Guest{" + "guestId=" + guestId + ", firstName=" + firstName + 
               ", lastName=" + lastName + ", contactNumber=" + contactNumber + '}';
    }
}
