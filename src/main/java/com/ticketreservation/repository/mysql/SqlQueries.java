package com.ticketreservation.repository.mysql;

public final class SqlQueries {

    private SqlQueries() {
        // Constants container
    }

    // Events
    public static final String SELECT_ALL_EVENTS = "SELECT * FROM events";
    public static final String SELECT_EVENT_BY_ID = "SELECT * FROM events WHERE id = ?";
    public static final String INSERT_EVENT = "INSERT INTO events (title, date, location, total_seats, price) VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE_EVENT = "UPDATE events SET title = ?, date = ?, location = ?, total_seats = ?, price = ? WHERE id = ?";
    public static final String DELETE_EVENT = "DELETE FROM events WHERE id = ?";

    // Seats
    public static final String SELECT_ALL_SEATS = "SELECT * FROM seats";
    public static final String SELECT_SEATS_BY_EVENT = "SELECT * FROM seats WHERE event_id = ?";
    public static final String SELECT_SEAT_BY_ID = "SELECT * FROM seats WHERE id = ?";
    public static final String INSERT_SEAT = "INSERT INTO seats (event_id, seat_number, status) VALUES (?, ?, ?)";
    public static final String UPDATE_SEAT_STATUS = "UPDATE seats SET status = ? WHERE id = ?";

    // Reservations
    public static final String SELECT_ALL_RESERVATIONS = "SELECT * FROM ticket_reservations";
    public static final String SELECT_RESERVATION_BY_ID = "SELECT * FROM ticket_reservations WHERE id = ?";
    public static final String INSERT_RESERVATION = "INSERT INTO ticket_reservations (event_id, seat_id, customer_name, reservation_date, amount) VALUES (?, ?, ?, ?, ?)";

    // Users
    public static final String SELECT_ALL_USERS = "SELECT * FROM users";
    public static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    public static final String SELECT_USER_BY_USERNAME = "SELECT * FROM users WHERE username = ?";
    public static final String INSERT_USER = "INSERT INTO users (username, password, role, active) VALUES (?, ?, ?, ?)";
}
