# Ocean View Resort - Hotel Reservation System

## Project Overview

**Project Title:** Ocean View Resort - Hotel Reservation Management System  
**Course:** CIS6003 - Advanced Programming  
**Technology Stack:** Java EE Web Application (JAX-RS RESTful Web Services)  
**Database:** MySQL  
**Application Server:** Apache Tomcat 8.5+  
**Build Tool:** Apache Maven

---

## 1. Introduction

### 1.1 Project Description
Ocean View Resort is a comprehensive hotel reservation management system developed for a beachfront resort located in Galle, Sri Lanka. The system provides a web-based interface for managing hotel operations including guest reservations, room management, billing, and reporting.

### 1.2 Problem Statement
Ocean View Resort requires an efficient computerized system to replace manual booking processes. The system must handle:
- User authentication and authorization
- Guest information management
- Room inventory and availability tracking
- Reservation creation and management
- Bill calculation and payment processing
- Comprehensive reporting for management decisions

### 1.3 Objectives
1. Develop a secure, user-friendly web application for hotel reservation management
2. Implement RESTful web services using Java EE (JAX-RS)
3. Apply appropriate design patterns for maintainability and scalability
4. Provide comprehensive reporting capabilities
5. Ensure data persistence using MySQL database

---

## 2. System Architecture

### 2.1 Architecture Pattern: MVC (Model-View-Controller)

```
┌─────────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                          │
│                    (HTML/CSS/JavaScript SPA)                      │
│                         index.html                                │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      WEB SERVICE LAYER                           │
│                    (JAX-RS REST Resources)                       │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐            │
│  │AuthResource│ │GuestResource│ │RoomResource│ │ReservationResource│        │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘            │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐                         │
│  │BillResource│ │ReportResource│ │HelpResource│                          │
│  └──────────┘ └──────────┘ └──────────┘                         │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      SERVICE LAYER                               │
│                    (Business Logic - Facade Pattern)             │
│         ┌─────────────────┐  ┌─────────────────────┐            │
│         │   AuthService   │  │ ReservationService  │            │
│         │   (Singleton)   │  │    (Singleton)      │            │
│         └─────────────────┘  └─────────────────────┘            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      DATA ACCESS LAYER                           │
│                    (DAO Pattern + Factory Pattern)               │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐            │
│  │ UserDAO  │ │ GuestDAO │ │ RoomDAO  │ │ReservationDAO│        │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘            │
│  ┌──────────┐ ┌────────────────────┐                            │
│  │ BillDAO  │ │    DAOFactory      │                            │
│  └──────────┘ └────────────────────┘                            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      DATABASE LAYER                              │
│                    (MySQL - Singleton Connection)                │
│              ┌─────────────────────────────┐                    │
│              │   DatabaseConnection        │                    │
│              │      (Singleton)            │                    │
│              └─────────────────────────────┘                    │
│                          │                                       │
│                          ▼                                       │
│              ┌─────────────────────────────┐                    │
│              │     MySQL Database          │                    │
│              │      oceanviewdb            │                    │
│              └─────────────────────────────┘                    │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 Package Structure

```
com.oceanview/
├── config/
│   └── ApplicationConfig.java      # JAX-RS Application Configuration
├── model/
│   ├── User.java                   # User entity
│   ├── Guest.java                  # Guest entity
│   ├── Room.java                   # Room entity
│   ├── Reservation.java           # Reservation entity
│   └── Bill.java                   # Bill entity
├── dao/
│   ├── GenericDAO.java            # Generic DAO interface
│   ├── UserDAO.java               # User DAO interface
│   ├── UserDAOImpl.java           # User DAO implementation
│   ├── GuestDAO.java              # Guest DAO interface
│   ├── GuestDAOImpl.java          # Guest DAO implementation
│   ├── RoomDAO.java               # Room DAO interface
│   ├── RoomDAOImpl.java           # Room DAO implementation
│   ├── ReservationDAO.java        # Reservation DAO interface
│   ├── ReservationDAOImpl.java    # Reservation DAO implementation
│   ├── BillDAO.java               # Bill DAO interface
│   ├── BillDAOImpl.java           # Bill DAO implementation
│   └── DAOFactory.java            # Factory for DAO instances
├── service/
│   ├── AuthService.java           # Authentication service (Singleton)
│   └── ReservationService.java    # Reservation service (Singleton/Facade)
├── util/
│   ├── DatabaseConnection.java    # Database connection manager (Singleton)
│   └── ValidationUtil.java        # Input validation utilities
└── webservice/
    ├── AuthResource.java          # Authentication REST endpoints
    ├── GuestResource.java         # Guest management REST endpoints
    ├── RoomResource.java          # Room management REST endpoints
    ├── ReservationResource.java   # Reservation REST endpoints
    ├── BillResource.java          # Billing REST endpoints
    ├── ReportResource.java        # Reporting REST endpoints
    └── HelpResource.java          # Help/Documentation REST endpoints
