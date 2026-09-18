package com.ticketreservation.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TicketReservationTest {

    @Test
    void testTicketReservationConstructorsAndGettersSetters() {
        TicketReservation res = new TicketReservation();
        res.setId(1L);
        res.setReservationDate("2026-09-16");
        res.setCustomerName("Alice");
        res.setTotalAmount(150.0);
        res.setUserId(5L);
        res.setEventId(10L);
        res.setSeatId(100L);

        assertThat(res.getId()).isEqualTo(1L);
        assertThat(res.getReservationDate()).isEqualTo("2026-09-16");
        assertThat(res.getCustomerName()).isEqualTo("Alice");
        assertThat(res.getTotalAmount()).isEqualTo(150.0);
        assertThat(res.getUserId()).isEqualTo(5L);
        assertThat(res.getEventId()).isEqualTo(10L);
        assertThat(res.getSeatId()).isEqualTo(100L);

        TicketReservation res2 = new TicketReservation(2L, "2026-09-17", "Bob", 75.0, null, 10L, 101L);
        assertThat(res2.getId()).isEqualTo(2L);
        assertThat(res2.getReservationDate()).isEqualTo("2026-09-17");
        assertThat(res2.getCustomerName()).isEqualTo("Bob");
        assertThat(res2.getTotalAmount()).isEqualTo(75.0);
        assertThat(res2.getUserId()).isNull();
        assertThat(res2.getEventId()).isEqualTo(10L);
        assertThat(res2.getSeatId()).isEqualTo(101L);
    }

    @Test
    void testEqualsAndHashCode() {
        TicketReservation res1 = new TicketReservation(1L, "2026-09-16", "Alice", 150.0, 5L, 10L, 100L);
        TicketReservation res2 = new TicketReservation(1L, "2026-09-16", "Alice", 150.0, 5L, 10L, 100L);
        TicketReservation res3 = new TicketReservation(2L, "2026-09-16", "Alice", 150.0, 5L, 10L, 100L);

        // Self-equality test (covers this == o branch)
        assertThat(res1).isEqualTo(res1);

        // Equality with identical object
        assertThat(res1).isEqualTo(res2);
        assertThat(res1.hashCode()).isEqualTo(res2.hashCode());

        // Inequality tests
        assertThat(res1).isNotEqualTo(res3);
        assertThat(res1).isNotEqualTo(null);
        assertThat(res1).isNotEqualTo("other object");
    }

    @Test
    void testToString() {
        TicketReservation res = new TicketReservation(1L, "2026-09-16", "Alice", 150.0, 5L, 10L, 100L);
        assertThat(res.toString()).contains("id=1", "customerName='Alice'", "totalAmount=150.0", "seatId=100");
    }
}
