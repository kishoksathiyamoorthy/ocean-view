package com.oceanview.webservice;

import com.oceanview.model.User;
import com.oceanview.service.AuthService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Authentication REST Resource.
 * Provides endpoints for user authentication and management.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    
    private final AuthService authService;
    private final Gson gson;
    
    public AuthResource() {
        this.authService = AuthService.getInstance();
        this.gson = new Gson();
    }
    
    /**
     * User login endpoint
     * POST /api/auth/login
     */
    @POST
    @Path("/login")
    public Response login(String jsonBody) {
        try {
            JsonObject json = gson.fromJson(jsonBody, JsonObject.class);
            String username = json.get("username").getAsString();
            String password = json.get("password").getAsString();
            
            if (username == null || username.trim().isEmpty()) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Username is required");
            }
            if (password == null || password.trim().isEmpty()) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Password is required");
            }
            
            User user = authService.login(username, password);
            
            if (user != null) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Login successful");
                response.addProperty("userId", user.getUserId());
                response.addProperty("username", user.getUsername());
                response.addProperty("fullName", user.getFullName());
                response.addProperty("role", user.getRole());
                
                return Response.ok(response.toString()).build();
            } else {
                return createErrorResponse(Response.Status.UNAUTHORIZED, "Invalid username or password");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Login failed: " + e.getMessage());
        }
    }
    
    /**
     * User logout endpoint
     * POST /api/auth/logout
     */
    @POST
    @Path("/logout")
    public Response logout() {
        authService.logout();
        
        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        response.addProperty("message", "Logout successful");
        
        return Response.ok(response.toString()).build();
    }
    
    /**
     * Get current user endpoint
     * GET /api/auth/current
     */
    @GET
    @Path("/current")
    public Response getCurrentUser() {
        User user = authService.getCurrentUser();
        
        if (user != null) {
            JsonObject response = new JsonObject();
            response.addProperty("success", true);
            response.addProperty("userId", user.getUserId());
            response.addProperty("username", user.getUsername());
            response.addProperty("fullName", user.getFullName());
            response.addProperty("role", user.getRole());
            
            return Response.ok(response.toString()).build();
        } else {
            return createErrorResponse(Response.Status.UNAUTHORIZED, "Not logged in");
        }
    }
    
    /**
     * Register new user endpoint
     * POST /api/auth/register
     */
    @POST
    @Path("/register")
    public Response register(String jsonBody) {
        try {
            User user = gson.fromJson(jsonBody, User.class);
            
            // Validate input
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Username is required");
            }
            if (user.getPassword() == null || user.getPassword().length() < 6) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Password must be at least 6 characters");
            }
            if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Full name is required");
            }
            
            // Set default values
            if (user.getRole() == null) {
                user.setRole("RECEPTIONIST");
            }
            user.setActive(true);
            
            User registeredUser = authService.registerUser(user);
            
            if (registeredUser != null) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "User registered successfully");
                response.addProperty("userId", registeredUser.getUserId());
                
                return Response.status(Response.Status.CREATED).entity(response.toString()).build();
            } else {
                return createErrorResponse(Response.Status.CONFLICT, "Username already exists");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Registration failed: " + e.getMessage());
        }
    }
    
    /**
     * Get all users endpoint (admin only)
     * GET /api/auth/users
     */
    @GET
    @Path("/users")
    public Response getAllUsers() {
        if (!authService.isAdmin()) {
            return createErrorResponse(Response.Status.FORBIDDEN, "Admin access required");
        }
        
        List<User> users = authService.getAllUsers();
        
        // Remove passwords from response
        for (User user : users) {
            user.setPassword(null);
        }
        
        return Response.ok(gson.toJson(users)).build();
    }
    
    /**
     * Update user endpoint
     * PUT /api/auth/users/{id}
     */
    @PUT
    @Path("/users/{id}")
    public Response updateUser(@PathParam("id") int userId, String jsonBody) {
        try {
            User user = gson.fromJson(jsonBody, User.class);
            user.setUserId(userId);
            
            boolean updated = authService.updateUser(user);
            
            if (updated) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "User updated successfully");
                return Response.ok(response.toString()).build();
            } else {
                return createErrorResponse(Response.Status.NOT_FOUND, "User not found");
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Update failed: " + e.getMessage());
        }
    }
    
    /**
     * Delete user endpoint (admin only)
     * DELETE /api/auth/users/{id}
     */
    @DELETE
    @Path("/users/{id}")
    public Response deleteUser(@PathParam("id") int userId) {
        if (!authService.isAdmin()) {
            return createErrorResponse(Response.Status.FORBIDDEN, "Admin access required");
        }
        
        boolean deleted = authService.deleteUser(userId);
        
        if (deleted) {
            JsonObject response = new JsonObject();
            response.addProperty("success", true);
            response.addProperty("message", "User deleted successfully");
            return Response.ok(response.toString()).build();
        } else {
            return createErrorResponse(Response.Status.NOT_FOUND, "User not found");
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