```

---

## 3. Design Patterns Implemented

### 3.1 Singleton Pattern

**Purpose:** Ensure a class has only one instance and provide global access point.

**Implementation:**

#### DatabaseConnection.java
```java
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    
    private DatabaseConnection() {
        // Private constructor - initializes database connection
    }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public Connection getConnection() {
        return connection;
    }
}
```

#### AuthService.java
```java
public class AuthService {
    private static AuthService instance;
    
    private AuthService() {
        // Private constructor
    }
    
    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }
}
```

#### ReservationService.java
```java
public class ReservationService {
    private static ReservationService instance;
    
    private ReservationService() {
        // Private constructor
    }
    
    public static synchronized ReservationService getInstance() {
        if (instance == null) {
            instance = new ReservationService();
        }
        return instance;
    }
}
```

**Benefits:**
- Controlled access to sole instance
- Reduced namespace pollution
- Permits refinement of operations
- Thread-safe implementation using synchronized

---

### 3.2 DAO (Data Access Object) Pattern

**Purpose:** Abstract and encapsulate all access to the data source.

**Implementation:**

#### GenericDAO.java (Interface)
```java
public interface GenericDAO<T> {
    T findById(int id);
    List<T> findAll();
    boolean save(T entity);
    boolean update(T entity);
    boolean delete(int id);
}
```

#### UserDAO.java
```java
public interface UserDAO extends GenericDAO<User> {
    User findByUsername(String username);
    boolean authenticate(String username, String password);
}
```

#### UserDAOImpl.java
```java
public class UserDAOImpl implements UserDAO {
    private Connection connection;
    
    public UserDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    @Override
    public User findById(int id) {
        // SQL query implementation
    }
    
    @Override
    public User findByUsername(String username) {
        // SQL query implementation
    }
    
    // Other CRUD implementations...
}
```

**Benefits:**
- Separation of concerns
- Easier to maintain and test
- Database independence
- Reusable data access code

---

### 3.3 Factory Pattern

**Purpose:** Create objects without exposing creation logic.

**Implementation:**

#### DAOFactory.java
```java
public class DAOFactory {
    
    public static UserDAO getUserDAO() {
        return new UserDAOImpl();
    }
    
    public static GuestDAO getGuestDAO() {
        return new GuestDAOImpl();
    }
    
    public static RoomDAO getRoomDAO() {
        return new RoomDAOImpl();
    }
    
    public static ReservationDAO getReservationDAO() {
        return new ReservationDAOImpl();
    }
    
    public static BillDAO getBillDAO() {
        return new BillDAOImpl();
    }
}
```

**Usage:**
```java
UserDAO userDAO = DAOFactory.getUserDAO();
User user = userDAO.findByUsername("admin");
```

**Benefits:**
- Centralized object creation
- Easy to switch implementations
- Loose coupling between layers
- Simplified testing with mock objects

---

### 3.4 Facade Pattern

**Purpose:** Provide a simplified interface to a complex subsystem.

**Implementation:**

#### ReservationService.java (Facade)
```java
public class ReservationService {
    private final GuestDAO guestDAO;
    private final RoomDAO roomDAO;
    private final ReservationDAO reservationDAO;
    private final BillDAO billDAO;
    
    private ReservationService() {
        this.guestDAO = DAOFactory.getGuestDAO();
        this.roomDAO = DAOFactory.getRoomDAO();
        this.reservationDAO = DAOFactory.getReservationDAO();
        this.billDAO = DAOFactory.getBillDAO();
    }
    
    // Simplified method that coordinates multiple DAOs
    public Reservation createReservation(Guest guest, Room room, 
                                         Date checkIn, Date checkOut) {
        // 1. Save guest if new
        guestDAO.save(guest);
        
        // 2. Check room availability
        if (!roomDAO.isAvailable(room.getRoomId(), checkIn, checkOut)) {
            throw new IllegalStateException("Room not available");
        }
        
        // 3. Create reservation
        Reservation reservation = new Reservation();
        reservation.setGuest(guest);
        reservation.setRoom(room);
        reservation.setCheckInDate(checkIn);
        reservation.setCheckOutDate(checkOut);
        reservationDAO.save(reservation);
        
        // 4. Update room availability
        roomDAO.updateAvailability(room.getRoomId(), false);
        
        return reservation;
    }
    
