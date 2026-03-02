package com.oceanview.webservice;

import com.oceanview.model.Bill;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import com.oceanview.service.ReservationService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Report REST Resource.
 * Provides endpoints for generating various reports.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
public class ReportResource {
    
    private final ReservationService reservationService;
    private final Gson gson;
    private final SimpleDateFormat dateFormat;
    
    public ReportResource() {
        this.reservationService = ReservationService.getInstance();
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }
    
    /**
     * Get dashboard summary
     * GET /api/reports/dashboard
     */
    @GET
    @Path("/dashboard")
    public Response getDashboard() {
        try {
            JsonObject dashboard = new JsonObject();
            dashboard.addProperty("generatedAt", dateFormat.format(new Date()));
            
            // Reservation statistics
            JsonObject reservationStats = new JsonObject();
            reservationStats.addProperty("total", reservationService.getReservationCount(null));
            reservationStats.addProperty("confirmed", reservationService.getReservationCount("CONFIRMED"));
            reservationStats.addProperty("checkedIn", reservationService.getReservationCount("CHECKED_IN"));
            reservationStats.addProperty("checkedOut", reservationService.getReservationCount("CHECKED_OUT"));
            reservationStats.addProperty("cancelled", reservationService.getReservationCount("CANCELLED"));
            dashboard.add("reservations", reservationStats);
            
            // Today's activity
            JsonObject todayActivity = new JsonObject();
            todayActivity.addProperty("checkIns", reservationService.getTodayCheckIns().size());
            todayActivity.addProperty("checkOuts", reservationService.getTodayCheckOuts().size());
            dashboard.add("todayActivity", todayActivity);
            
            // Room statistics
            List<Room> allRooms = reservationService.getAllRooms();
            List<Room> availableRooms = reservationService.getAvailableRooms();
            JsonObject roomStats = new JsonObject();
            roomStats.addProperty("totalRooms", allRooms.size());
            roomStats.addProperty("available", availableRooms.size());
            roomStats.addProperty("occupied", allRooms.size() - availableRooms.size());
            double occupancyRate = allRooms.size() > 0 ? 
                ((double)(allRooms.size() - availableRooms.size()) / allRooms.size()) * 100 : 0;
            roomStats.addProperty("occupancyRate", String.format("%.1f%%", occupancyRate));
            dashboard.add("rooms", roomStats);
            
            // Revenue statistics
            JsonObject revenueStats = new JsonObject();
            revenueStats.addProperty("totalRevenue", reservationService.getTotalRevenue(null));
            revenueStats.addProperty("paidRevenue", reservationService.getTotalRevenue("PAID"));
            revenueStats.addProperty("pendingRevenue", reservationService.getTotalRevenue("PENDING"));
            dashboard.add("revenue", revenueStats);
            
            // Guest statistics
            JsonObject guestStats = new JsonObject();
            guestStats.addProperty("totalGuests", reservationService.getAllGuests().size());
            dashboard.add("guests", guestStats);
            
            return Response.ok(dashboard.toString()).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to generate dashboard: " + e.getMessage());
        }
    }
    
