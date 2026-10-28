package com.ticketreservation.controller;

import com.ticketreservation.model.Event;
import com.ticketreservation.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventRepository eventRepository;

    private EventController eventController;

    @BeforeEach
    void setUp() {
        eventController = new EventController(eventRepository);
    }

    @Test
    void testGetAllEvents() {
        Event e1 = new Event(1L, "Concert", "2026-10-10", "Hall", 100, 100);
        when(eventRepository.findAll()).thenReturn(List.of(e1));

        List<Event> events = eventController.getAllEvents();
        assertThat(events).containsExactly(e1);
    }

    @Test
    void testGetEventByIdSuccessAndNotFound() {
        Event e1 = new Event(1L, "Concert", "2026-10-10", "Hall", 100, 100);
        when(eventRepository.findById(1L)).thenReturn(e1);

        assertThat(eventController.getEventById(1L)).isEqualTo(e1);

        when(eventRepository.findById(2L)).thenReturn(null);
        assertThatThrownBy(() -> eventController.getEventById(2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Event not found with id: 2");
    }

    @Test
    void testCreateEventSuccess() {
        Event event = new Event(0L, "Concert", "2026-10-10", "Hall", 100, 100);
        when(eventRepository.save(event)).thenReturn(new Event(1L, "Concert", "2026-10-10", "Hall", 100, 100));

        Event created = eventController.createEvent(event);
        assertThat(created.getId()).isEqualTo(1L);
    }

    @Test
    void testCreateEventValidationFailures() {
        assertThatThrownBy(() -> eventController.createEvent(null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> eventController.createEvent(new Event(0L, null, "2026-10-10", "Hall", 100, 100)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> eventController.createEvent(new Event(0L, "", "2026-10-10", "Hall", 100, 100)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> eventController.createEvent(new Event(0L, "Concert", null, "Hall", 100, 100)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> eventController.createEvent(new Event(0L, "Concert", "", "Hall", 100, 100)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> eventController.createEvent(new Event(0L, "Concert", "2026-10-10", null, 100, 100)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> eventController.createEvent(new Event(0L, "Concert", "2026-10-10", "", 100, 100)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> eventController.createEvent(new Event(0L, "Concert", "2026-10-10", "Hall", 0, 0)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> eventController.createEvent(new Event(0L, "Concert", "2026-10-10", "Hall", -1, 0)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> eventController.createEvent(new Event(0L, "Concert", "2026-10-10", "Hall", 100, -1)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> eventController.createEvent(new Event(0L, "Concert", "2026-10-10", "Hall", 100, 101)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testCreateEventZeroAvailableSeatsSoldOutSuccess() {
        Event soldOut = new Event(0L, "Sold Out", "2026-10-10", "Hall", 100, 0);
        when(eventRepository.save(soldOut)).thenReturn(new Event(2L, "Sold Out", "2026-10-10", "Hall", 100, 0));

        Event created = eventController.createEvent(soldOut);
        assertThat(created.getId()).isEqualTo(2L);
        assertThat(created.getAvailableSeats()).isZero();
    }

    @Test
    void testUpdateEventSuccessAndValidation() {
        Event event = new Event(1L, "Concert", "2026-10-10", "Hall", 100, 100);
        when(eventRepository.update(event)).thenReturn(event);

        assertThat(eventController.updateEvent(event)).isEqualTo(event);

        Event invalidId = new Event(0L, "Concert", "2026-10-10", "Hall", 100, 100);
        assertThatThrownBy(() -> eventController.updateEvent(invalidId))
                .isInstanceOf(IllegalArgumentException.class);

        Event negativeId = new Event(-1L, "Concert", "2026-10-10", "Hall", 100, 100);
        assertThatThrownBy(() -> eventController.updateEvent(negativeId))
                .isInstanceOf(IllegalArgumentException.class);

        Event invalidTitle = new Event(1L, "", "2026-10-10", "Hall", 100, 100);
        assertThatThrownBy(() -> eventController.updateEvent(invalidTitle))
                .isInstanceOf(IllegalArgumentException.class);

        Event invalidSeats = new Event(1L, "Concert", "2026-10-10", "Hall", 0, 0);
        assertThatThrownBy(() -> eventController.updateEvent(invalidSeats))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testDeleteEventSuccessAndValidation() {
        eventController.deleteEvent(1L);
        verify(eventRepository).delete(1L);

        assertThatThrownBy(() -> eventController.deleteEvent(0L))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> eventController.deleteEvent(-1L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
