# Event Ticketing Application - Architecture & Design

## 1. System Overview
The **Event Ticketing Application** is a Java desktop application architected following the **Model-View-Controller (MVC)** design pattern, backed by a relational **MySQL** database accessed via raw JDBC with transaction management and connection pooling (`MysqlDataSource`). Dependency injection is managed via **Google Guice**, and reporting is generated via **iText PDF**.

---

## 2. Layered Architecture

```
+-------------------------------------------------------------+
|                     Presentation Layer                      |
|  - LoginSwingView                                           |
|  - EventSwingView (Customer Seat Booking)                   |
|  - AdminView (Events, Booked Tickets, User Management)      |
+------------------------------+------------------------------+
                               |
                               v
+-------------------------------------------------------------+
|                      Controller Layer                       |
|  - UserController (Authentication & User Administration)    |
|  - EventController (Event Lifecycle & Seat Provisioning)    |
|  - SeatController (Seat Reservation & Lock Management)      |
+------------------------------+------------------------------+
                               |
                               v
+-------------------------------------------------------------+
|                      Repository Layer                       |
|  - EventRepository / MysqlEventRepository                   |
|  - SeatRepository / MysqlSeatRepository                     |
|  - ReservationRepository / MysqlReservationRepository       |
|  - UserRepository / MysqlUserRepository                     |
+------------------------------+------------------------------+
                               |
                               v
+-------------------------------------------------------------+
|                        Domain Models                        |
|  - Event (id, title, date, location, totalSeats, price)     |
|  - Seat (id, eventId, seatNumber, status)                   |
|  - TicketReservation (id, eventId, seatId, customerName,...) |
|  - User (id, username, password, role, active)              |
+-------------------------------------------------------------+
```

---

## 3. Database Schema

- **`users`**: User credentials and roles (`ORGANIZER`, `CUSTOMER`). Primary keys auto-increment starting from ID 15.
- **`events`**: Event information (title, date, location, total seats, base price in Euro €).
- **`seats`**: Individual seats linked to `events` with status (`AVAILABLE`, `RESERVED`, `BOOKED`).
- **`ticket_reservations`**: Audit log of confirmed bookings with customer name, timestamp, and amount paid.

---

## 4. Testing Strategy
- **Unit Testing**: JUnit 5 + Mockito for controllers and domain models (100% branch and line coverage).
- **GUI Testing**: AssertJ-Swing with headless virtual frame buffer (`Xvfb`) in CI.
- **Integration Testing**: Testcontainers MySQL running real database queries in isolated containers.
- **Mutation Testing**: Pitest mutation testing plugin validating test assertion efficacy.
- **Static Analysis**: SonarCloud Quality Gate + JaCoCo coverage reports.
