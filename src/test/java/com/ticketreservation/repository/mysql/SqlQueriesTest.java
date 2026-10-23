package com.ticketreservation.repository.mysql;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

class SqlQueriesTest {

    @Test
    @DisplayName("Verify event SQL constants are well-formed")
    void testEventQueries() {
        assertThat(SqlQueries.SELECT_ALL_EVENTS).contains("SELECT", "events");
        assertThat(SqlQueries.SELECT_EVENT_BY_ID).contains("WHERE id = ?");
        assertThat(SqlQueries.INSERT_EVENT).contains("INSERT INTO events");
        assertThat(SqlQueries.UPDATE_EVENT).contains("UPDATE events SET");
        assertThat(SqlQueries.DELETE_EVENT).contains("DELETE FROM events");
    }

    @Test
    @DisplayName("Verify seat SQL constants are well-formed")
    void testSeatQueries() {
        assertThat(SqlQueries.SELECT_ALL_SEATS).contains("SELECT", "seats");
        assertThat(SqlQueries.SELECT_SEATS_BY_EVENT).contains("event_id = ?");
        assertThat(SqlQueries.SELECT_SEAT_BY_ID).contains("WHERE id = ?");
        assertThat(SqlQueries.INSERT_SEAT).contains("INSERT INTO seats");
        assertThat(SqlQueries.UPDATE_SEAT_STATUS).contains("UPDATE seats SET status = ?");
    }

    @Test
    @DisplayName("Verify reservation and user SQL constants are well-formed")
    void testReservationAndUserQueries() {
        assertThat(SqlQueries.SELECT_ALL_RESERVATIONS).contains("ticket_reservations");
        assertThat(SqlQueries.SELECT_RESERVATION_BY_ID).contains("WHERE id = ?");
        assertThat(SqlQueries.INSERT_RESERVATION).contains("INSERT INTO ticket_reservations");

        assertThat(SqlQueries.SELECT_ALL_USERS).contains("users");
        assertThat(SqlQueries.SELECT_USER_BY_ID).contains("WHERE id = ?");
        assertThat(SqlQueries.SELECT_USER_BY_USERNAME).contains("WHERE username = ?");
        assertThat(SqlQueries.INSERT_USER).contains("INSERT INTO users");
    }

    @Test
    @DisplayName("Private constructor should be callable via reflection")
    void privateConstructorTest() throws Exception {
        Constructor<SqlQueries> constructor = SqlQueries.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        SqlQueries instance = constructor.newInstance();
        assertThat(instance).isNotNull();
    }
}
