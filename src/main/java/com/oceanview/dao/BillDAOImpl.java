package com.oceanview.dao;

import com.oceanview.model.Bill;
import com.oceanview.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Bill DAO Implementation.
 * Implements BillDAO interface with Derby database operations.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
public class BillDAOImpl implements BillDAO {
    
    private final Connection connection;
    
    public BillDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    @Override
    public Bill create(Bill bill) {
        String sql = "INSERT INTO bills (bill_number, reservation_id, room_charges, service_charges, " +
                     "tax_amount, discount_amount, total_amount, payment_status, payment_method) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Generate bill number if not provided
            if (bill.getBillNumber() == null || bill.getBillNumber().isEmpty()) {
                bill.setBillNumber(generateBillNumber());
            }
            
            stmt.setString(1, bill.getBillNumber());
            stmt.setInt(2, bill.getReservationId());
            stmt.setDouble(3, bill.getRoomCharges());
            stmt.setDouble(4, bill.getServiceCharges());
            stmt.setDouble(5, bill.getTaxAmount());
            stmt.setDouble(6, bill.getDiscountAmount());
            stmt.setDouble(7, bill.getTotalAmount());
            stmt.setString(8, bill.getPaymentStatus() != null ? bill.getPaymentStatus() : "PENDING");
            stmt.setString(9, bill.getPaymentMethod());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        bill.setBillId(generatedKeys.getInt(1));
                    }
                }
            }
            return bill;
        } catch (SQLException e) {
            System.err.println("Error creating bill: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public Bill findById(int id) {
        String sql = "SELECT * FROM bills WHERE bill_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBill(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding bill: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public Bill findByBillNumber(String billNumber) {
        String sql = "SELECT * FROM bills WHERE bill_number = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, billNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBill(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding bill by number: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public Bill findByReservationId(int reservationId) {
        String sql = "SELECT * FROM bills WHERE reservation_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBill(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding bill by reservation: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<Bill> findByPaymentStatus(String paymentStatus) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE payment_status = ? ORDER BY bill_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, paymentStatus);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bills.add(mapResultSetToBill(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding bills by status: " + e.getMessage());
        }
        return bills;
    }
    
    @Override
    public boolean updatePaymentStatus(int billId, String paymentStatus, String paymentMethod) {
        String sql = "UPDATE bills SET payment_status = ?, payment_method = ? WHERE bill_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, paymentStatus);
            stmt.setString(2, paymentMethod);
            stmt.setInt(3, billId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment status: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String generateBillNumber() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        
        String sql = "SELECT MAX(CAST(SUBSTR(bill_number, 11) AS INT)) FROM bills " +
                     "WHERE bill_number LIKE 'BILL-" + year + "-%'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int nextNumber = 1;
            if (rs.next()) {
                int maxNumber = rs.getInt(1);
                if (!rs.wasNull()) {
                    nextNumber = maxNumber + 1;
                }
            }
            
            return String.format("BILL-%d-%04d", year, nextNumber);
        } catch (SQLException e) {
            System.err.println("Error generating bill number: " + e.getMessage());
            return "BILL-" + year + "-" + System.currentTimeMillis() % 10000;
        }
    }
    
    @Override
    public double getTotalRevenue(String paymentStatus) {
        String sql;
        if (paymentStatus != null && !paymentStatus.isEmpty()) {
            sql = "SELECT SUM(total_amount) FROM bills WHERE payment_status = ?";
        } else {
            sql = "SELECT SUM(total_amount) FROM bills";
        }
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (paymentStatus != null && !paymentStatus.isEmpty()) {
                stmt.setString(1, paymentStatus);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting total revenue: " + e.getMessage());
        }
        return 0.0;
    }
    
    @Override
    public List<Bill> findAll() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY bill_date DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all bills: " + e.getMessage());
        }
        return bills;
    }
    
    @Override
    public boolean update(Bill bill) {
        String sql = "UPDATE bills SET room_charges = ?, service_charges = ?, tax_amount = ?, " +
                     "discount_amount = ?, total_amount = ?, payment_status = ?, payment_method = ? " +
                     "WHERE bill_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, bill.getRoomCharges());
            stmt.setDouble(2, bill.getServiceCharges());
            stmt.setDouble(3, bill.getTaxAmount());
            stmt.setDouble(4, bill.getDiscountAmount());
            stmt.setDouble(5, bill.getTotalAmount());
            stmt.setString(6, bill.getPaymentStatus());
            stmt.setString(7, bill.getPaymentMethod());
            stmt.setInt(8, bill.getBillId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating bill: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM bills WHERE bill_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting bill: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Map ResultSet to Bill object
     */
    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getInt("bill_id"));
        bill.setBillNumber(rs.getString("bill_number"));
        bill.setReservationId(rs.getInt("reservation_id"));
        bill.setRoomCharges(rs.getDouble("room_charges"));
        bill.setServiceCharges(rs.getDouble("service_charges"));
        bill.setTaxAmount(rs.getDouble("tax_amount"));
        bill.setDiscountAmount(rs.getDouble("discount_amount"));
        bill.setTotalAmount(rs.getDouble("total_amount"));
        bill.setBillDate(rs.getTimestamp("bill_date"));
        bill.setPaymentStatus(rs.getString("payment_status"));
        bill.setPaymentMethod(rs.getString("payment_method"));
        return bill;
    }
}
