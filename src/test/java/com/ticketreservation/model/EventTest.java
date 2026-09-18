package com.ticketreservation.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    void testEventConstructorsAndGettersSetters() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Concert");
        event.setEventDate("2026-10-10");
        event.setVenueName("Opera House");
        event.setTotalSeats(100);
        event.setAvailableSeats(80);

        assertThat(event.getId()).isEqualTo(1L);
        assertThat(event.getTitle()).isEqualTo("Concert");
        assertThat(event.getEventDate()).isEqualTo("2026-10-10");
        assertThat(event.getVenueName()).isEqualTo("Opera House");
        assertThat(event.getTotalSeats()).isEqualTo(100);
        assertThat(event.getAvailableSeats()).isEqualTo(80);

        Event event2 = new Event(2L, "Festival", "2026-11-11", "Arena", 500, 450);
        assertThat(event2.getId()).isEqualTo(2L);
        assertThat(event2.getTitle()).isEqualTo("Festival");
        assertThat(event2.getEventDate()).isEqualTo("2026-11-11");
        assertThat(event2.getVenueName()).isEqualTo("Arena");
        assertThat(event2.getTotalSeats()).isEqualTo(500);
        assertThat(event2.getAvailableSeats()).isEqualTo(450);
    }

    @Test
    void testEqualsAndHashCode() {
        Event event1 = new Event(1L, "Concert", "2026-10-10", "Opera House", 100, 80);
        Event event2 = new Event(1L, "Concert", "2026-10-10", "Opera House", 100, 80);
        Event event3 = new Event(2L, "Concert", "2026-10-10", "Opera House", 100, 80);

        // Self-equality test (covers this == o branch)
        assertThat(event1).isEqualTo(event1);

        // Equality with identical object
        assertThat(event1).isEqualTo(event2);
        assertThat(event1.hashCode()).isEqualTo(event2.hashCode());

        // Inequality tests
        assertThat(event1).isNotEqualTo(event3);
        assertThat(event1).isNotEqualTo(null);
        assertThat(event1).isNotEqualTo("other object");
    }

    @Test
    void testToString() {
        Event event = new Event(1L, "Concert", "2026-10-10", "Opera House", 100, 80);
        assertThat(event.toString()).contains("id=1", "title='Concert'", "venueName='Opera House'", "totalSeats=100");
    }
}
