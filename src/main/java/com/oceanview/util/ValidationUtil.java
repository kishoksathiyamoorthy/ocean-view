package com.oceanview.util;

import java.util.regex.Pattern;

/**
 * Input Validation Utility Class.
 * Provides validation methods for user input across the application.
 * Ensures data integrity and prevents invalid entries.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public class ValidationUtil {
    
    // Regular expression patterns for validation
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9]{10}$");
    
    private static final Pattern NIC_PATTERN_OLD = 
        Pattern.compile("^[0-9]{9}[VvXx]$");
    
    private static final Pattern NIC_PATTERN_NEW = 
        Pattern.compile("^[0-9]{12}$");
    
    private static final Pattern NAME_PATTERN = 
        Pattern.compile("^[A-Za-z\\s]{2,50}$");
    
    private static final Pattern USERNAME_PATTERN = 
        Pattern.compile("^[A-Za-z0-9_]{4,20}$");
    
    private static final Pattern ROOM_NUMBER_PATTERN = 
        Pattern.compile("^[0-9]{3,4}[A-Za-z]?$");
    
    /**
     * Validate email address
     * 
     * @param email email address to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate phone number (10 digits)
     * 
     * @param phone phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        String cleanPhone = phone.replaceAll("[\\s-]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }
    
    /**
     * Validate Sri Lankan NIC number (old or new format)
     * 
     * @param nic NIC number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidNIC(String nic) {
        if (nic == null || nic.trim().isEmpty()) {
            return false;
        }
        String cleanNIC = nic.trim();
        return NIC_PATTERN_OLD.matcher(cleanNIC).matches() || 
               NIC_PATTERN_NEW.matcher(cleanNIC).matches();
    }
    
    /**
     * Validate name (letters and spaces only, 2-50 characters)
     * 
     * @param name name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(name.trim()).matches();
    }
    
    /**
     * Validate username (alphanumeric and underscore, 4-20 characters)
     * 
     * @param username username to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }
    
    /**
     * Validate password (minimum 6 characters)
     * 
     * @param password password to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    /**
     * Validate room number format
     * 
     * @param roomNumber room number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            return false;
        }
        return ROOM_NUMBER_PATTERN.matcher(roomNumber.trim()).matches();
    }
    
    /**
     * Validate positive number
     * 
     * @param number number to validate
     * @return true if positive, false otherwise
     */
    public static boolean isPositiveNumber(double number) {
        return number > 0;
    }
    
    /**
     * Validate positive integer
     * 
     * @param number number to validate
     * @return true if positive, false otherwise
     */
    public static boolean isPositiveInteger(int number) {
        return number > 0;
    }
    
    /**
     * Validate that a string is not null or empty
     * 
     * @param str string to validate
     * @return true if not empty, false otherwise
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Validate date range (check-out must be after check-in)
     * 
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @return true if valid range, false otherwise
     */
    public static boolean isValidDateRange(java.util.Date checkIn, java.util.Date checkOut) {
        if (checkIn == null || checkOut == null) {
            return false;
        }
        return checkOut.after(checkIn);
    }
    
    /**
     * Validate that date is not in the past
     * 
     * @param date date to validate
     * @return true if date is today or future, false otherwise
     */
    public static boolean isNotPastDate(java.util.Date date) {
        if (date == null) {
            return false;
        }
        java.util.Calendar today = java.util.Calendar.getInstance();
        today.set(java.util.Calendar.HOUR_OF_DAY, 0);
        today.set(java.util.Calendar.MINUTE, 0);
        today.set(java.util.Calendar.SECOND, 0);
        today.set(java.util.Calendar.MILLISECOND, 0);
        return !date.before(today.getTime());
    }
    
    /**
     * Sanitize input string to prevent SQL injection
     * 
     * @param input input string to sanitize
     * @return sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[<>\"'%;()&+]", "");
    }
    
    /**
     * Get validation error message for email
     * 
     * @return error message
     */
    public static String getEmailErrorMessage() {
        return "Invalid email format. Please enter a valid email address.";
    }
    
    /**
     * Get validation error message for phone
     * 
     * @return error message
     */
    public static String getPhoneErrorMessage() {
        return "Invalid phone number. Please enter a 10-digit phone number.";
    }
    
    /**
     * Get validation error message for NIC
     * 
     * @return error message
     */
    public static String getNICErrorMessage() {
        return "Invalid NIC format. Please enter a valid Sri Lankan NIC number.";
    }
    
    /**
     * Get validation error message for name
     * 
     * @return error message
     */
    public static String getNameErrorMessage() {
        return "Invalid name. Name should contain only letters and spaces (2-50 characters).";
    }
}
