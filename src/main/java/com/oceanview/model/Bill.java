package com.oceanview.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Model class representing a bill for a reservation.
 * Contains billing details including room charges and additional services.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public class Bill implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int billId;
    private String billNumber;
    private int reservationId;
    private double roomCharges;
    private double serviceCharges;
    private double taxAmount;
    private double discountAmount;
    private double totalAmount;
    private Date billDate;
    private String paymentStatus; // PENDING, PAID, PARTIAL
    private String paymentMethod; // CASH, CARD, BANK_TRANSFER
    
    // For display purposes
    private Reservation reservation;
    
    // Tax rate (10% government tax in Sri Lanka)
    public static final double TAX_RATE = 0.10;
    // Service charge rate (10%)
    public static final double SERVICE_CHARGE_RATE = 0.10;
    
    // Default constructor
    public Bill() {
        this.billDate = new Date();
        this.paymentStatus = "PENDING";
    }
    
    // Parameterized constructor
    public Bill(int billId, String billNumber, int reservationId, double roomCharges,
                double serviceCharges, double taxAmount, double discountAmount,
                double totalAmount, Date billDate, String paymentStatus, String paymentMethod) {
        this.billId = billId;
        this.billNumber = billNumber;
        this.reservationId = reservationId;
        this.roomCharges = roomCharges;
        this.serviceCharges = serviceCharges;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public double getRoomCharges() {
        return roomCharges;
    }

    public void setRoomCharges(double roomCharges) {
        this.roomCharges = roomCharges;
    }

    public double getServiceCharges() {
        return serviceCharges;
    }

    public void setServiceCharges(double serviceCharges) {
        this.serviceCharges = serviceCharges;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    
    /**
     * Calculate the bill amount based on reservation details
     * @param numberOfNights number of nights stayed
     * @param ratePerNight room rate per night
     * @param discount discount percentage (0-100)
     */
    public void calculateBill(long numberOfNights, double ratePerNight, double discount) {
        this.roomCharges = numberOfNights * ratePerNight;
        this.serviceCharges = this.roomCharges * SERVICE_CHARGE_RATE;
        double subtotal = this.roomCharges + this.serviceCharges;
        this.discountAmount = subtotal * (discount / 100);
        double afterDiscount = subtotal - this.discountAmount;
        this.taxAmount = afterDiscount * TAX_RATE;
        this.totalAmount = afterDiscount + this.taxAmount;
    }

    @Override
    public String toString() {
        return "Bill{" + "billId=" + billId + ", billNumber=" + billNumber + 
               ", totalAmount=" + totalAmount + ", paymentStatus=" + paymentStatus + '}';
    }
}
