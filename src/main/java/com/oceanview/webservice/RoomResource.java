package com.oceanview.webservice;

import com.oceanview.model.Room;
import com.oceanview.service.ReservationService;
import com.oceanview.util.ValidationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Room REST Resource.
 * Provides endpoints for room management.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    
    private final ReservationService reservationService;
    private final Gson gson;
    private final SimpleDateFormat dateFormat;
    
    public RoomResource() {
        this.reservationService = ReservationService.getInstance();
        this.gson = new Gson();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }
    
    /**
     * Get all rooms
     * GET /api/rooms
     */
    @GET
    public Response getAllRooms() {
        try {
            List<Room> rooms = reservationService.getAllRooms();
            return Response.ok(gson.toJson(rooms)).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve rooms: " + e.getMessage());
        }
    }
    
    /**
     * Get room by ID
     * GET /api/rooms/{id}
     */
    @GET
    @Path("/{id}")
    public Response getRoomById(@PathParam("id") int roomId) {
        try {
            Room room = reservationService.getRoomById(roomId);
            
            if (room != null) {
                return Response.ok(gson.toJson(room)).build();
            } else {
                return createErrorResponse(Response.Status.NOT_FOUND, "Room not found");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve room: " + e.getMessage());
        }
    }
    
    /**
     * Get available rooms
     * GET /api/rooms/available
     */
    @GET
    @Path("/available")
    public Response getAvailableRooms() {
        try {
            List<Room> rooms = reservationService.getAvailableRooms();
            return Response.ok(gson.toJson(rooms)).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve available rooms: " + e.getMessage());
        }
    }
    
    /**
     * Get rooms by type
     * GET /api/rooms/type/{roomType}
     */
    @GET
    @Path("/type/{roomType}")
    public Response getRoomsByType(@PathParam("roomType") String roomType) {
        try {
            List<Room> rooms = reservationService.getRoomsByType(roomType.toUpperCase());
            return Response.ok(gson.toJson(rooms)).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve rooms: " + e.getMessage());
        }
    }
    
    /**
     * Check room availability for dates
     * GET /api/rooms/check-availability?checkIn={date}&checkOut={date}&roomType={type}
     */
    @GET
    @Path("/check-availability")
    public Response checkAvailability(
            @QueryParam("checkIn") String checkInStr,
            @QueryParam("checkOut") String checkOutStr,
            @QueryParam("roomType") String roomType) {
        try {
            if (checkInStr == null || checkOutStr == null) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Check-in and check-out dates are required");
            }
            
            Date checkIn = dateFormat.parse(checkInStr);
            Date checkOut = dateFormat.parse(checkOutStr);
            
            if (!ValidationUtil.isValidDateRange(checkIn, checkOut)) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Check-out date must be after check-in date");
            }
            
            List<Room> rooms;
            if (roomType != null && !roomType.trim().isEmpty()) {
                rooms = reservationService.getAvailableRoomsByTypeForDates(roomType.toUpperCase(), checkIn, checkOut);
            } else {
                rooms = reservationService.getAvailableRoomsForDates(checkIn, checkOut);
            }
            
            return Response.ok(gson.toJson(rooms)).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.BAD_REQUEST, "Invalid date format. Use yyyy-MM-dd");
        }
    }
    
    /**
     * Get room types with rates
     * GET /api/rooms/types
     */
    @GET
    @Path("/types")
    public Response getRoomTypes() {
        JsonObject types = new JsonObject();
        
        JsonObject single = new JsonObject();
        single.addProperty("name", "Single Room");
        single.addProperty("rate", 5000.00);
        single.addProperty("maxOccupancy", 1);
        types.add("SINGLE", single);
        
        JsonObject doubleRoom = new JsonObject();
        doubleRoom.addProperty("name", "Double Room");
        doubleRoom.addProperty("rate", 8000.00);
        doubleRoom.addProperty("maxOccupancy", 2);
        types.add("DOUBLE", doubleRoom);
        
        JsonObject deluxe = new JsonObject();
        deluxe.addProperty("name", "Deluxe Room");
        deluxe.addProperty("rate", 12000.00);
        deluxe.addProperty("maxOccupancy", 3);
        types.add("DELUXE", deluxe);
        
        JsonObject suite = new JsonObject();
        suite.addProperty("name", "Suite");
        suite.addProperty("rate", 20000.00);
        suite.addProperty("maxOccupancy", 4);
        types.add("SUITE", suite);
        
        JsonObject family = new JsonObject();
        family.addProperty("name", "Family Room");
        family.addProperty("rate", 15000.00);
        family.addProperty("maxOccupancy", 5);
        types.add("FAMILY", family);
        
        return Response.ok(types.toString()).build();
    }
    
    /**
     * Create new room
     * POST /api/rooms
     */
    @POST
    public Response createRoom(String jsonBody) {
        try {
            Room room = gson.fromJson(jsonBody, Room.class);
            
            // Validate input
            if (!ValidationUtil.isNotEmpty(room.getRoomNumber())) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Room number is required");
            }
            
            if (!ValidationUtil.isNotEmpty(room.getRoomType())) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Room type is required");
            }
            
            if (!ValidationUtil.isPositiveNumber(room.getRatePerNight())) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Rate per night must be a positive number");
            }
            
            if (!ValidationUtil.isPositiveInteger(room.getMaxOccupancy())) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Max occupancy must be a positive number");
            }
            
            // Check if room number already exists
            if (reservationService.getRoomByNumber(room.getRoomNumber()) != null) {
                return createErrorResponse(Response.Status.CONFLICT, "Room number already exists");
            }
            
            room.setAvailable(true);
            Room createdRoom = reservationService.createRoom(room);
            
            if (createdRoom != null) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Room created successfully");
                response.addProperty("roomId", createdRoom.getRoomId());
                response.add("room", gson.toJsonTree(createdRoom));
                
                return Response.status(Response.Status.CREATED).entity(response.toString()).build();
            } else {
                return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to create room");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to create room: " + e.getMessage());
        }
    }
    
    /**
     * Update room
     * PUT /api/rooms/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateRoom(@PathParam("id") int roomId, String jsonBody) {
        try {
            Room room = gson.fromJson(jsonBody, Room.class);
            room.setRoomId(roomId);
            
            boolean updated = reservationService.updateRoom(room);
            
            if (updated) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Room updated successfully");
                return Response.ok(response.toString()).build();
            } else {
                return createErrorResponse(Response.Status.NOT_FOUND, "Room not found");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to update room: " + e.getMessage());
        }
    }
    
    /**
     * Delete room
     * DELETE /api/rooms/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") int roomId) {
        try {
            boolean deleted = reservationService.deleteRoom(roomId);
            
            if (deleted) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Room deleted successfully");
                return Response.ok(response.toString()).build();
            } else {
                return createErrorResponse(Response.Status.NOT_FOUND, "Room not found or cannot be deleted");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to delete room: " + e.getMessage());
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
