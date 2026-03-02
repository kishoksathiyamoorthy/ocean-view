package com.oceanview.webservice;

import com.oceanview.model.Reservation;
import com.oceanview.service.ReservationService;
import com.oceanview.util.ValidationUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Reservation REST Resource.
 * Provides endpoints for reservation management.
 * This is the core resource for the booking system.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
@Path("/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {
    
    private final ReservationService reservationService;
    private final Gson gson;
    private final SimpleDateFormat dateFormat;
    
    public ReservationResource() {
        this.reservationService = ReservationService.getInstance();
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }
    
    /**
     * Get all reservations
     * GET /api/reservations
     */
    @GET
    public Response getAllReservations() {
        try {
            List<Reservation> reservations = reservationService.getAllReservations();
            return Response.ok(gson.toJson(reservations)).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve reservations: " + e.getMessage());
        }
    }
    
    /**
     * Get reservation by ID
     * GET /api/reservations/{id}
     */
    @GET
    @Path("/{id}")
    public Response getReservationById(@PathParam("id") int reservationId) {
        try {
            Reservation reservation = reservationService.getReservationById(reservationId);
            
            if (reservation != null) {
                return Response.ok(gson.toJson(reservation)).build();
            } else {
                return createErrorResponse(Response.Status.NOT_FOUND, "Reservation not found");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve reservation: " + e.getMessage());
        }
    }
    
    /**
     * Get reservation by reservation number
     * GET /api/reservations/number/{reservationNumber}
     */
    @GET
    @Path("/number/{reservationNumber}")
    public Response getReservationByNumber(@PathParam("reservationNumber") String reservationNumber) {
        try {
            Reservation reservation = reservationService.getReservationByNumber(reservationNumber);
            
            if (reservation != null) {
                return Response.ok(gson.toJson(reservation)).build();
            } else {
                return createErrorResponse(Response.Status.NOT_FOUND, "Reservation not found");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve reservation: " + e.getMessage());
        }
    }
    
    /**
     * Get reservations by status
     * GET /api/reservations/status/{status}
     */
    @GET
    @Path("/status/{status}")
    public Response getReservationsByStatus(@PathParam("status") String status) {
        try {
            List<Reservation> reservations = reservationService.getReservationsByStatus(status.toUpperCase());
            return Response.ok(gson.toJson(reservations)).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve reservations: " + e.getMessage());
        }
    }
    
    /**
     * Get reservations by guest
     * GET /api/reservations/guest/{guestId}
     */
    @GET
    @Path("/guest/{guestId}")
    public Response getReservationsByGuest(@PathParam("guestId") int guestId) {
        try {
            List<Reservation> reservations = reservationService.getReservationsByGuest(guestId);
            return Response.ok(gson.toJson(reservations)).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve reservations: " + e.getMessage());
        }
    }
    
    /**
     * Get reservations by date range
     * GET /api/reservations/date-range?startDate={date}&endDate={date}
     */
    @GET
    @Path("/date-range")
    public Response getReservationsByDateRange(
            @QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr) {
        try {
            if (startDateStr == null || endDateStr == null) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Start date and end date are required");
            }
            
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);
            
            List<Reservation> reservations = reservationService.getReservationsByDateRange(startDate, endDate);
            return Response.ok(gson.toJson(reservations)).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.BAD_REQUEST, "Invalid date format. Use yyyy-MM-dd");
        }
    }
    
    /**
     * Get today's check-ins
     * GET /api/reservations/today/check-ins
     */
    @GET
    @Path("/today/check-ins")
    public Response getTodayCheckIns() {
        try {
            List<Reservation> reservations = reservationService.getTodayCheckIns();
            return Response.ok(gson.toJson(reservations)).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve check-ins: " + e.getMessage());
        }
    }
    
    /**
     * Get today's check-outs
     * GET /api/reservations/today/check-outs
     */
    @GET
    @Path("/today/check-outs")
    public Response getTodayCheckOuts() {
        try {
            List<Reservation> reservations = reservationService.getTodayCheckOuts();
            return Response.ok(gson.toJson(reservations)).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve check-outs: " + e.getMessage());
        }
    }
    
    /**
     * Create new reservation
     * POST /api/reservations
     */
    @POST
    public Response createReservation(String jsonBody) {
        try {
            JsonObject json = gson.fromJson(jsonBody, JsonObject.class);
            
            // Parse and validate input
            if (!json.has("guestId") || json.get("guestId").getAsInt() <= 0) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Valid guest ID is required");
            }
            
            if (!json.has("roomId") || json.get("roomId").getAsInt() <= 0) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Valid room ID is required");
            }
            
            if (!json.has("checkInDate") || !json.has("checkOutDate")) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Check-in and check-out dates are required");
            }
            
            Date checkInDate = dateFormat.parse(json.get("checkInDate").getAsString());
            Date checkOutDate = dateFormat.parse(json.get("checkOutDate").getAsString());
            
            if (!ValidationUtil.isValidDateRange(checkInDate, checkOutDate)) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Check-out date must be after check-in date");
            }
            
            int numberOfGuests = json.has("numberOfGuests") ? json.get("numberOfGuests").getAsInt() : 1;
            if (numberOfGuests < 1) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Number of guests must be at least 1");
            }
            
            // Verify guest exists
            if (reservationService.getGuestById(json.get("guestId").getAsInt()) == null) {
                return createErrorResponse(Response.Status.NOT_FOUND, "Guest not found");
            }
            
            // Verify room exists
            if (reservationService.getRoomById(json.get("roomId").getAsInt()) == null) {
                return createErrorResponse(Response.Status.NOT_FOUND, "Room not found");
            }
            
            // Create reservation object
            Reservation reservation = new Reservation();
            reservation.setGuestId(json.get("guestId").getAsInt());
            reservation.setRoomId(json.get("roomId").getAsInt());
            reservation.setCheckInDate(checkInDate);
            reservation.setCheckOutDate(checkOutDate);
            reservation.setNumberOfGuests(numberOfGuests);
            reservation.setStatus("CONFIRMED");
            
            if (json.has("specialRequests")) {
                reservation.setSpecialRequests(ValidationUtil.sanitizeInput(json.get("specialRequests").getAsString()));
            }
            
            Reservation createdReservation = reservationService.createReservation(reservation);
            
            if (createdReservation != null) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Reservation created successfully");
                response.addProperty("reservationId", createdReservation.getReservationId());
                response.addProperty("reservationNumber", createdReservation.getReservationNumber());
                response.add("reservation", gson.toJsonTree(createdReservation));
                
                return Response.status(Response.Status.CREATED).entity(response.toString()).build();
            } else {
                return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to create reservation");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to create reservation: " + e.getMessage());
        }
    }
    
    /**
     * Update reservation
     * PUT /api/reservations/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateReservation(@PathParam("id") int reservationId, String jsonBody) {
        try {
            Reservation existingReservation = reservationService.getReservationById(reservationId);
            if (existingReservation == null) {
                return createErrorResponse(Response.Status.NOT_FOUND, "Reservation not found");
            }
            
            JsonObject json = gson.fromJson(jsonBody, JsonObject.class);
            
            // Update fields if provided
            if (json.has("checkInDate")) {
                existingReservation.setCheckInDate(dateFormat.parse(json.get("checkInDate").getAsString()));
            }
            if (json.has("checkOutDate")) {
                existingReservation.setCheckOutDate(dateFormat.parse(json.get("checkOutDate").getAsString()));
            }
            if (json.has("numberOfGuests")) {
                existingReservation.setNumberOfGuests(json.get("numberOfGuests").getAsInt());
            }
            if (json.has("specialRequests")) {
                existingReservation.setSpecialRequests(ValidationUtil.sanitizeInput(json.get("specialRequests").getAsString()));
            }
            if (json.has("roomId")) {
                existingReservation.setRoomId(json.get("roomId").getAsInt());
            }
            
            boolean updated = reservationService.updateReservation(existingReservation);
            
            if (updated) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Reservation updated successfully");
                return Response.ok(response.toString()).build();
            } else {
                return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to update reservation");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to update reservation: " + e.getMessage());
        }
    }
    
    /**
     * Check-in guest
     * PUT /api/reservations/{id}/check-in
     */
    @PUT
    @Path("/{id}/check-in")
    public Response checkIn(@PathParam("id") int reservationId) {
        try {
            Reservation reservation = reservationService.getReservationById(reservationId);
            if (reservation == null) {
                return createErrorResponse(Response.Status.NOT_FOUND, "Reservation not found");
            }
            
            if (!"CONFIRMED".equals(reservation.getStatus())) {
                return createErrorResponse(Response.Status.BAD_REQUEST, 
                    "Cannot check-in. Current status: " + reservation.getStatus());
            }
            
            boolean success = reservationService.checkIn(reservationId);
            
            if (success) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Guest checked in successfully");
                return Response.ok(response.toString()).build();
            } else {
                return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Check-in failed");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Check-in failed: " + e.getMessage());
        }
    }
    
    /**
     * Check-out guest
     * PUT /api/reservations/{id}/check-out
     */
    @PUT
    @Path("/{id}/check-out")
    public Response checkOut(@PathParam("id") int reservationId) {
        try {
            Reservation reservation = reservationService.getReservationById(reservationId);
            if (reservation == null) {
                return createErrorResponse(Response.Status.NOT_FOUND, "Reservation not found");
            }
            
            if (!"CHECKED_IN".equals(reservation.getStatus())) {
                return createErrorResponse(Response.Status.BAD_REQUEST, 
                    "Cannot check-out. Current status: " + reservation.getStatus());
            }
            
            boolean success = reservationService.checkOut(reservationId);
            
            if (success) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Guest checked out successfully");
                return Response.ok(response.toString()).build();
            } else {
                return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Check-out failed");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Check-out failed: " + e.getMessage());
        }
    }
    
    /**
     * Cancel reservation
     * PUT /api/reservations/{id}/cancel
     */
    @PUT
    @Path("/{id}/cancel")
    public Response cancelReservation(@PathParam("id") int reservationId) {
        try {
            Reservation reservation = reservationService.getReservationById(reservationId);
            if (reservation == null) {
                return createErrorResponse(Response.Status.NOT_FOUND, "Reservation not found");
            }
            
            if ("CHECKED_OUT".equals(reservation.getStatus()) || "CANCELLED".equals(reservation.getStatus())) {
                return createErrorResponse(Response.Status.BAD_REQUEST, 
                    "Cannot cancel. Current status: " + reservation.getStatus());
            }
            
            boolean success = reservationService.cancelReservation(reservationId);
            
            if (success) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Reservation cancelled successfully");
                return Response.ok(response.toString()).build();
            } else {
                return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Cancellation failed");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Cancellation failed: " + e.getMessage());
        }
    }
    
    /**
     * Delete reservation
     * DELETE /api/reservations/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteReservation(@PathParam("id") int reservationId) {
        try {
            boolean deleted = reservationService.deleteReservation(reservationId);
            
            if (deleted) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Reservation deleted successfully");
                return Response.ok(response.toString()).build();
            } else {
                return createErrorResponse(Response.Status.NOT_FOUND, "Reservation not found");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to delete reservation: " + e.getMessage());
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
