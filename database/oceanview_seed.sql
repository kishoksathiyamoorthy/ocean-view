-- =====================================================
-- Ocean View Resort Database Schema and Seed Data
-- MySQL Database Setup Script
-- =====================================================

-- Create the database
CREATE DATABASE IF NOT EXISTS oceanviewdb;
USE oceanviewdb;

-- =====================================================
-- Drop existing tables (in reverse order of dependencies)
-- =====================================================
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS guests;
DROP TABLE IF EXISTS users;

-- =====================================================
-- Create Users Table
-- =====================================================
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- Create Guests Table
-- =====================================================
CREATE TABLE guests (
    guest_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    address VARCHAR(255),
    contact_number VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    nic_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- Create Rooms Table
-- =====================================================
CREATE TABLE rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    room_type VARCHAR(20) NOT NULL,
    rate_per_night DOUBLE NOT NULL,
    max_occupancy INT NOT NULL,
    description VARCHAR(500),
    available BOOLEAN DEFAULT TRUE,
    amenities VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- Create Reservations Table
-- =====================================================
CREATE TABLE reservations (
    reservation_id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_number VARCHAR(20) UNIQUE NOT NULL,
    guest_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_guests INT NOT NULL,
    status VARCHAR(20) DEFAULT 'CONFIRMED',
    total_amount DOUBLE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    special_requests VARCHAR(500),
    FOREIGN KEY (guest_id) REFERENCES guests(guest_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

-- =====================================================
-- Create Bills Table
-- =====================================================
CREATE TABLE bills (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    bill_number VARCHAR(20) UNIQUE NOT NULL,
    reservation_id INT NOT NULL,
    room_charges DOUBLE NOT NULL,
    service_charges DOUBLE,
    tax_amount DOUBLE,
    discount_amount DOUBLE DEFAULT 0,
    total_amount DOUBLE NOT NULL,
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    payment_method VARCHAR(20),
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id)
);

-- =====================================================
-- Seed Data: Users
-- =====================================================
INSERT INTO users (username, password, full_name, role, active) VALUES
('admin', 'admin123', 'System Administrator', 'ADMIN', TRUE),
('receptionist', 'recep123', 'Front Desk Receptionist', 'RECEPTIONIST', TRUE),
('manager', 'manager123', 'Hotel Manager', 'MANAGER', TRUE);

-- =====================================================
-- Seed Data: Rooms
-- =====================================================
INSERT INTO rooms (room_number, room_type, rate_per_night, max_occupancy, description, available, amenities) VALUES
('101', 'SINGLE', 5000.00, 1, 'Cozy single room with garden view', TRUE, 'AC, TV, WiFi, Mini Bar'),
('102', 'SINGLE', 5000.00, 1, 'Single room with pool view', TRUE, 'AC, TV, WiFi, Mini Bar'),
('103', 'SINGLE', 5500.00, 1, 'Premium single room with sea view', TRUE, 'AC, Smart TV, WiFi, Mini Bar'),
('201', 'DOUBLE', 8000.00, 2, 'Spacious double room with balcony', TRUE, 'AC, TV, WiFi, Mini Bar, Balcony'),
('202', 'DOUBLE', 8500.00, 2, 'Double room with ocean view', TRUE, 'AC, TV, WiFi, Mini Bar, Ocean View'),
('203', 'DOUBLE', 8000.00, 2, 'Double room with garden view', TRUE, 'AC, TV, WiFi, Mini Bar, Garden View'),
('301', 'DELUXE', 12000.00, 3, 'Luxurious deluxe room with premium amenities', TRUE, 'AC, Smart TV, WiFi, Mini Bar, Jacuzzi, Ocean View'),
('302', 'DELUXE', 12500.00, 3, 'Deluxe room with private terrace', TRUE, 'AC, Smart TV, WiFi, Mini Bar, Jacuzzi, Terrace'),
('401', 'SUITE', 20000.00, 4, 'Executive suite with living area', TRUE, 'AC, Smart TV, WiFi, Mini Bar, Jacuzzi, Living Room, Ocean View'),
('402', 'SUITE', 22000.00, 4, 'Presidential suite with panoramic view', TRUE, 'AC, Smart TV, WiFi, Mini Bar, Jacuzzi, Living Room, Dining Area, Ocean View'),
('501', 'FAMILY', 15000.00, 5, 'Family room with multiple beds', TRUE, 'AC, TV, WiFi, Mini Bar, Kids Area, Garden View'),
('502', 'FAMILY', 16000.00, 6, 'Large family room with bunk beds', TRUE, 'AC, TV, WiFi, Mini Bar, Kids Area, Play Station, Pool View');

-- =====================================================
-- Seed Data: Sample Guests
-- =====================================================
INSERT INTO guests (first_name, last_name, address, contact_number, email, nic_number) VALUES
('John', 'Silva', '123 Main Street, Colombo', '0771234567', 'john.silva@email.com', '901234567V'),
('Sarah', 'Fernando', '456 Galle Road, Galle', '0772345678', 'sarah.fernando@email.com', '885678901V'),
('Michael', 'Perera', '789 Beach Road, Negombo', '0773456789', 'michael.perera@email.com', '921234567V'),
('Emily', 'De Silva', '321 Lake View, Kandy', '0774567890', 'emily.desilva@email.com', '951234567V'),
('David', 'Jayawardena', '654 Temple Road, Anuradhapura', '0775678901', 'david.jay@email.com', '881234567V');

-- =====================================================
-- Seed Data: Sample Reservations
-- =====================================================
INSERT INTO reservations (reservation_number, guest_id, room_id, check_in_date, check_out_date, number_of_guests, status, total_amount, special_requests) VALUES
('RES-2026-001', 1, 4, '2026-02-01', '2026-02-05', 2, 'CONFIRMED', 32000.00, 'Late check-in requested'),
('RES-2026-002', 2, 7, '2026-02-03', '2026-02-06', 3, 'CONFIRMED', 36000.00, 'Honeymoon package'),
('RES-2026-003', 3, 1, '2026-02-10', '2026-02-12', 1, 'CONFIRMED', 10000.00, 'Quiet room preferred'),
('RES-2026-004', 4, 9, '2026-02-15', '2026-02-20', 4, 'PENDING', 100000.00, 'Business meeting room required'),
('RES-2026-005', 5, 11, '2026-02-20', '2026-02-25', 5, 'CONFIRMED', 75000.00, 'Extra beds for children');

-- =====================================================
-- Seed Data: Sample Bills
-- =====================================================
INSERT INTO bills (bill_number, reservation_id, room_charges, service_charges, tax_amount, discount_amount, total_amount, payment_status, payment_method) VALUES
('BILL-2026-001', 1, 32000.00, 3200.00, 3520.00, 0.00, 38720.00, 'PAID', 'CREDIT_CARD'),
('BILL-2026-002', 2, 36000.00, 3600.00, 3960.00, 1000.00, 42560.00, 'PAID', 'CASH'),
('BILL-2026-003', 3, 10000.00, 1000.00, 1100.00, 0.00, 12100.00, 'PENDING', NULL);

-- =====================================================
-- Verify Data
-- =====================================================
SELECT 'Users:' AS '', COUNT(*) AS count FROM users;
SELECT 'Rooms:' AS '', COUNT(*) AS count FROM rooms;
SELECT 'Guests:' AS '', COUNT(*) AS count FROM guests;
SELECT 'Reservations:' AS '', COUNT(*) AS count FROM reservations;
SELECT 'Bills:' AS '', COUNT(*) AS count FROM bills;

SELECT '========================================' AS '';
SELECT 'Ocean View Resort Database Setup Complete!' AS '';
SELECT '========================================' AS '';