    // Other coordinated operations...
}
```

**Benefits:**
- Simplified interface for complex operations
- Reduced dependencies for clients
- Layered architecture support
- Easy to modify subsystem without affecting clients

---

### 3.5 MVC (Model-View-Controller) Pattern

**Purpose:** Separate application concerns into three interconnected components.

**Implementation:**

| Component | Implementation | Description |
|-----------|---------------|-------------|
| **Model** | `com.oceanview.model.*` | Entity classes (User, Guest, Room, Reservation, Bill) |
| **View** | `index.html` (SPA) | Single Page Application with HTML/CSS/JavaScript |
| **Controller** | `com.oceanview.webservice.*` | JAX-RS REST Resources handling HTTP requests |

**Benefits:**
- Separation of concerns
- Independent development
- Easier testing
- Reusable components

---

## 4. Database Design

### 4.1 Entity Relationship Diagram

```
┌─────────────────┐       ┌─────────────────┐
│     USERS       │       │     GUESTS      │
├─────────────────┤       ├─────────────────┤
│ user_id (PK)    │       │ guest_id (PK)   │
│ username        │       │ first_name      │
│ password        │       │ last_name       │
│ full_name       │       │ address         │
│ role            │       │ contact_number  │
│ active          │       │ email           │
│ created_at      │       │ nic_number      │
└─────────────────┘       │ created_at      │
                          └────────┬────────┘
                                   │
                                   │ 1
                                   │
                                   ▼ N
┌─────────────────┐       ┌─────────────────┐
│     ROOMS       │       │  RESERVATIONS   │
├─────────────────┤       ├─────────────────┤
│ room_id (PK)    │◄──────│ reservation_id  │
│ room_number     │   N   │ reservation_no  │
│ room_type       │       │ guest_id (FK)   │
│ rate_per_night  │       │ room_id (FK)    │
│ max_occupancy   │       │ check_in_date   │
│ description     │       │ check_out_date  │
│ available       │       │ number_of_guests│
│ amenities       │       │ status          │
│ created_at      │       │ total_amount    │
└─────────────────┘       │ special_requests│
                          │ created_date    │
                          └────────┬────────┘
                                   │
                                   │ 1
                                   │
                                   ▼ 1
                          ┌─────────────────┐
                          │     BILLS       │
                          ├─────────────────┤
                          │ bill_id (PK)    │
                          │ bill_number     │
                          │ reservation_id  │
                          │ room_charges    │
                          │ service_charges │
                          │ tax_amount      │
                          │ discount_amount │
                          │ total_amount    │
                          │ bill_date       │
                          │ payment_status  │
                          │ payment_method  │
                          └─────────────────┘
```

### 4.2 Database Tables

#### Users Table
```sql
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Guests Table
```sql
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
```

#### Rooms Table
```sql
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
```

#### Reservations Table
```sql
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
```

#### Bills Table
```sql
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
```

---

## 5. RESTful Web Services (API Endpoints)

### 5.1 Authentication API

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | User login |
| POST | `/api/auth/logout` | User logout |
| GET | `/api/auth/current` | Get current user |
| GET | `/api/auth/users` | Get all users |
| POST | `/api/auth/users` | Create new user |

### 5.2 Guest Management API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/guests` | Get all guests |
| GET | `/api/guests/{id}` | Get guest by ID |
| POST | `/api/guests` | Create new guest |
| PUT | `/api/guests/{id}` | Update guest |
| DELETE | `/api/guests/{id}` | Delete guest |
| GET | `/api/guests/search?query=` | Search guests |

### 5.3 Room Management API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/rooms` | Get all rooms |
| GET | `/api/rooms/{id}` | Get room by ID |
| GET | `/api/rooms/available` | Get available rooms |
| POST | `/api/rooms` | Create new room |
| PUT | `/api/rooms/{id}` | Update room |
| DELETE | `/api/rooms/{id}` | Delete room |

### 5.4 Reservation API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/reservations` | Get all reservations |
| GET | `/api/reservations/{id}` | Get reservation by ID |
| GET | `/api/reservations/number/{num}` | Get by reservation number |
| POST | `/api/reservations` | Create new reservation |
| PUT | `/api/reservations/{id}` | Update reservation |
| PUT | `/api/reservations/{id}/status` | Update status |
| DELETE | `/api/reservations/{id}` | Cancel reservation |

### 5.5 Billing API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/bills` | Get all bills |
| GET | `/api/bills/{id}` | Get bill by ID |
| POST | `/api/bills` | Generate bill |
| PUT | `/api/bills/{id}/payment` | Process payment |
| GET | `/api/bills/pending` | Get pending bills |
| GET | `/api/bills/reservation/{id}` | Get bill by reservation |

