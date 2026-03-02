package com.oceanview.webservice;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Help REST Resource.
 * Provides help documentation and guidelines for new staff members.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
@Path("/help")
@Produces(MediaType.APPLICATION_JSON)
public class HelpResource {
    
    /**
     * Get all help topics
     * GET /api/help
     */
    @GET
    public Response getAllHelp() {
        JsonObject help = new JsonObject();
        help.addProperty("title", "Ocean View Resort - Reservation System Help");
        help.addProperty("version", "1.0");
        help.addProperty("description", "Welcome to the Ocean View Resort Reservation System. This guide will help you understand how to use the system effectively.");
        
        JsonArray topics = new JsonArray();
        
        // Topic 1: Getting Started
        JsonObject gettingStarted = new JsonObject();
        gettingStarted.addProperty("id", 1);
        gettingStarted.addProperty("title", "Getting Started");
        gettingStarted.addProperty("content", 
            "1. Login: Use your username and password to log into the system.\n" +
            "2. Navigate using the menu to access different features.\n" +
            "3. Default admin credentials: username: admin, password: admin123\n" +
            "4. Change your password after first login for security.");
        topics.add(gettingStarted);
        
        // Topic 2: Making Reservations
        JsonObject reservations = new JsonObject();
        reservations.addProperty("id", 2);
        reservations.addProperty("title", "Making Reservations");
        reservations.addProperty("content",
            "To create a new reservation:\n" +
            "1. First, register the guest (if new) or find existing guest.\n" +
            "2. Check room availability for desired dates.\n" +
            "3. Select an available room that matches guest requirements.\n" +
            "4. Enter check-in and check-out dates.\n" +
            "5. Specify number of guests and any special requests.\n" +
            "6. Confirm the reservation - a unique reservation number will be generated.\n\n" +
            "Reservation Number Format: OVR-YYYY-NNNN (e.g., OVR-2026-0001)");
        topics.add(reservations);
        
        // Topic 3: Guest Management
        JsonObject guests = new JsonObject();
        guests.addProperty("id", 3);
        guests.addProperty("title", "Guest Management");
        guests.addProperty("content",
            "Managing guest information:\n" +
            "1. Add New Guest: Enter first name, last name, contact number, and other details.\n" +
            "2. Search Guest: Search by name or contact number.\n" +
            "3. Update Guest: Modify guest details as needed.\n" +
            "4. View History: See all reservations for a guest.\n\n" +
            "Required Information:\n" +
            "- First Name and Last Name\n" +
            "- Contact Number (10 digits)\n" +
            "- Email (optional but recommended)\n" +
            "- NIC Number (optional)");
        topics.add(guests);
        
        // Topic 4: Room Types & Rates
        JsonObject rooms = new JsonObject();
        rooms.addProperty("id", 4);
        rooms.addProperty("title", "Room Types & Rates");
        rooms.addProperty("content",
            "Available Room Types at Ocean View Resort:\n\n" +
            "1. SINGLE ROOM - LKR 5,000/night\n" +
            "   - Max Occupancy: 1 person\n" +
            "   - Amenities: AC, TV, WiFi, Mini Bar\n\n" +
            "2. DOUBLE ROOM - LKR 8,000/night\n" +
            "   - Max Occupancy: 2 persons\n" +
            "   - Amenities: AC, TV, WiFi, Mini Bar, Balcony\n\n" +
            "3. DELUXE ROOM - LKR 12,000/night\n" +
            "   - Max Occupancy: 3 persons\n" +
            "   - Amenities: AC, Smart TV, WiFi, Mini Bar, Jacuzzi, Ocean View\n\n" +
            "4. SUITE - LKR 20,000/night\n" +
            "   - Max Occupancy: 4 persons\n" +
            "   - Amenities: AC, Smart TV, WiFi, Mini Bar, Jacuzzi, Living Room, Ocean View\n\n" +
            "5. FAMILY ROOM - LKR 15,000/night\n" +
            "   - Max Occupancy: 5 persons\n" +
            "   - Amenities: AC, TV, WiFi, Mini Bar, Kids Area, Garden View");
        topics.add(rooms);
        
        // Topic 5: Check-in/Check-out
        JsonObject checkInOut = new JsonObject();
        checkInOut.addProperty("id", 5);
        checkInOut.addProperty("title", "Check-in and Check-out Process");
        checkInOut.addProperty("content",
            "CHECK-IN PROCESS:\n" +
            "1. Find the reservation using reservation number or guest name.\n" +
            "2. Verify guest identity with ID/Passport.\n" +
            "3. Confirm room details with guest.\n" +
            "4. Click 'Check-in' to update reservation status.\n" +
            "5. Provide room key to guest.\n\n" +
            "CHECK-OUT PROCESS:\n" +
            "1. Find the checked-in reservation.\n" +
            "2. Generate the final bill.\n" +
            "3. Review any additional charges.\n" +
            "4. Process payment (Cash, Card, or Bank Transfer).\n" +
            "5. Click 'Check-out' to complete.\n" +
            "6. Print receipt for guest.\n\n" +
            "Standard Times:\n" +
            "- Check-in: 2:00 PM\n" +
            "- Check-out: 12:00 PM (Noon)");
        topics.add(checkInOut);
        
        // Topic 6: Billing
        JsonObject billing = new JsonObject();
        billing.addProperty("id", 6);
        billing.addProperty("title", "Billing and Payments");
        billing.addProperty("content",
            "GENERATING BILLS:\n" +
            "1. Select the reservation.\n" +
            "2. Click 'Generate Bill'.\n" +
            "3. Apply any discounts if applicable.\n" +
            "4. Review the bill breakdown.\n\n" +
            "BILL COMPONENTS:\n" +
            "- Room Charges: Rate × Number of Nights\n" +
            "- Service Charge: 10% of Room Charges\n" +
            "- Discount: Applied before tax (if any)\n" +
            "- Tax: 10% (Government Tax)\n\n" +
            "PAYMENT METHODS:\n" +
            "- CASH: Accept Sri Lankan Rupees\n" +
            "- CARD: Visa, MasterCard, American Express\n" +
            "- BANK_TRANSFER: For advance bookings\n\n" +
            "Bill Number Format: BILL-YYYY-NNNN (e.g., BILL-2026-0001)");
        topics.add(billing);
        
        // Topic 7: Reports
        JsonObject reports = new JsonObject();
        reports.addProperty("id", 7);
        reports.addProperty("title", "Reports and Statistics");
        reports.addProperty("content",
            "Available Reports:\n\n" +
            "1. DAILY REPORT\n" +
            "   - Today's check-ins and check-outs\n" +
            "   - Current occupancy status\n\n" +
            "2. RESERVATION REPORT\n" +
            "   - All reservations by date range\n" +
            "   - Status breakdown (Confirmed, Checked-in, etc.)\n\n" +
            "3. REVENUE REPORT\n" +
            "   - Total revenue by period\n" +
            "   - Payment status summary\n\n" +
            "4. OCCUPANCY REPORT\n" +
            "   - Room occupancy statistics\n" +
            "   - Room type popularity\n\n" +
            "Access reports from the Reports menu.");
        topics.add(reports);
        
        // Topic 8: Troubleshooting
        JsonObject troubleshooting = new JsonObject();
        troubleshooting.addProperty("id", 8);
        troubleshooting.addProperty("title", "Troubleshooting");
        troubleshooting.addProperty("content",
            "COMMON ISSUES AND SOLUTIONS:\n\n" +
            "1. Cannot Login\n" +
            "   - Check username and password\n" +
            "   - Ensure CAPS LOCK is off\n" +
            "   - Contact admin if account is locked\n\n" +
            "2. Room Shows Unavailable\n" +
            "   - Check if dates overlap with existing bookings\n" +
            "   - Verify room is not under maintenance\n\n" +
            "3. Cannot Create Reservation\n" +
            "   - Ensure guest is registered\n" +
            "   - Check all required fields are filled\n" +
            "   - Verify dates are valid\n\n" +
            "4. Bill Calculation Error\n" +
            "   - Verify check-in/check-out dates\n" +
            "   - Check room rate is correct\n\n" +
            "For technical support, contact the IT department.");
        topics.add(troubleshooting);
        
        help.add("topics", topics);
        
        return Response.ok(help.toString()).build();
    }
    