    /**
     * Get daily report
     * GET /api/reports/daily
     */
    @GET
    @Path("/daily")
    public Response getDailyReport() {
        try {
            JsonObject report = new JsonObject();
            report.addProperty("reportType", "Daily Report");
            report.addProperty("date", dateFormat.format(new Date()));
            
            // Today's check-ins
            List<Reservation> checkIns = reservationService.getTodayCheckIns();
            JsonArray checkInsArray = new JsonArray();
            for (Reservation r : checkIns) {
                JsonObject res = new JsonObject();
                res.addProperty("reservationNumber", r.getReservationNumber());
                res.addProperty("guestName", r.getGuest().getFullName());
                res.addProperty("roomNumber", r.getRoom().getRoomNumber());
                res.addProperty("roomType", r.getRoom().getRoomType());
                checkInsArray.add(res);
            }
            report.add("expectedCheckIns", checkInsArray);
            report.addProperty("totalCheckIns", checkIns.size());
            
            // Today's check-outs
            List<Reservation> checkOuts = reservationService.getTodayCheckOuts();
            JsonArray checkOutsArray = new JsonArray();
            for (Reservation r : checkOuts) {
                JsonObject res = new JsonObject();
                res.addProperty("reservationNumber", r.getReservationNumber());
                res.addProperty("guestName", r.getGuest().getFullName());
                res.addProperty("roomNumber", r.getRoom().getRoomNumber());
                res.addProperty("roomType", r.getRoom().getRoomType());
                checkOutsArray.add(res);
            }
            report.add("expectedCheckOuts", checkOutsArray);
            report.addProperty("totalCheckOuts", checkOuts.size());
            
            // Current occupancy
            List<Reservation> currentlyCheckedIn = reservationService.getReservationsByStatus("CHECKED_IN");
            report.addProperty("currentlyOccupied", currentlyCheckedIn.size());
            
            // Available rooms
            List<Room> availableRooms = reservationService.getAvailableRooms();
            report.addProperty("availableRooms", availableRooms.size());
            
            return Response.ok(report.toString()).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to generate daily report: " + e.getMessage());
        }
    }
    
    /**
     * Get reservation report
     * GET /api/reports/reservations?startDate={date}&endDate={date}
     */
    @GET
    @Path("/reservations")
    public Response getReservationReport(
            @QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr) {
        try {
            JsonObject report = new JsonObject();
            report.addProperty("reportType", "Reservation Report");
            report.addProperty("generatedAt", dateFormat.format(new Date()));
            
            List<Reservation> reservations;
            if (startDateStr != null && endDateStr != null) {
                Date startDate = dateFormat.parse(startDateStr);
                Date endDate = dateFormat.parse(endDateStr);
                reservations = reservationService.getReservationsByDateRange(startDate, endDate);
                report.addProperty("startDate", startDateStr);
                report.addProperty("endDate", endDateStr);
            } else {
                reservations = reservationService.getAllReservations();
                report.addProperty("period", "All Time");
            }
            
            // Status breakdown
            Map<String, Integer> statusCount = new HashMap<>();
            for (Reservation r : reservations) {
                String status = r.getStatus();
                statusCount.put(status, statusCount.getOrDefault(status, 0) + 1);
            }
            
            JsonObject statusBreakdown = new JsonObject();
            for (Map.Entry<String, Integer> entry : statusCount.entrySet()) {
                statusBreakdown.addProperty(entry.getKey(), entry.getValue());
            }
            report.add("statusBreakdown", statusBreakdown);
            report.addProperty("totalReservations", reservations.size());
            
            // Reservation list
            JsonArray reservationList = new JsonArray();
            for (Reservation r : reservations) {
                JsonObject res = new JsonObject();
                res.addProperty("reservationNumber", r.getReservationNumber());
                res.addProperty("guestName", r.getGuest() != null ? r.getGuest().getFullName() : "N/A");
                res.addProperty("roomNumber", r.getRoom() != null ? r.getRoom().getRoomNumber() : "N/A");
                res.addProperty("roomType", r.getRoom() != null ? r.getRoom().getRoomType() : "N/A");
                res.addProperty("checkInDate", dateFormat.format(r.getCheckInDate()));
                res.addProperty("checkOutDate", dateFormat.format(r.getCheckOutDate()));
                res.addProperty("numberOfNights", r.getNumberOfNights());
                res.addProperty("status", r.getStatus());
                res.addProperty("totalAmount", r.getTotalAmount());
                reservationList.add(res);
            }
            report.add("reservations", reservationList);
            
            return Response.ok(report.toString()).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to generate reservation report: " + e.getMessage());
        }
    }
    
