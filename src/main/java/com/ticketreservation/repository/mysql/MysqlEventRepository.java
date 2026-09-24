package com.ticketreservation.repository.mysql;

import com.ticketreservation.model.Event;
import com.ticketreservation.repository.EventRepository;
import com.ticketreservation.repository.RepositoryException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MysqlEventRepository implements EventRepository {

    private final DataSource dataSource;

    public MysqlEventRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Event> findAll() {
        String sql = "SELECT * FROM events";
        List<Event> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractEvent(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding all events", e);
        }
    }

    @Override
    public Event findById(long id) {
        String sql = "SELECT * FROM events WHERE id = ?";
        Event event = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    event = extractEvent(rs);
                }
            }
            return event;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding event by id: " + id, e);
        }
    }

    @Override
    public Event save(Event event) {
        String sql = "INSERT INTO events (title, event_date, venue_name, total_seats, available_seats) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getEventDate());
            ps.setString(3, event.getVenueName());
            ps.setInt(4, event.getTotalSeats());
            ps.setInt(5, event.getAvailableSeats());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getLong(1));
                }
            }
            return event;
        } catch (SQLException e) {
            throw new RepositoryException("Error saving event: " + event.getTitle(), e);
        }
    }

    @Override
    public Event update(Event event) {
        String sql = "UPDATE events SET title = ?, event_date = ?, venue_name = ?, total_seats = ?, available_seats = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getEventDate());
            ps.setString(3, event.getVenueName());
            ps.setInt(4, event.getTotalSeats());
            ps.setInt(5, event.getAvailableSeats());
            ps.setLong(6, event.getId());
            ps.executeUpdate();
            return event;
        } catch (SQLException e) {
            throw new RepositoryException("Error updating event id: " + event.getId(), e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM events WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting event id: " + id, e);
        }
    }

    private Event extractEvent(ResultSet rs) throws SQLException {
        return new Event(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("event_date"),
                rs.getString("venue_name"),
                rs.getInt("total_seats"),
                rs.getInt("available_seats")
        );
    }
}