    /**
     * Get specific help topic
     * GET /api/help/{topicId}
     */
    @GET
    @Path("/{topicId}")
    public Response getHelpTopic(@PathParam("topicId") int topicId) {
        // Redirect to main help for specific topics
        return getAllHelp();
    }
    
    /**
     * Get quick reference card
     * GET /api/help/quick-reference
     */
    @GET
    @Path("/quick-reference")
    public Response getQuickReference() {
        JsonObject quickRef = new JsonObject();
        quickRef.addProperty("title", "Quick Reference Card");
        
        JsonObject shortcuts = new JsonObject();
        shortcuts.addProperty("newReservation", "Click 'New Reservation' or press Ctrl+N");
        shortcuts.addProperty("searchGuest", "Use search bar or press Ctrl+F");
        shortcuts.addProperty("checkIn", "Find reservation → Click 'Check In'");
        shortcuts.addProperty("checkOut", "Find reservation → Generate Bill → Click 'Check Out'");
        shortcuts.addProperty("printBill", "Open Bill → Click 'Print'");
        quickRef.add("shortcuts", shortcuts);
        
        JsonObject statusCodes = new JsonObject();
        statusCodes.addProperty("CONFIRMED", "Reservation is confirmed, awaiting check-in");
        statusCodes.addProperty("CHECKED_IN", "Guest has checked in");
        statusCodes.addProperty("CHECKED_OUT", "Guest has checked out");
        statusCodes.addProperty("CANCELLED", "Reservation was cancelled");
        quickRef.add("statusCodes", statusCodes);
        
        JsonObject contacts = new JsonObject();
        contacts.addProperty("frontDesk", "+94 91 2234567");
        contacts.addProperty("manager", "+94 91 2234568");
        contacts.addProperty("itSupport", "+94 91 2234569");
        contacts.addProperty("emergency", "119");
        quickRef.add("contacts", contacts);
        
        return Response.ok(quickRef.toString()).build();
    }
    
