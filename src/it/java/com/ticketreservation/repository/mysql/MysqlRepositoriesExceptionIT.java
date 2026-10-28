package com.ticketreservation.repository.mysql;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.ticketreservation.model.Event;
import com.ticketreservation.model.Seat;
import com.ticketreservation.model.TicketReservation;
import com.ticketreservation.model.User;
import com.ticketreservation.repository.RepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests verifying that real SQLExceptions thrown by real JDBC connections
 * are properly caught and rethrown as RepositoryException.
 * Note: Uses real MysqlDataSource instances without mocking third-party types.
 */
class MysqlRepositoriesExceptionIT {

    private MysqlUserRepository userRepository;
    private MysqlEventRepository eventRepository;
    private MysqlSeatRepository seatRepository;
    private MysqlReservationRepository reservationRepository;

    @BeforeEach
    void setUp() throws SQLException {
        // Real MysqlDataSource pointed to a closed port on localhost
        // Ensures real java.sql.SQLException is thrown on connection attempt without mocks
        MysqlDataSource failingDataSource = new MysqlDataSource();
        failingDataSource.setURL("jdbc:mysql://localhost:1/nonexistent");
        failingDataSource.setConnectTimeout(100);
        failingDataSource.setSocketTimeout(100);

        userRepository = new MysqlUserRepository(failingDataSource);
        eventRepository = new MysqlEventRepository(failingDataSource);
        seatRepository = new MysqlSeatRepository(failingDataSource);
        reservationRepository = new MysqlReservationRepository(failingDataSource);
    }

    @Test
    void testUserRepositoryThrowsRepositoryExceptionOnConnectionFailure() {
        User user = new User(1L, "alice", "pass", "CUSTOMER", true);

        assertThatThrownBy(() -> userRepository.findAll())
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error finding all users");

        assertThatThrownBy(() -> userRepository.findById(1L))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error finding user by id: 1");

        assertThatThrownBy(() -> userRepository.findByUsername("alice"))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error finding user by username: alice");

        assertThatThrownBy(() -> userRepository.save(user))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error saving user: alice");

        assertThatThrownBy(() -> userRepository.update(user))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error updating user id: 1");

        assertThatThrownBy(() -> userRepository.delete(1L))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error deleting user id: 1");
    }

    @Test
    void testEventRepositoryThrowsRepositoryExceptionOnConnectionFailure() {
        Event event = new Event(1L, "Opera", "2026-10-15", "Florence", 100, 100);

        assertThatThrownBy(() -> eventRepository.findAll())
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error finding all events");

        assertThatThrownBy(() -> eventRepository.findById(1L))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error finding event by id: 1");

        assertThatThrownBy(() -> eventRepository.save(event))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error saving event: Opera");

        assertThatThrownBy(() -> eventRepository.update(event))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error updating event id: 1");

        assertThatThrownBy(() -> eventRepository.delete(1L))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error deleting event id: 1");
    }

    @Test
    void testSeatRepositoryThrowsRepositoryExceptionOnConnectionFailure() {
        Seat seat = new Seat(1L, 10L, "VIP-1", "VIP", 150.0, "AVAILABLE");

        assertThatThrownBy(() -> seatRepository.findAll())
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error finding all seats");

        assertThatThrownBy(() -> seatRepository.findByEventId(10L))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error finding seats by event_id: 10");

        assertThatThrownBy(() -> seatRepository.findById(1L))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error finding seat by id: 1");

        assertThatThrownBy(() -> seatRepository.save(seat))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error saving seat: VIP-1");

        assertThatThrownBy(() -> seatRepository.update(seat))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error updating seat id: 1");

        assertThatThrownBy(() -> seatRepository.delete(1L))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error deleting seat id: 1");
    }

    @Test
    void testReservationRepositoryThrowsRepositoryExceptionOnConnectionFailure() {
        TicketReservation reservation = new TicketReservation(1L, "2026-10-28", "Alice", 150.0, 1L, 10L, 1L);

        assertThatThrownBy(() -> reservationRepository.findAll())
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error finding all reservations");

        assertThatThrownBy(() -> reservationRepository.findById(1L))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error finding reservation by id: 1");

        assertThatThrownBy(() -> reservationRepository.findByUserId(1L))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error finding reservations by user_id: 1");

        assertThatThrownBy(() -> reservationRepository.findByEventId(10L))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error finding reservations by event_id: 10");

        assertThatThrownBy(() -> reservationRepository.delete(1L))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Error deleting reservation id: 1");

        assertThatThrownBy(() -> reservationRepository.createReservationTransaction(reservation))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Failed to execute reservation transaction");
    }
}