    /**
     * Get revenue report
     * GET /api/reports/revenue
     */
    @GET
    @Path("/revenue")
    public Response getRevenueReport() {
        try {
            JsonObject report = new JsonObject();
            report.addProperty("reportType", "Revenue Report");
            report.addProperty("generatedAt", dateFormat.format(new Date()));
            
            // Overall revenue
            double totalRevenue = reservationService.getTotalRevenue(null);
            double paidRevenue = reservationService.getTotalRevenue("PAID");
            double pendingRevenue = reservationService.getTotalRevenue("PENDING");
            
            report.addProperty("totalRevenue", totalRevenue);
            report.addProperty("paidRevenue", paidRevenue);
            report.addProperty("pendingRevenue", pendingRevenue);
            
            // Collection rate
            double collectionRate = totalRevenue > 0 ? (paidRevenue / totalRevenue) * 100 : 0;
            report.addProperty("collectionRate", String.format("%.1f%%", collectionRate));
            
            // Bill summary
            List<Bill> allBills = reservationService.getAllBills();
            List<Bill> pendingBills = reservationService.getPendingBills();
            
            report.addProperty("totalBills", allBills.size());
            report.addProperty("pendingBillsCount", pendingBills.size());
            
            // Pending bills details
            JsonArray pendingBillsArray = new JsonArray();
            for (Bill b : pendingBills) {
                JsonObject bill = new JsonObject();
                bill.addProperty("billNumber", b.getBillNumber());
                bill.addProperty("reservationId", b.getReservationId());
                bill.addProperty("totalAmount", b.getTotalAmount());
                bill.addProperty("billDate", dateFormat.format(b.getBillDate()));
                pendingBillsArray.add(bill);
            }
            report.add("pendingBills", pendingBillsArray);
            
            return Response.ok(report.toString()).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to generate revenue report: " + e.getMessage());
        }
    }
    
    /**
     * Get occupancy report
     * GET /api/reports/occupancy
     */
    @GET
    @Path("/occupancy")
    public Response getOccupancyReport() {
        try {
            JsonObject report = new JsonObject();
            report.addProperty("reportType", "Occupancy Report");
            report.addProperty("generatedAt", dateFormat.format(new Date()));
            
            List<Room> allRooms = reservationService.getAllRooms();
            List<Room> availableRooms = reservationService.getAvailableRooms();
            
            int totalRooms = allRooms.size();
            int availableCount = availableRooms.size();
            int occupiedCount = totalRooms - availableCount;
            
            report.addProperty("totalRooms", totalRooms);
            report.addProperty("occupied", occupiedCount);
            report.addProperty("available", availableCount);
            
            double occupancyRate = totalRooms > 0 ? ((double) occupiedCount / totalRooms) * 100 : 0;
            report.addProperty("occupancyRate", String.format("%.1f%%", occupancyRate));
            
            // Room type breakdown
            Map<String, int[]> roomTypeStats = new HashMap<>(); // [total, available]
            for (Room room : allRooms) {
                String type = room.getRoomType();
                int[] stats = roomTypeStats.getOrDefault(type, new int[]{0, 0});
                stats[0]++;
                if (room.isAvailable()) {
                    stats[1]++;
                }
                roomTypeStats.put(type, stats);
            }
            
            JsonObject roomTypeBreakdown = new JsonObject();
            for (Map.Entry<String, int[]> entry : roomTypeStats.entrySet()) {
                JsonObject typeStats = new JsonObject();
                typeStats.addProperty("total", entry.getValue()[0]);
                typeStats.addProperty("available", entry.getValue()[1]);
                typeStats.addProperty("occupied", entry.getValue()[0] - entry.getValue()[1]);
                roomTypeBreakdown.add(entry.getKey(), typeStats);
            }
            report.add("roomTypeBreakdown", roomTypeBreakdown);
            
            // Available rooms list
            JsonArray availableRoomsList = new JsonArray();
            for (Room room : availableRooms) {
                JsonObject roomJson = new JsonObject();
                roomJson.addProperty("roomNumber", room.getRoomNumber());
                roomJson.addProperty("roomType", room.getRoomType());
                roomJson.addProperty("ratePerNight", room.getRatePerNight());
                roomJson.addProperty("maxOccupancy", room.getMaxOccupancy());
                availableRoomsList.add(roomJson);
            }
            report.add("availableRooms", availableRoomsList);
            
            return Response.ok(report.toString()).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Failed to generate occupancy report: " + e.getMessage());
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
