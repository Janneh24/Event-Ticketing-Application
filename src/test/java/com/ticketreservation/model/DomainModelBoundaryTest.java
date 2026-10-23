package com.ticketreservation.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainModelBoundaryTest {

    @Test
    @DisplayName("Event boundary conditions and contracts")
    void testEventBoundaryConditions() {
        Event e1 = new Event(0L, "", "", "", 0, 0.0);
        Event e2 = new Event(0L, "", "", "", 0, 0.0);

        assertThat(e1).isEqualTo(e2);
        assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
        assertThat(e1).isNotEqualTo(null);
        assertThat(e1).isNotEqualTo(new Object());

        e1.setId(Long.MAX_VALUE);
        e1.setPrice(Double.MAX_VALUE);
        e1.setTotalSeats(Integer.MAX_VALUE);
        assertThat(e1.getId()).isEqualTo(Long.MAX_VALUE);
        assertThat(e1.getPrice()).isEqualTo(Double.MAX_VALUE);
        assertThat(e1.getTotalSeats()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    @DisplayName("Seat boundary conditions and contracts")
    void testSeatBoundaryConditions() {
        Seat s1 = new Seat(15L, 15L, "A1", SeatStatus.AVAILABLE);
        Seat s2 = new Seat(15L, 15L, "A1", SeatStatus.AVAILABLE);

        assertThat(s1).isEqualTo(s2);
        assertThat(s1.hashCode()).isEqualTo(s2.hashCode());
        assertThat(s1).isNotEqualTo(null);
        assertThat(s1).isNotEqualTo("StringObject");

        s1.setStatus(SeatStatus.BOOKED);
        assertThat(s1.getStatus()).isEqualTo(SeatStatus.BOOKED);
    }

    @Test
    @DisplayName("TicketReservation boundary conditions and contracts")
    void testTicketReservationBoundaryConditions() {
        TicketReservation r1 = new TicketReservation(15L, 15L, 15L, "Bob", "2026-10-19", 50.0);
        TicketReservation r2 = new TicketReservation(15L, 15L, 15L, "Bob", "2026-10-19", 50.0);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
        assertThat(r1).isNotEqualTo(null);
        assertThat(r1).isNotEqualTo(new Object());

        r1.setAmount(9999.99);
        assertThat(r1.getAmount()).isEqualTo(9999.99);
    }

    @Test
    @DisplayName("User boundary conditions and contracts")
    void testUserBoundaryConditions() {
        User u1 = new User(15L, "admin", "pwd", UserRole.ORGANIZER, true);
        User u2 = new User(15L, "admin", "pwd", UserRole.ORGANIZER, true);

        assertThat(u1).isEqualTo(u2);
        assertThat(u1.hashCode()).isEqualTo(u2.hashCode());
        assertThat(u1).isNotEqualTo(null);
        assertThat(u1).isNotEqualTo(new Object());

        u1.setActive(false);
        assertThat(u1.isActive()).isFalse();
    }
}
