package com.oceanview.webservice;

import com.oceanview.model.Guest;
import com.oceanview.service.ReservationService;
import com.oceanview.util.ValidationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Guest REST Resource.
 * Provides endpoints for guest management.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
@Path("/guests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GuestResource {
    
    private final ReservationService reservationService;
    private final Gson gson;
    
    public GuestResource() {
        this.reservationService = ReservationService.getInstance();
        this.gson = new Gson();
    }
    
    /**
     * Get all guests
     * GET /api/guests
     */
    @GET
    public Response getAllGuests() {
        try {
            List<Guest> guests = reservationService.getAllGuests();
            return Response.ok(gson.toJson(guests)).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve guests: " + e.getMessage());
        }
    }
    
    /**
     * Get guest by ID
     * GET /api/guests/{id}
     */
    @GET
    @Path("/{id}")
    public Response getGuestById(@PathParam("id") int guestId) {
        try {
            Guest guest = reservationService.getGuestById(guestId);
            
            if (guest != null) {
                return Response.ok(gson.toJson(guest)).build();
            } else {
                return createErrorResponse(Response.Status.NOT_FOUND, "Guest not found");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve guest: " + e.getMessage());
        }
    }
    
    /**
     * Search guests by name
     * GET /api/guests/search?name={name}
     */
    @GET
    @Path("/search")
    public Response searchGuests(@QueryParam("name") String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Search name is required");
            }
            
            List<Guest> guests = reservationService.searchGuestsByName(name);
            return Response.ok(gson.toJson(guests)).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Search failed: " + e.getMessage());
        }
    }
    
    /**
     * Get guest by contact number
     * GET /api/guests/contact/{contactNumber}
     */
    @GET
    @Path("/contact/{contactNumber}")
    public Response getGuestByContact(@PathParam("contactNumber") String contactNumber) {
        try {
            Guest guest = reservationService.getGuestByContact(contactNumber);
            
            if (guest != null) {
                return Response.ok(gson.toJson(guest)).build();
            } else {
                return createErrorResponse(Response.Status.NOT_FOUND, "Guest not found");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve guest: " + e.getMessage());
        }
    }
    
    /**
     * Create new guest
     * POST /api/guests
     */
    @POST
    public Response createGuest(String jsonBody) {
        try {
            Guest guest = gson.fromJson(jsonBody, Guest.class);
            
            // Validate input
            StringBuilder errors = new StringBuilder();
            
            if (!ValidationUtil.isNotEmpty(guest.getFirstName())) {
                errors.append("First name is required. ");
            } else if (!ValidationUtil.isValidName(guest.getFirstName())) {
                errors.append(ValidationUtil.getNameErrorMessage()).append(" ");
            }
            
            if (!ValidationUtil.isNotEmpty(guest.getLastName())) {
                errors.append("Last name is required. ");
            } else if (!ValidationUtil.isValidName(guest.getLastName())) {
                errors.append(ValidationUtil.getNameErrorMessage()).append(" ");
            }
            
            if (!ValidationUtil.isNotEmpty(guest.getContactNumber())) {
                errors.append("Contact number is required. ");
            } else if (!ValidationUtil.isValidPhoneNumber(guest.getContactNumber())) {
                errors.append(ValidationUtil.getPhoneErrorMessage()).append(" ");
            }
            
            if (ValidationUtil.isNotEmpty(guest.getEmail()) && !ValidationUtil.isValidEmail(guest.getEmail())) {
                errors.append(ValidationUtil.getEmailErrorMessage()).append(" ");
            }
            
            if (ValidationUtil.isNotEmpty(guest.getNicNumber()) && !ValidationUtil.isValidNIC(guest.getNicNumber())) {
                errors.append(ValidationUtil.getNICErrorMessage()).append(" ");
            }
            
            if (errors.length() > 0) {
                return createErrorResponse(Response.Status.BAD_REQUEST, errors.toString().trim());
            }
            
            // Sanitize input
            guest.setFirstName(ValidationUtil.sanitizeInput(guest.getFirstName()));
            guest.setLastName(ValidationUtil.sanitizeInput(guest.getLastName()));
            guest.setAddress(ValidationUtil.sanitizeInput(guest.getAddress()));
            
            Guest createdGuest = reservationService.createGuest(guest);
            
            if (createdGuest != null) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Guest created successfully");
                response.addProperty("guestId", createdGuest.getGuestId());
                response.add("guest", gson.toJsonTree(createdGuest));
                
                return Response.status(Response.Status.CREATED).entity(response.toString()).build();
            } else {
                return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to create guest");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to create guest: " + e.getMessage());
        }
    }
    
    /**
     * Update guest
     * PUT /api/guests/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateGuest(@PathParam("id") int guestId, String jsonBody) {
        try {
            Guest guest = gson.fromJson(jsonBody, Guest.class);
            guest.setGuestId(guestId);
            
            // Validate input
            if (ValidationUtil.isNotEmpty(guest.getContactNumber()) && !ValidationUtil.isValidPhoneNumber(guest.getContactNumber())) {
                return createErrorResponse(Response.Status.BAD_REQUEST, ValidationUtil.getPhoneErrorMessage());
            }
            
            if (ValidationUtil.isNotEmpty(guest.getEmail()) && !ValidationUtil.isValidEmail(guest.getEmail())) {
                return createErrorResponse(Response.Status.BAD_REQUEST, ValidationUtil.getEmailErrorMessage());
            }
            
            boolean updated = reservationService.updateGuest(guest);
            
            if (updated) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Guest updated successfully");
                return Response.ok(response.toString()).build();
            } else {
                return createErrorResponse(Response.Status.NOT_FOUND, "Guest not found");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to update guest: " + e.getMessage());
        }
    }
    
    /**
     * Delete guest
     * DELETE /api/guests/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteGuest(@PathParam("id") int guestId) {
        try {
            boolean deleted = reservationService.deleteGuest(guestId);
            
            if (deleted) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Guest deleted successfully");
                return Response.ok(response.toString()).build();
            } else {
                return createErrorResponse(Response.Status.NOT_FOUND, "Guest not found or cannot be deleted");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to delete guest: " + e.getMessage());
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
