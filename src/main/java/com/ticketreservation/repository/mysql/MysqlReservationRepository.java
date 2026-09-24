package com.ticketreservation.repository.mysql;

import com.ticketreservation.model.TicketReservation;
import com.ticketreservation.repository.RepositoryException;
import com.ticketreservation.repository.ReservationRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MysqlReservationRepository implements ReservationRepository {

    private final DataSource dataSource;

    public MysqlReservationRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<TicketReservation> findAll() {
        String sql = "SELECT * FROM ticket_reservations";
        List<TicketReservation> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractReservation(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding all reservations", e);
        }
    }

    @Override
    public TicketReservation findById(long id) {
        String sql = "SELECT * FROM ticket_reservations WHERE id = ?";
        TicketReservation reservation = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    reservation = extractReservation(rs);
                }
            }
            return reservation;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding reservation by id: " + id, e);
        }
    }

    @Override
    public List<TicketReservation> findByUserId(long userId) {
        String sql = "SELECT * FROM ticket_reservations WHERE user_id = ?";
        List<TicketReservation> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractReservation(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding reservations by user_id: " + userId, e);
        }
    }

    @Override
    public List<TicketReservation> findByEventId(long eventId) {
        String sql = "SELECT * FROM ticket_reservations WHERE event_id = ?";
        List<TicketReservation> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractReservation(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding reservations by event_id: " + eventId, e);
        }
    }

    /**
     * Executes an ATOMIC SQL Transaction:
     * 1. Checks seat availability (status = 'AVAILABLE')
     * 2. Updates seat status to 'RESERVED'
     * 3. Decrements available_seats in the events table
     * 4. Inserts the ticket_reservation record
     * Rolls back on any failure or if seat is unavailable.
     */
    @Override
    public TicketReservation createReservationTransaction(TicketReservation reservation) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false); // Begin SQL Transaction

            // 1. Check seat availability
            String checkSeatSql = "SELECT status FROM seats WHERE id = ? FOR UPDATE";
            try (PreparedStatement psCheck = conn.prepareStatement(checkSeatSql)) {
                psCheck.setLong(1, reservation.getSeatId());
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (!rs.next() || !"AVAILABLE".equalsIgnoreCase(rs.getString("status"))) {
                        conn.rollback();
                        throw new RepositoryException("Seat is not available for reservation: " + reservation.getSeatId(), null);
                    }
                }
            }

            // 2. Update seat status to RESERVED
            String updateSeatSql = "UPDATE seats SET status = 'RESERVED' WHERE id = ?";
            try (PreparedStatement psUpdateSeat = conn.prepareStatement(updateSeatSql)) {
                psUpdateSeat.setLong(1, reservation.getSeatId());
                psUpdateSeat.executeUpdate();
            }

            // 3. Decrement available_seats in event
            String updateEventSql = "UPDATE events SET available_seats = available_seats - 1 WHERE id = ? AND available_seats > 0";
            try (PreparedStatement psUpdateEvent = conn.prepareStatement(updateEventSql)) {
                psUpdateEvent.setLong(1, reservation.getEventId());
                int rows = psUpdateEvent.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    throw new RepositoryException("No available seats left for event: " + reservation.getEventId(), null);
                }
            }

            // 4. Insert ticket_reservation record
            String insertReservationSql = "INSERT INTO ticket_reservations (reservation_date, customer_name, total_amount, user_id, event_id, seat_id) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psInsert = conn.prepareStatement(insertReservationSql, Statement.RETURN_GENERATED_KEYS)) {
                psInsert.setString(1, reservation.getReservationDate());
                psInsert.setString(2, reservation.getCustomerName());
                psInsert.setDouble(3, reservation.getTotalAmount());
                if (reservation.getUserId() != null) {
                    psInsert.setLong(4, reservation.getUserId());
                } else {
                    psInsert.setNull(4, Types.BIGINT);
                }
                psInsert.setLong(5, reservation.getEventId());
                psInsert.setLong(6, reservation.getSeatId());
                psInsert.executeUpdate();

                try (ResultSet generatedKeys = psInsert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reservation.setId(generatedKeys.getLong(1));
                    }
                }
            }

            conn.commit(); // Commit Transaction
            return reservation;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Suppress rollback exception
                }
            }
            throw new RepositoryException("Failed to execute reservation transaction", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Suppress close exception
                }
            }
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM ticket_reservations WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting reservation id: " + id, e);
        }
    }

    private TicketReservation extractReservation(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String date = rs.getString("reservation_date");
        String name = rs.getString("customer_name");
        double amount = rs.getDouble("total_amount");
        long userIdVal = rs.getLong("user_id");
        Long userId = rs.wasNull() ? null : userIdVal;
        long eventId = rs.getLong("event_id");
        long seatId = rs.getLong("seat_id");

        return new TicketReservation(id, date, name, amount, userId, eventId, seatId);
    }
}
