package com.ticketreservation.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SeatTest {

    @Test
    void testSeatConstructorsAndGettersSetters() {
        Seat seat = new Seat();
        seat.setId(1L);
        seat.setEventId(10L);
        seat.setSeatNumber("A-101");
        seat.setSection("VIP");
        seat.setPrice(150.0);
        seat.setStatus("AVAILABLE");

        assertThat(seat.getId()).isEqualTo(1L);
        assertThat(seat.getEventId()).isEqualTo(10L);
        assertThat(seat.getSeatNumber()).isEqualTo("A-101");
        assertThat(seat.getSection()).isEqualTo("VIP");
        assertThat(seat.getPrice()).isEqualTo(150.0);
        assertThat(seat.getStatus()).isEqualTo("AVAILABLE");

        Seat seat2 = new Seat(2L, 10L, "B-201", "REGULAR", 75.0, "RESERVED");
        assertThat(seat2.getId()).isEqualTo(2L);
        assertThat(seat2.getEventId()).isEqualTo(10L);
        assertThat(seat2.getSeatNumber()).isEqualTo("B-201");
        assertThat(seat2.getSection()).isEqualTo("REGULAR");
        assertThat(seat2.getPrice()).isEqualTo(75.0);
        assertThat(seat2.getStatus()).isEqualTo("RESERVED");
    }

    @Test
    void testEqualsAndHashCode() {
        Seat seat1 = new Seat(1L, 10L, "A-101", "VIP", 150.0, "AVAILABLE");
        Seat seat2 = new Seat(1L, 10L, "A-101", "VIP", 150.0, "AVAILABLE");
        Seat seat3 = new Seat(2L, 10L, "A-101", "VIP", 150.0, "AVAILABLE");

        // Self-equality test (covers this == o branch)
        assertThat(seat1).isEqualTo(seat1);

        // Equality with identical object
        assertThat(seat1).isEqualTo(seat2);
        assertThat(seat1.hashCode()).isEqualTo(seat2.hashCode());

        // Inequality tests
        assertThat(seat1).isNotEqualTo(seat3);
        assertThat(seat1).isNotEqualTo(null);
        assertThat(seat1).isNotEqualTo("other object");
    }

    @Test
    void testToString() {
        Seat seat = new Seat(1L, 10L, "A-101", "VIP", 150.0, "AVAILABLE");
        assertThat(seat.toString()).contains("id=1", "eventId=10", "seatNumber='A-101'", "price=150.0");
    }
}
