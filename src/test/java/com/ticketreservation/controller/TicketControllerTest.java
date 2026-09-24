package com.ticketreservation.controller;

import com.ticketreservation.model.Event;
import com.ticketreservation.model.Seat;
import com.ticketreservation.model.TicketReservation;
import com.ticketreservation.repository.EventRepository;
import com.ticketreservation.repository.ReservationRepository;
import com.ticketreservation.repository.SeatRepository;
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
class TicketControllerTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private EventRepository eventRepository;

    private TicketController ticketController;

    @BeforeEach
    void setUp() {
        ticketController = new TicketController(reservationRepository, seatRepository, eventRepository);
    }

    @Test
    void testGetAllReservationsAndGetByUserId() {
        TicketReservation res = new TicketReservation(1L, "2026-09-16", "Alice", 100.0, 5L, 10L, 50L);
        when(reservationRepository.findAll()).thenReturn(List.of(res));
        when(reservationRepository.findByUserId(5L)).thenReturn(List.of(res));

        assertThat(ticketController.getAllReservations()).containsExactly(res);
        assertThat(ticketController.getReservationsByUserId(5L)).containsExactly(res);

        assertThatThrownBy(() -> ticketController.getReservationsByUserId(0L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testGetSeatsForEvent() {
        Seat s1 = new Seat(1L, 10L, "A-1", "VIP", 150.0, "AVAILABLE");
        when(seatRepository.findByEventId(10L)).thenReturn(List.of(s1));

        assertThat(ticketController.getSeatsForEvent(10L)).containsExactly(s1);

        assertThatThrownBy(() -> ticketController.getSeatsForEvent(0L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testCreateSeatSuccessAndValidation() {
        Seat seat = new Seat(0L, 10L, "A-1", "VIP", 150.0, "AVAILABLE");
        Event event = new Event(10L, "Concert", "2026-10-10", "Hall", 100, 100);

        when(eventRepository.findById(10L)).thenReturn(event);
        when(seatRepository.save(seat)).thenReturn(new Seat(1L, 10L, "A-1", "VIP", 150.0, "AVAILABLE"));

        Seat created = ticketController.createSeat(seat);
        assertThat(created.getId()).isEqualTo(1L);

        assertThatThrownBy(() -> ticketController.createSeat(null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> ticketController.createSeat(new Seat(0L, 0L, "A-1", "VIP", 150.0, "AVAILABLE")))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> ticketController.createSeat(new Seat(0L, 10L, null, "VIP", 150.0, "AVAILABLE")))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> ticketController.createSeat(new Seat(0L, 10L, "", "VIP", 150.0, "AVAILABLE")))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> ticketController.createSeat(new Seat(0L, 10L, "A-1", "VIP", 0.0, "AVAILABLE")))
                .isInstanceOf(IllegalArgumentException.class);

        when(eventRepository.findById(99L)).thenReturn(null);
        assertThatThrownBy(() -> ticketController.createSeat(new Seat(0L, 99L, "A-1", "VIP", 100.0, "AVAILABLE")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot create seat for non-existent event");
    }

    @Test
    void testReserveTicketSuccessAndValidation() {
        TicketReservation res = new TicketReservation(0L, "2026-09-16", "Alice", 150.0, 5L, 10L, 50L);
        Seat seat = new Seat(50L, 10L, "A-1", "VIP", 150.0, "AVAILABLE");

        when(seatRepository.findById(50L)).thenReturn(seat);
        when(reservationRepository.createReservationTransaction(res)).thenReturn(new TicketReservation(1L, "2026-09-16", "Alice", 150.0, 5L, 10L, 50L));

        TicketReservation created = ticketController.reserveTicket(res);
        assertThat(created.getId()).isEqualTo(1L);

        assertThatThrownBy(() -> ticketController.reserveTicket(null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> ticketController.reserveTicket(new TicketReservation(0L, "2026-09-16", null, 150.0, 5L, 10L, 50L)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> ticketController.reserveTicket(new TicketReservation(0L, "2026-09-16", "", 150.0, 5L, 10L, 50L)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> ticketController.reserveTicket(new TicketReservation(0L, null, "Alice", 150.0, 5L, 10L, 50L)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> ticketController.reserveTicket(new TicketReservation(0L, "", "Alice", 150.0, 5L, 10L, 50L)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> ticketController.reserveTicket(new TicketReservation(0L, "2026-09-16", "Alice", 150.0, 5L, 0L, 50L)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> ticketController.reserveTicket(new TicketReservation(0L, "2026-09-16", "Alice", 150.0, 5L, 10L, 0L)))
                .isInstanceOf(IllegalArgumentException.class);

        when(seatRepository.findById(99L)).thenReturn(null);
        assertThatThrownBy(() -> ticketController.reserveTicket(new TicketReservation(0L, "2026-09-16", "Alice", 150.0, 5L, 10L, 99L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Seat not found");

        Seat reservedSeat = new Seat(50L, 10L, "A-1", "VIP", 150.0, "RESERVED");
        when(seatRepository.findById(50L)).thenReturn(reservedSeat);
        assertThatThrownBy(() -> ticketController.reserveTicket(res))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Seat is not available");
    }

    @Test
    void testCancelReservationSuccessAndValidation() {
        TicketReservation res = new TicketReservation(1L, "2026-09-16", "Alice", 150.0, 5L, 10L, 50L);
        Seat seat = new Seat(50L, 10L, "A-1", "VIP", 150.0, "RESERVED");
        Event event = new Event(10L, "Concert", "2026-10-10", "Hall", 100, 50);

        when(reservationRepository.findById(1L)).thenReturn(res);
        when(seatRepository.findById(50L)).thenReturn(seat);
        when(eventRepository.findById(10L)).thenReturn(event);

        ticketController.cancelReservation(1L);

        verify(seatRepository).update(seat);
        verify(eventRepository).update(event);
        verify(reservationRepository).delete(1L);

        assertThat(seat.getStatus()).isEqualTo("AVAILABLE");
        assertThat(event.getAvailableSeats()).isEqualTo(51);

        assertThatThrownBy(() -> ticketController.cancelReservation(0L))
                .isInstanceOf(IllegalArgumentException.class);

        when(reservationRepository.findById(99L)).thenReturn(null);
        assertThatThrownBy(() -> ticketController.cancelReservation(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Reservation not found");
    }

    @Test
    void testCancelReservationWhenSeatOrEventNotFoundStillDeletesReservation() {
        TicketReservation res = new TicketReservation(2L, "2026-09-16", "Bob", 50.0, 5L, 10L, 50L);
        when(reservationRepository.findById(2L)).thenReturn(res);
        when(seatRepository.findById(50L)).thenReturn(null);
        when(eventRepository.findById(10L)).thenReturn(null);

        ticketController.cancelReservation(2L);

        verify(reservationRepository).delete(2L);
    }
}