    /**
     * Get system information
     * GET /api/help/system-info
     */
    @GET
    @Path("/system-info")
    public Response getSystemInfo() {
        JsonObject info = new JsonObject();
        info.addProperty("systemName", "Ocean View Resort Reservation System");
        info.addProperty("version", "1.0.0");
        info.addProperty("developer", "Ocean View Resort Development Team");
        info.addProperty("lastUpdated", "January 2026");
        info.addProperty("technology", "Java EE Web Services (JAX-RS)");
        info.addProperty("database", "Apache Derby");
        
        JsonArray features = new JsonArray();
        features.add("User Authentication");
        features.add("Guest Management");
        features.add("Room Management");
        features.add("Reservation Management");
        features.add("Billing System");
        features.add("Reports Generation");
        features.add("Help Documentation");
        info.add("features", features);
        
        JsonArray designPatterns = new JsonArray();
        designPatterns.add("Singleton Pattern - Database Connection, Services");
        designPatterns.add("DAO Pattern - Data Access Objects");
        designPatterns.add("Factory Pattern - DAO Factory");
        designPatterns.add("MVC Pattern - Model-View-Controller");
        designPatterns.add("Service/Facade Pattern - Business Logic");
        info.add("designPatterns", designPatterns);
        
        return Response.ok(info.toString()).build();
    }
}
