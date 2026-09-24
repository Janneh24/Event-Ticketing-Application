package com.ticketreservation.repository.mysql;

import com.ticketreservation.model.Event;
import com.ticketreservation.model.Seat;
import com.ticketreservation.model.TicketReservation;
import com.ticketreservation.model.User;
import com.ticketreservation.repository.RepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MysqlRepositoriesExceptionTest {

    private DataSource mockDataSource;
    private Connection mockConnection;

    private MysqlEventRepository eventRepository;
    private MysqlSeatRepository seatRepository;
    private MysqlUserRepository userRepository;
    private MysqlReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        mockDataSource = mock(DataSource.class);
        mockConnection = mock(Connection.class);

        eventRepository = new MysqlEventRepository(mockDataSource);
        seatRepository = new MysqlSeatRepository(mockDataSource);
        userRepository = new MysqlUserRepository(mockDataSource);
        reservationRepository = new MysqlReservationRepository(mockDataSource);
    }

    @Test
    void testEventRepositorySqlExceptions() throws Exception {
        when(mockDataSource.getConnection()).thenThrow(new SQLException("DB down"));

        Event event = new Event(1L, "Concert", "2026-10-10", "Hall", 100, 100);

        assertThatThrownBy(() -> eventRepository.findAll()).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> eventRepository.findById(1L)).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> eventRepository.save(event)).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> eventRepository.update(event)).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> eventRepository.delete(1L)).isInstanceOf(RepositoryException.class);
    }

    @Test
    void testSeatRepositorySqlExceptions() throws Exception {
        when(mockDataSource.getConnection()).thenThrow(new SQLException("DB down"));

        Seat seat = new Seat(1L, 10L, "A-1", "VIP", 100.0, "AVAILABLE");

        assertThatThrownBy(() -> seatRepository.findAll()).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> seatRepository.findById(1L)).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> seatRepository.findByEventId(10L)).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> seatRepository.save(seat)).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> seatRepository.update(seat)).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> seatRepository.delete(1L)).isInstanceOf(RepositoryException.class);
    }

    @Test
    void testUserRepositorySqlExceptions() throws Exception {
        when(mockDataSource.getConnection()).thenThrow(new SQLException("DB down"));

        User user = new User(1L, "alice", "pass", "CUSTOMER", true);

        assertThatThrownBy(() -> userRepository.findAll()).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> userRepository.findById(1L)).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> userRepository.findByUsername("alice")).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> userRepository.save(user)).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> userRepository.update(user)).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> userRepository.delete(1L)).isInstanceOf(RepositoryException.class);
    }

    @Test
    void testReservationRepositorySqlExceptions() throws Exception {
        when(mockDataSource.getConnection()).thenThrow(new SQLException("DB down"));

        TicketReservation res = new TicketReservation(1L, "2026-09-16", "Alice", 100.0, 1L, 10L, 50L);

        assertThatThrownBy(() -> reservationRepository.findAll()).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> reservationRepository.findById(1L)).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> reservationRepository.findByUserId(1L)).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> reservationRepository.findByEventId(10L)).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> reservationRepository.delete(1L)).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> reservationRepository.createReservationTransaction(res)).isInstanceOf(RepositoryException.class);
    }

    @Test
    void testCreateReservationTransactionRollbackAndCloseExceptions() throws Exception {
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Query error"));
        doThrow(new SQLException("Rollback error")).when(mockConnection).rollback();
        doThrow(new SQLException("Close error")).when(mockConnection).close();

        TicketReservation res = new TicketReservation(0L, "2026-09-16", "Alice", 100.0, 1L, 10L, 50L);

        assertThatThrownBy(() -> reservationRepository.createReservationTransaction(res))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Failed to execute reservation transaction");

        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

    @Test
    void testSeatRepositoryFindAllAndBranches() throws Exception {
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        // test findAll with one seat
        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getLong("id")).thenReturn(1L);
        when(mockRs.getLong("event_id")).thenReturn(10L);
        when(mockRs.getString("seat_number")).thenReturn("A-1");
        when(mockRs.getString("section")).thenReturn("VIP");
        when(mockRs.getDouble("price")).thenReturn(100.0);
        when(mockRs.getString("status")).thenReturn("AVAILABLE");

        var seats = seatRepository.findAll();
        assertThat(seats).hasSize(1);
        assertThat(seats.get(0).getSeatNumber()).isEqualTo("A-1");

        // test findById when not found
        when(mockRs.next()).thenReturn(false);
        assertThat(seatRepository.findById(99L)).isNull();

        // test save when generatedKeys has no next
        ResultSet emptyKeys = mock(ResultSet.class);
        when(emptyKeys.next()).thenReturn(false);
        when(mockConnection.prepareStatement(anyString(), org.mockito.ArgumentMatchers.anyInt())).thenReturn(mockPs);
        when(mockPs.getGeneratedKeys()).thenReturn(emptyKeys);
        Seat seatToSave = new Seat(0L, 10L, "A-1", "VIP", 100.0, "AVAILABLE");
        Seat saved = seatRepository.save(seatToSave);
        assertThat(saved.getId()).isEqualTo(0L);
    }

    @Test
    void testEventRepositoryBranches() throws Exception {
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        // findById not found
        when(mockRs.next()).thenReturn(false);
        assertThat(eventRepository.findById(99L)).isNull();

        // save when generatedKeys has no next
        ResultSet emptyKeys = mock(ResultSet.class);
        when(emptyKeys.next()).thenReturn(false);
        when(mockConnection.prepareStatement(anyString(), org.mockito.ArgumentMatchers.anyInt())).thenReturn(mockPs);
        when(mockPs.getGeneratedKeys()).thenReturn(emptyKeys);
        Event eventToSave = new Event(0L, "Show", "2026-10-10", "Hall", 100, 100);
        Event saved = eventRepository.save(eventToSave);
        assertThat(saved.getId()).isEqualTo(0L);
    }

    @Test
    void testUserRepositoryBranches() throws Exception {
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        // findById not found
        when(mockRs.next()).thenReturn(false);
        assertThat(userRepository.findById(99L)).isNull();

        // findByUsername not found
        when(mockRs.next()).thenReturn(false);
        assertThat(userRepository.findByUsername("unknown")).isNull();

        // save when generatedKeys has no next
        ResultSet emptyKeys = mock(ResultSet.class);
        when(emptyKeys.next()).thenReturn(false);
        when(mockConnection.prepareStatement(anyString(), org.mockito.ArgumentMatchers.anyInt())).thenReturn(mockPs);
        when(mockPs.getGeneratedKeys()).thenReturn(emptyKeys);
        User userToSave = new User(0L, "alice", "pass", "CUSTOMER", true);
        User saved = userRepository.save(userToSave);
        assertThat(saved.getId()).isEqualTo(0L);
    }

    @Test
    void testReservationRepositoryQueriesAndBranches() throws Exception {
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        // findById not found
        when(mockRs.next()).thenReturn(false);
        assertThat(reservationRepository.findById(99L)).isNull();

        // findByUserId with item having null userId (rs.wasNull() == true)
        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getLong("id")).thenReturn(1L);
        when(mockRs.getString("reservation_date")).thenReturn("2026-09-16");
        when(mockRs.getString("customer_name")).thenReturn("Alice");
        when(mockRs.getDouble("total_amount")).thenReturn(100.0);
        when(mockRs.getLong("user_id")).thenReturn(0L);
        when(mockRs.wasNull()).thenReturn(true);
        when(mockRs.getLong("event_id")).thenReturn(10L);
        when(mockRs.getLong("seat_id")).thenReturn(50L);

        var userReservations = reservationRepository.findByUserId(1L);
        assertThat(userReservations).hasSize(1);
        assertThat(userReservations.get(0).getUserId()).isNull();

        // findByEventId with item
        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getLong("id")).thenReturn(2L);
        when(mockRs.getString("reservation_date")).thenReturn("2026-09-16");
        when(mockRs.getString("customer_name")).thenReturn("Bob");
        when(mockRs.getDouble("total_amount")).thenReturn(100.0);
        when(mockRs.getLong("user_id")).thenReturn(5L);
        when(mockRs.wasNull()).thenReturn(false);
        when(mockRs.getLong("event_id")).thenReturn(10L);
        when(mockRs.getLong("seat_id")).thenReturn(51L);

        var eventReservations = reservationRepository.findByEventId(10L);
        assertThat(eventReservations).hasSize(1);
        assertThat(eventReservations.get(0).getUserId()).isEqualTo(5L);
    }

    @Test
    void testCreateReservationTransactionBranches() throws Exception {
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        PreparedStatement psCheck = mock(PreparedStatement.class);
        PreparedStatement psUpdateSeat = mock(PreparedStatement.class);
        PreparedStatement psUpdateEvent = mock(PreparedStatement.class);
        PreparedStatement psInsert = mock(PreparedStatement.class);

        ResultSet rsCheck = mock(ResultSet.class);
        when(psCheck.executeQuery()).thenReturn(rsCheck);

        when(mockConnection.prepareStatement(org.mockito.ArgumentMatchers.contains("SELECT status FROM seats"))).thenReturn(psCheck);
        when(mockConnection.prepareStatement(org.mockito.ArgumentMatchers.contains("UPDATE seats SET status"))).thenReturn(psUpdateSeat);
        when(mockConnection.prepareStatement(org.mockito.ArgumentMatchers.contains("UPDATE events SET available_seats"))).thenReturn(psUpdateEvent);
        when(mockConnection.prepareStatement(org.mockito.ArgumentMatchers.contains("INSERT INTO ticket_reservations"), org.mockito.ArgumentMatchers.anyInt())).thenReturn(psInsert);

        TicketReservation resWithNullUser = new TicketReservation(0L, "2026-09-16", "Alice", 100.0, null, 10L, 50L);

        // 1. Seat not available (rsCheck.next() == false)
        when(rsCheck.next()).thenReturn(false);
        assertThatThrownBy(() -> reservationRepository.createReservationTransaction(resWithNullUser))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Seat is not available");
        verify(mockConnection, org.mockito.Mockito.atLeastOnce()).rollback();

        // 2. Seat status not AVAILABLE (e.g. RESERVED)
        when(rsCheck.next()).thenReturn(true);
        when(rsCheck.getString("status")).thenReturn("RESERVED");
        assertThatThrownBy(() -> reservationRepository.createReservationTransaction(resWithNullUser))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Seat is not available");

        // 3. No available seats in event (psUpdateEvent.executeUpdate() == 0)
        when(rsCheck.getString("status")).thenReturn("AVAILABLE");
        when(psUpdateEvent.executeUpdate()).thenReturn(0);
        assertThatThrownBy(() -> reservationRepository.createReservationTransaction(resWithNullUser))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("No available seats left");

        // 4. Success with null userId and generatedKeys.next() == false
        when(psUpdateEvent.executeUpdate()).thenReturn(1);
        ResultSet emptyKeys = mock(ResultSet.class);
        when(emptyKeys.next()).thenReturn(false);
        when(psInsert.getGeneratedKeys()).thenReturn(emptyKeys);

        TicketReservation created = reservationRepository.createReservationTransaction(resWithNullUser);
        assertThat(created.getId()).isEqualTo(0L);
        verify(psInsert).setNull(org.mockito.ArgumentMatchers.eq(4), org.mockito.ArgumentMatchers.eq(java.sql.Types.BIGINT));
        verify(mockConnection).commit();
    }

    @Test
    void testCreateReservationTransactionRollbackSucceeds() throws Exception {
        // This covers lines 164-170: conn.rollback() succeeds (no exception from rollback)
        Connection conn = mock(Connection.class);
        when(mockDataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Prepare failed"));
        // rollback succeeds (does NOT throw)

        TicketReservation res = new TicketReservation(0L, "2026-09-16", "Alice", 100.0, 1L, 10L, 50L);

        assertThatThrownBy(() -> reservationRepository.createReservationTransaction(res))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Failed to execute reservation transaction");

        verify(conn).setAutoCommit(false);
        verify(conn).rollback();
        verify(conn).setAutoCommit(true);
        verify(conn).close();
    }

    @Test
    void testUserRepositorySuccessOperations() throws Exception {
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockConnection.prepareStatement(anyString(), org.mockito.ArgumentMatchers.anyInt())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getLong("id")).thenReturn(1L);
        when(mockRs.getString("username")).thenReturn("alice");
        when(mockRs.getString("password")).thenReturn("pass");
        when(mockRs.getString("role")).thenReturn("CUSTOMER");
        when(mockRs.getBoolean("enabled")).thenReturn(true);

        // findAll
        var users = userRepository.findAll();
        assertThat(users).hasSize(1);

        // findById
        when(mockRs.next()).thenReturn(true);
        assertThat(userRepository.findById(1L)).isNotNull();

        // findByUsername
        when(mockRs.next()).thenReturn(true);
        assertThat(userRepository.findByUsername("alice")).isNotNull();

        // save with generated key
        ResultSet keys = mock(ResultSet.class);
        when(keys.next()).thenReturn(true);
        when(keys.getLong(1)).thenReturn(10L);
        when(mockPs.getGeneratedKeys()).thenReturn(keys);
        User saved = userRepository.save(new User(0L, "bob", "pass", "CUSTOMER", true));
        assertThat(saved.getId()).isEqualTo(10L);

        // update
        User updated = userRepository.update(saved);
        assertThat(updated).isNotNull();

        // delete
        userRepository.delete(10L);
        verify(mockPs).setLong(1, 10L);
    }

    @Test
    void testEventRepositorySuccessOperations() throws Exception {
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockConnection.prepareStatement(anyString(), org.mockito.ArgumentMatchers.anyInt())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getLong("id")).thenReturn(1L);
        when(mockRs.getString("title")).thenReturn("Concert");
        when(mockRs.getString("event_date")).thenReturn("2026-10-10");
        when(mockRs.getString("venue_name")).thenReturn("Hall");
        when(mockRs.getInt("total_seats")).thenReturn(100);
        when(mockRs.getInt("available_seats")).thenReturn(100);

        // findAll
        var events = eventRepository.findAll();
        assertThat(events).hasSize(1);

        // findById
        when(mockRs.next()).thenReturn(true);
        assertThat(eventRepository.findById(1L)).isNotNull();

        // save with key
        ResultSet keys = mock(ResultSet.class);
        when(keys.next()).thenReturn(true);
        when(keys.getLong(1)).thenReturn(5L);
        when(mockPs.getGeneratedKeys()).thenReturn(keys);
        Event saved = eventRepository.save(new Event(0L, "Concert", "2026-10-10", "Hall", 100, 100));
        assertThat(saved.getId()).isEqualTo(5L);

        // update
        Event updated = eventRepository.update(saved);
        assertThat(updated).isNotNull();

        // delete
        eventRepository.delete(5L);
        verify(mockPs).setLong(1, 5L);
    }

    @Test
    void testSeatRepositorySuccessOperations() throws Exception {
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockConnection.prepareStatement(anyString(), org.mockito.ArgumentMatchers.anyInt())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getLong("id")).thenReturn(1L);
        when(mockRs.getLong("event_id")).thenReturn(10L);
        when(mockRs.getString("seat_number")).thenReturn("A-1");
        when(mockRs.getString("section")).thenReturn("VIP");
        when(mockRs.getDouble("price")).thenReturn(100.0);
        when(mockRs.getString("status")).thenReturn("AVAILABLE");

        // findByEventId
        var seats = seatRepository.findByEventId(10L);
        assertThat(seats).hasSize(1);

        // findById
        when(mockRs.next()).thenReturn(true);
        assertThat(seatRepository.findById(1L)).isNotNull();

        // save with key
        ResultSet keys = mock(ResultSet.class);
        when(keys.next()).thenReturn(true);
        when(keys.getLong(1)).thenReturn(25L);
        when(mockPs.getGeneratedKeys()).thenReturn(keys);
        Seat saved = seatRepository.save(new Seat(0L, 10L, "A-1", "VIP", 100.0, "AVAILABLE"));
        assertThat(saved.getId()).isEqualTo(25L);

        // update
        Seat updated = seatRepository.update(saved);
        assertThat(updated).isNotNull();

        // delete
        seatRepository.delete(25L);
        verify(mockPs).setLong(1, 25L);
    }

    @Test
    void testReservationRepositorySuccessOperations() throws Exception {
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockConnection.prepareStatement(anyString(), org.mockito.ArgumentMatchers.anyInt())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getLong("id")).thenReturn(1L);
        when(mockRs.getString("reservation_date")).thenReturn("2026-09-16");
        when(mockRs.getString("customer_name")).thenReturn("Alice");
        when(mockRs.getDouble("total_amount")).thenReturn(100.0);
        when(mockRs.getLong("user_id")).thenReturn(1L);
        when(mockRs.wasNull()).thenReturn(false);
        when(mockRs.getLong("event_id")).thenReturn(10L);
        when(mockRs.getLong("seat_id")).thenReturn(50L);

        // findAll
        var list = reservationRepository.findAll();
        assertThat(list).hasSize(1);

        // findById
        when(mockRs.next()).thenReturn(true);
        assertThat(reservationRepository.findById(1L)).isNotNull();

        // delete
        reservationRepository.delete(999L);
        verify(mockPs).setLong(1, 999L);

        // createReservationTransaction with userId != null and generatedKeys.next() == true
        PreparedStatement psCheck = mock(PreparedStatement.class);
        PreparedStatement psUpdateSeat = mock(PreparedStatement.class);
        PreparedStatement psUpdateEvent = mock(PreparedStatement.class);
        PreparedStatement psInsert = mock(PreparedStatement.class);
        ResultSet rsCheck = mock(ResultSet.class);
        when(psCheck.executeQuery()).thenReturn(rsCheck);
        when(rsCheck.next()).thenReturn(true);
        when(rsCheck.getString("status")).thenReturn("AVAILABLE");
        when(psUpdateEvent.executeUpdate()).thenReturn(1);

        ResultSet keys = mock(ResultSet.class);
        when(keys.next()).thenReturn(true);
        when(keys.getLong(1)).thenReturn(77L);
        when(psInsert.getGeneratedKeys()).thenReturn(keys);

        when(mockConnection.prepareStatement(org.mockito.ArgumentMatchers.contains("SELECT status FROM seats"))).thenReturn(psCheck);
        when(mockConnection.prepareStatement(org.mockito.ArgumentMatchers.contains("UPDATE seats SET status"))).thenReturn(psUpdateSeat);
        when(mockConnection.prepareStatement(org.mockito.ArgumentMatchers.contains("UPDATE events SET available_seats"))).thenReturn(psUpdateEvent);
        when(mockConnection.prepareStatement(org.mockito.ArgumentMatchers.contains("INSERT INTO ticket_reservations"), org.mockito.ArgumentMatchers.anyInt())).thenReturn(psInsert);

        TicketReservation resWithUser = new TicketReservation(0L, "2026-09-16", "Alice", 100.0, 5L, 10L, 50L);
        TicketReservation result = reservationRepository.createReservationTransaction(resWithUser);
        assertThat(result.getId()).isEqualTo(77L);
        verify(psInsert).setLong(4, 5L);
    }
}