### 5.6 Reports API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/reports/dashboard` | Dashboard summary |
| GET | `/api/reports/daily` | Daily report |
| GET | `/api/reports/reservations` | Reservations report |
| GET | `/api/reports/revenue` | Revenue report |
| GET | `/api/reports/occupancy` | Occupancy report |

### 5.7 Help API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/help` | Get help documentation |

---

## 6. System Features

### 6.1 User Authentication (Login System)
- Secure login with username and password
- Role-based access control (ADMIN, MANAGER, RECEPTIONIST)
- Session management
- Logout functionality

### 6.2 Add Reservation
- Guest information capture
- Room selection based on availability
- Date range selection (check-in/check-out)
- Automatic total amount calculation
- Special requests handling
- Reservation confirmation number generation

### 6.3 Display Reservation Details
- Search by reservation number
- View complete reservation information
- Guest details display
- Room information
- Status tracking (PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED)

### 6.4 Calculate/Print Bill
- Automatic bill generation from reservation
- Room charges calculation based on nights stayed
- Service charges (10%)
- Tax calculation (10%)
- Discount application
- Multiple payment methods (CASH, CREDIT_CARD, DEBIT_CARD)
- Bill printing functionality

### 6.5 Reports
- **Dashboard**: Overview of key metrics
- **Daily Report**: Today's check-ins and check-outs
- **Reservations Report**: All reservations with status breakdown
- **Revenue Report**: Financial summary with collection rates
- **Occupancy Report**: Room utilization by type

### 6.6 Help Section
- System documentation
- Feature guides
- Contact information
- FAQ section

---

## 7. User Interface

### 7.1 Technology Stack
- **HTML5**: Structure and semantics
- **CSS3**: Styling with modern features (Flexbox, Grid, Gradients)
- **JavaScript (ES6+)**: Client-side logic and AJAX calls
- **Single Page Application (SPA)**: Dynamic content loading without page refresh

### 7.2 UI Components
- Responsive navigation sidebar
- Dashboard with statistics cards
- Data tables with search functionality
- Modal dialogs for details and forms
- Status badges with color coding
- Form validation with user feedback

### 7.3 Color Scheme
- Primary: `#1a5276` (Dark Blue)
- Secondary: `#2980b9` (Medium Blue)
- Accent: `#5dade2` (Light Blue)
- Success: `#27ae60` (Green)
- Warning: `#f39c12` (Orange)
- Danger: `#e74c3c` (Red)

---

## 8. Validation Mechanisms

### 8.1 Server-Side Validation (ValidationUtil.java)

```java
public class ValidationUtil {
    // Email validation
    public static boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(regex);
    }
    
    // Phone validation (Sri Lankan format)
    public static boolean isValidContactNumber(String phone) {
        String regex = "^(\\+94|0)?[0-9]{9,10}$";
        return phone != null && phone.matches(regex);
    }
    
    // NIC validation (Sri Lankan format)
    public static boolean isValidNIC(String nic) {
        String regex = "^([0-9]{9}[vVxX]|[0-9]{12})$";
        return nic != null && nic.matches(regex);
    }
    
    // Name validation
    public static boolean isValidName(String name) {
        return name != null && name.length() >= 2 && 
               name.matches("^[a-zA-Z\\s]+$");
    }
}
```

### 8.2 Client-Side Validation
- Required field validation
- Email format validation
- Phone number format validation
- Date range validation (check-out after check-in)
- Numeric input validation

---

## 9. Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 8+ | Programming Language |
| Java EE | 7.0 | Enterprise Platform |
| JAX-RS | 2.0 | RESTful Web Services |
| Jersey | 2.29.1 | JAX-RS Implementation |
| MySQL | 8.0 | Database |
| MySQL Connector/J | 8.0.33 | JDBC Driver |
| Gson | 2.8.9 | JSON Processing |
| Apache Tomcat | 8.5+ | Application Server |
| Apache Maven | 3.6+ | Build Tool |
| HTML5/CSS3/JS | - | Frontend |

---

## 10. Setup and Installation

### 10.1 Prerequisites
1. JDK 8 or higher
2. Apache Maven 3.6+
3. MySQL Server 8.0+
4. Apache Tomcat 8.5+

### 10.2 Database Setup

```bash
# Login to MySQL
mysql -u root -p

# Run the seed script
source /path/to/oceanview_seed.sql
```

### 10.3 Configuration

