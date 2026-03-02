package com.oceanview.webservice;

import com.oceanview.model.Bill;
import com.oceanview.model.Reservation;
import com.oceanview.service.ReservationService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Bill REST Resource.
 * Provides endpoints for billing and payment operations.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
@Path("/bills")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BillResource {
    
    private final ReservationService reservationService;
    private final Gson gson;
    private final SimpleDateFormat dateFormat;
    
    public BillResource() {
        this.reservationService = ReservationService.getInstance();
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }
    
    /**
     * Get all bills
     * GET /api/bills
     */
    @GET
    public Response getAllBills() {
        try {
            List<Bill> bills = reservationService.getAllBills();
            return Response.ok(gson.toJson(bills)).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve bills: " + e.getMessage());
        }
    }
    
    /**
     * Get bill by ID
     * GET /api/bills/{id}
     */
    @GET
    @Path("/{id}")
    public Response getBillById(@PathParam("id") int billId) {
        try {
            Bill bill = reservationService.getBillById(billId);
            
            if (bill != null) {
                return Response.ok(gson.toJson(bill)).build();
            } else {
                return createErrorResponse(Response.Status.NOT_FOUND, "Bill not found");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve bill: " + e.getMessage());
        }
    }
    
    /**
     * Get bill by bill number
     * GET /api/bills/number/{billNumber}
     */
    @GET
    @Path("/number/{billNumber}")
    public Response getBillByNumber(@PathParam("billNumber") String billNumber) {
        try {
            Bill bill = reservationService.getBillByNumber(billNumber);
            
            if (bill != null) {
                return Response.ok(gson.toJson(bill)).build();
            } else {
                return createErrorResponse(Response.Status.NOT_FOUND, "Bill not found");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve bill: " + e.getMessage());
        }
    }
    
    /**
     * Get bill by reservation ID
     * GET /api/bills/reservation/{reservationId}
     */
    @GET
    @Path("/reservation/{reservationId}")
    public Response getBillByReservation(@PathParam("reservationId") int reservationId) {
        try {
            Bill bill = reservationService.getBillByReservation(reservationId);
            
            if (bill != null) {
                return Response.ok(gson.toJson(bill)).build();
            } else {
                return createErrorResponse(Response.Status.NOT_FOUND, "Bill not found for this reservation");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve bill: " + e.getMessage());
        }
    }
    
    /**
     * Get pending bills
     * GET /api/bills/pending
     */
    @GET
    @Path("/pending")
    public Response getPendingBills() {
        try {
            List<Bill> bills = reservationService.getPendingBills();
            return Response.ok(gson.toJson(bills)).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve pending bills: " + e.getMessage());
        }
    }
    
    /**
     * Generate bill for reservation
     * POST /api/bills/generate
     */
    @POST
    @Path("/generate")
    public Response generateBill(String jsonBody) {
        try {
            JsonObject json = gson.fromJson(jsonBody, JsonObject.class);
            
            if (!json.has("reservationId")) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Reservation ID is required");
            }
            
            int reservationId = json.get("reservationId").getAsInt();
            double discountPercent = json.has("discountPercent") ? json.get("discountPercent").getAsDouble() : 0;
            
            // Verify reservation exists
            Reservation reservation = reservationService.getReservationById(reservationId);
            if (reservation == null) {
                return createErrorResponse(Response.Status.NOT_FOUND, "Reservation not found");
            }
            
            Bill bill = reservationService.generateBill(reservationId, discountPercent);
            
            if (bill != null) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Bill generated successfully");
                response.addProperty("billId", bill.getBillId());
                response.addProperty("billNumber", bill.getBillNumber());
                response.add("bill", gson.toJsonTree(bill));
                
                return Response.status(Response.Status.CREATED).entity(response.toString()).build();
            } else {
                return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to generate bill");
            }
        } catch (IllegalArgumentException e) {
            return createErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to generate bill: " + e.getMessage());
        }
    }
    
    /**
     * Calculate bill preview (without saving)
     * POST /api/bills/calculate
     */
    @POST
    @Path("/calculate")
    public Response calculateBill(String jsonBody) {
        try {
            JsonObject json = gson.fromJson(jsonBody, JsonObject.class);
            
            if (!json.has("reservationId")) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Reservation ID is required");
            }
            
            int reservationId = json.get("reservationId").getAsInt();
            double discountPercent = json.has("discountPercent") ? json.get("discountPercent").getAsDouble() : 0;
            
            Reservation reservation = reservationService.getReservationById(reservationId);
            if (reservation == null) {
                return createErrorResponse(Response.Status.NOT_FOUND, "Reservation not found");
            }
            
            // Calculate bill without saving
            long nights = reservation.getNumberOfNights();
            double ratePerNight = reservation.getRoom().getRatePerNight();
            double roomCharges = nights * ratePerNight;
            double serviceCharges = roomCharges * 0.10; // 10% service charge
            double subtotal = roomCharges + serviceCharges;
            double discountAmount = subtotal * (discountPercent / 100);
            double afterDiscount = subtotal - discountAmount;
            double taxAmount = afterDiscount * 0.10; // 10% tax
            double totalAmount = afterDiscount + taxAmount;
            
            JsonObject response = new JsonObject();
            response.addProperty("success", true);
            response.addProperty("reservationNumber", reservation.getReservationNumber());
            response.addProperty("guestName", reservation.getGuest().getFullName());
            response.addProperty("roomNumber", reservation.getRoom().getRoomNumber());
            response.addProperty("roomType", reservation.getRoom().getRoomType());
            response.addProperty("checkInDate", dateFormat.format(reservation.getCheckInDate()));
            response.addProperty("checkOutDate", dateFormat.format(reservation.getCheckOutDate()));
            response.addProperty("numberOfNights", nights);
            response.addProperty("ratePerNight", ratePerNight);
            response.addProperty("roomCharges", roomCharges);
            response.addProperty("serviceCharges", serviceCharges);
            response.addProperty("discountPercent", discountPercent);
            response.addProperty("discountAmount", discountAmount);
            response.addProperty("taxAmount", taxAmount);
            response.addProperty("totalAmount", totalAmount);
            
            return Response.ok(response.toString()).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to calculate bill: " + e.getMessage());
        }
    }
    
    /**
     * Update payment status
     * PUT /api/bills/{id}/payment
     */
    @PUT
    @Path("/{id}/payment")
    public Response updatePaymentStatus(@PathParam("id") int billId, String jsonBody) {
        try {
            JsonObject json = gson.fromJson(jsonBody, JsonObject.class);
            
            if (!json.has("paymentStatus")) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Payment status is required");
            }
            
            String paymentStatus = json.get("paymentStatus").getAsString().toUpperCase();
            String paymentMethod = json.has("paymentMethod") ? json.get("paymentMethod").getAsString().toUpperCase() : null;
            
            // Validate payment status
            if (!paymentStatus.equals("PENDING") && !paymentStatus.equals("PAID") && !paymentStatus.equals("PARTIAL")) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Invalid payment status. Valid values: PENDING, PAID, PARTIAL");
            }
            
            // Validate payment method
            if (paymentMethod != null && !paymentMethod.equals("CASH") && !paymentMethod.equals("CARD") && !paymentMethod.equals("BANK_TRANSFER")) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Invalid payment method. Valid values: CASH, CARD, BANK_TRANSFER");
            }
            
            boolean updated = reservationService.updatePaymentStatus(billId, paymentStatus, paymentMethod);
            
            if (updated) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Payment status updated successfully");
                return Response.ok(response.toString()).build();
            } else {
                return createErrorResponse(Response.Status.NOT_FOUND, "Bill not found");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to update payment: " + e.getMessage());
        }
    }
    
    /**
     * Print bill (get printable format)
     * GET /api/bills/{id}/print
     */
    @GET
    @Path("/{id}/print")
    @Produces(MediaType.TEXT_HTML)
    public Response printBill(@PathParam("id") int billId) {
        try {
            Bill bill = reservationService.getBillById(billId);
            
            if (bill == null) {
                return createErrorResponse(Response.Status.NOT_FOUND, "Bill not found");
            }
            
            Reservation reservation = reservationService.getReservationById(bill.getReservationId());
            bill.setReservation(reservation);
            
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head>");
            html.append("<title>Bill - ").append(bill.getBillNumber()).append("</title>");
            html.append("<style>");
            html.append("body { font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; }");
            html.append(".header { text-align: center; border-bottom: 2px solid #333; padding-bottom: 20px; }");
            html.append(".header h1 { color: #0066cc; margin: 0; }");
            html.append(".header p { margin: 5px 0; color: #666; }");
            html.append(".bill-info { display: flex; justify-content: space-between; margin: 20px 0; }");
            html.append(".section { margin: 20px 0; }");
            html.append(".section h3 { border-bottom: 1px solid #ccc; padding-bottom: 5px; }");
            html.append("table { width: 100%; border-collapse: collapse; }");
            html.append("th, td { padding: 10px; text-align: left; border-bottom: 1px solid #eee; }");
            html.append(".amount-row td { font-weight: bold; }");
            html.append(".total-row { background: #f5f5f5; }");
            html.append(".total-row td { font-size: 1.2em; }");
            html.append(".footer { text-align: center; margin-top: 40px; padding-top: 20px; border-top: 1px solid #ccc; }");
            html.append("@media print { button { display: none; } }");
            html.append("</style></head><body>");
            
            // Header
            html.append("<div class='header'>");
            html.append("<h1>🏨 Ocean View Resort</h1>");
            html.append("<p>Beachside Paradise, Galle, Sri Lanka</p>");
            html.append("<p>Tel: +94 91 2234567 | Email: info@oceanviewresort.lk</p>");
            html.append("</div>");
            
            // Bill Info
            html.append("<div class='bill-info'>");
            html.append("<div><strong>Bill Number:</strong> ").append(bill.getBillNumber()).append("</div>");
            html.append("<div><strong>Date:</strong> ").append(dateFormat.format(bill.getBillDate())).append("</div>");
            html.append("</div>");
            
            // Guest Info
            html.append("<div class='section'>");
            html.append("<h3>Guest Information</h3>");
            html.append("<p><strong>Name:</strong> ").append(reservation.getGuest().getFullName()).append("</p>");
            html.append("<p><strong>Contact:</strong> ").append(reservation.getGuest().getContactNumber()).append("</p>");
            html.append("<p><strong>Reservation #:</strong> ").append(reservation.getReservationNumber()).append("</p>");
            html.append("</div>");
            
            // Stay Details
            html.append("<div class='section'>");
            html.append("<h3>Stay Details</h3>");
            html.append("<p><strong>Room:</strong> ").append(reservation.getRoom().getRoomNumber());
            html.append(" (").append(reservation.getRoom().getRoomType()).append(")</p>");
            html.append("<p><strong>Check-in:</strong> ").append(dateFormat.format(reservation.getCheckInDate())).append("</p>");
            html.append("<p><strong>Check-out:</strong> ").append(dateFormat.format(reservation.getCheckOutDate())).append("</p>");
            html.append("<p><strong>Number of Nights:</strong> ").append(reservation.getNumberOfNights()).append("</p>");
            html.append("</div>");
            
            // Charges
            html.append("<div class='section'>");
            html.append("<h3>Charges</h3>");
            html.append("<table>");
            html.append("<tr><td>Room Charges (").append(reservation.getNumberOfNights());
            html.append(" nights × LKR ").append(String.format("%,.2f", reservation.getRoom().getRatePerNight())).append(")</td>");
            html.append("<td style='text-align:right'>LKR ").append(String.format("%,.2f", bill.getRoomCharges())).append("</td></tr>");
            html.append("<tr><td>Service Charges (10%)</td>");
            html.append("<td style='text-align:right'>LKR ").append(String.format("%,.2f", bill.getServiceCharges())).append("</td></tr>");
            
            if (bill.getDiscountAmount() > 0) {
                html.append("<tr><td>Discount</td>");
                html.append("<td style='text-align:right'>- LKR ").append(String.format("%,.2f", bill.getDiscountAmount())).append("</td></tr>");
            }
            
            html.append("<tr><td>Tax (10%)</td>");
            html.append("<td style='text-align:right'>LKR ").append(String.format("%,.2f", bill.getTaxAmount())).append("</td></tr>");
            html.append("<tr class='total-row'><td><strong>TOTAL AMOUNT</strong></td>");
            html.append("<td style='text-align:right'><strong>LKR ").append(String.format("%,.2f", bill.getTotalAmount())).append("</strong></td></tr>");
            html.append("</table>");
            html.append("</div>");
            
            // Payment Info
            html.append("<div class='section'>");
            html.append("<h3>Payment Information</h3>");
            html.append("<p><strong>Status:</strong> ").append(bill.getPaymentStatus()).append("</p>");
            if (bill.getPaymentMethod() != null) {
                html.append("<p><strong>Method:</strong> ").append(bill.getPaymentMethod()).append("</p>");
            }
            html.append("</div>");
            
            // Footer
            html.append("<div class='footer'>");
            html.append("<p>Thank you for staying at Ocean View Resort!</p>");
            html.append("<p>We hope to see you again soon.</p>");
            html.append("<button onclick='window.print()'>Print Bill</button>");
            html.append("</div>");
            
            html.append("</body></html>");
            
            return Response.ok(html.toString()).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to print bill: " + e.getMessage());
        }
    }
    
    /**
     * Create error response helper
     */
    private Response createErrorResponse(Response.Status status, String message) {
        JsonObject error = new JsonObject();
        error.addProperty("success", false);
        error.addProperty("error", message);
        return Response.status(status).entity(error.toString()).build();
    }
}
