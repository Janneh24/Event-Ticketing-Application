package com.ticketreservation.repository.mysql;

import com.ticketreservation.model.Seat;
import com.ticketreservation.repository.RepositoryException;
import com.ticketreservation.repository.SeatRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MysqlSeatRepository implements SeatRepository {

    private final DataSource dataSource;

    public MysqlSeatRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Seat> findAll() {
        String sql = "SELECT * FROM seats";
        List<Seat> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractSeat(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding all seats", e);
        }
    }

    @Override
    public List<Seat> findByEventId(long eventId) {
        String sql = "SELECT * FROM seats WHERE event_id = ?";
        List<Seat> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractSeat(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding seats by event_id: " + eventId, e);
        }
    }

    @Override
    public Seat findById(long id) {
        String sql = "SELECT * FROM seats WHERE id = ?";
        Seat seat = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    seat = extractSeat(rs);
                }
            }
            return seat;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding seat by id: " + id, e);
        }
    }

    @Override
    public Seat save(Seat seat) {
        String sql = "INSERT INTO seats (event_id, seat_number, section, price, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, seat.getEventId());
            ps.setString(2, seat.getSeatNumber());
            ps.setString(3, seat.getSection());
            ps.setDouble(4, seat.getPrice());
            ps.setString(5, seat.getStatus());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    seat.setId(generatedKeys.getLong(1));
                }
            }
            return seat;
        } catch (SQLException e) {
            throw new RepositoryException("Error saving seat: " + seat.getSeatNumber(), e);
        }
    }

    @Override
    public Seat update(Seat seat) {
        String sql = "UPDATE seats SET event_id = ?, seat_number = ?, section = ?, price = ?, status = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, seat.getEventId());
            ps.setString(2, seat.getSeatNumber());
            ps.setString(3, seat.getSection());
            ps.setDouble(4, seat.getPrice());
            ps.setString(5, seat.getStatus());
            ps.setLong(6, seat.getId());
            ps.executeUpdate();
            return seat;
        } catch (SQLException e) {
            throw new RepositoryException("Error updating seat id: " + seat.getId(), e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM seats WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting seat id: " + id, e);
        }
    }

    private Seat extractSeat(ResultSet rs) throws SQLException {
        return new Seat(
                rs.getLong("id"),
                rs.getLong("event_id"),
                rs.getString("seat_number"),
                rs.getString("section"),
                rs.getDouble("price"),
                rs.getString("status")
        );
    }
}