Edit `DatabaseConnection.java` with your MySQL credentials:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/oceanviewdb";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_password";
```

### 10.4 Build

```bash
cd OceanViewResort
mvn clean package
```

### 10.5 Deploy

Copy `target/OceanViewResort-1.0-SNAPSHOT.war` to Tomcat's `webapps` folder.

### 10.6 Access

Open browser: `http://localhost:8080/OceanViewResort-1.0-SNAPSHOT/`

**Default Login:**
- Username: `admin`
- Password: `admin123`

---

## 11. Project Structure

```
OceanViewResort/
├── pom.xml                          # Maven configuration
├── README.md                        # Project documentation
├── database/
│   └── oceanview_seed.sql          # Database schema and seed data
├── src/
│   └── main/
│       ├── java/
│       │   └── com/oceanview/
│       │       ├── config/
│       │       ├── dao/
│       │       ├── model/
│       │       ├── service/
│       │       ├── util/
│       │       └── webservice/
│       └── webapp/
│           ├── index.html          # Main SPA
│           ├── error.html          # Error page
│           ├── META-INF/
│           │   └── context.xml
│           └── WEB-INF/
│               └── web.xml
└── target/
    └── OceanViewResort-1.0-SNAPSHOT.war
```

---

## 12. Testing

### 12.1 Manual Testing Checklist

| Feature | Test Case | Expected Result |
|---------|-----------|-----------------|
| Login | Valid credentials | Redirect to dashboard |
| Login | Invalid credentials | Error message |
| Add Reservation | Complete form | Reservation created |
| Add Reservation | Missing fields | Validation error |
| View Reservation | Valid reservation # | Details displayed |
| Generate Bill | Select reservation | Bill calculated |
| Process Payment | Select method | Status updated |
| Reports | Click report type | Report displayed |

### 12.2 API Testing with cURL

```bash
# Login
curl -X POST http://localhost:8080/OceanViewResort-1.0-SNAPSHOT/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Get all rooms
curl http://localhost:8080/OceanViewResort-1.0-SNAPSHOT/api/rooms

# Get available rooms
curl http://localhost:8080/OceanViewResort-1.0-SNAPSHOT/api/rooms/available
```

---

## 13. Future Enhancements

1. **Email Notifications**: Send booking confirmations via email
2. **Online Payment Integration**: PayPal, Stripe integration
3. **Mobile Application**: Native Android/iOS apps
4. **Advanced Reporting**: PDF export, charts and graphs
5. **Multi-language Support**: Internationalization
6. **Room Images**: Photo gallery for each room
7. **Customer Reviews**: Rating and review system
8. **Loyalty Program**: Points and rewards system

---

## 14. Conclusion

The Ocean View Resort Hotel Reservation System successfully demonstrates the application of:

1. **Java EE Web Services**: RESTful API design using JAX-RS
2. **Design Patterns**: Singleton, DAO, Factory, Facade, and MVC patterns
3. **Database Management**: MySQL with proper normalization
4. **Modern Web Technologies**: Single Page Application architecture
5. **Input Validation**: Both client-side and server-side validation
6. **User Experience**: Intuitive and responsive user interface

The system provides a complete solution for hotel reservation management with all required features including user authentication, reservation management, billing, and comprehensive reporting.

---

## 15. References

1. Oracle Java EE 7 Tutorial - https://docs.oracle.com/javaee/7/tutorial/
2. JAX-RS Specification - https://jax-rs.github.io/
3. Jersey User Guide - https://eclipse-ee4j.github.io/jersey/
4. MySQL Documentation - https://dev.mysql.com/doc/
5. Design Patterns: Elements of Reusable Object-Oriented Software (Gang of Four)
6. Maven Documentation - https://maven.apache.org/guides/

---

## 16. Appendices

### Appendix A: Sample Data

**Users:**
| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| receptionist | recep123 | RECEPTIONIST |
| manager | manager123 | MANAGER |

**Room Types:**
| Type | Rate/Night (LKR) | Max Occupancy |
|------|------------------|---------------|
| SINGLE | 5,000 - 5,500 | 1 |
| DOUBLE | 8,000 - 8,500 | 2 |
| DELUXE | 12,000 - 12,500 | 3 |
| SUITE | 20,000 - 22,000 | 4 |
| FAMILY | 15,000 - 16,000 | 5-6 |

### Appendix B: Bill Calculation Formula

```
Room Charges = Rate per Night × Number of Nights
Service Charges = Room Charges × 10%
Tax Amount = Room Charges × 10%
Total Amount = Room Charges + Service Charges + Tax Amount - Discount
```

---

**Developed by:** Ocean View Resort Development Team  
**Version:** 1.0  
**Date:** January 2026
