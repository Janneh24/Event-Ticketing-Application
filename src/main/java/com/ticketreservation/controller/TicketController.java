package com.ticketreservation.controller;

import com.ticketreservation.model.Event;
import com.ticketreservation.model.Seat;
import com.ticketreservation.model.TicketReservation;
import com.ticketreservation.repository.EventRepository;
import com.ticketreservation.repository.ReservationRepository;
import com.ticketreservation.repository.SeatRepository;

import java.util.List;

public class TicketController {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;

    public TicketController(ReservationRepository reservationRepository,
                            SeatRepository seatRepository,
                            EventRepository eventRepository) {
        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
        this.eventRepository = eventRepository;
    }

    public List<TicketReservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public List<TicketReservation> getReservationsByUserId(long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        return reservationRepository.findByUserId(userId);
    }

    public List<Seat> getSeatsForEvent(long eventId) {
        if (eventId <= 0) {
            throw new IllegalArgumentException("Event ID must be positive");
        }
        return seatRepository.findByEventId(eventId);
    }

    public Seat createSeat(Seat seat) {
        validateSeat(seat);
        Event event = eventRepository.findById(seat.getEventId());
        if (event == null) {
            throw new IllegalArgumentException("Cannot create seat for non-existent event id: " + seat.getEventId());
        }
        return seatRepository.save(seat);
    }

    public TicketReservation reserveTicket(TicketReservation reservation) {
        validateReservation(reservation);

        Seat seat = seatRepository.findById(reservation.getSeatId());
        if (seat == null) {
            throw new IllegalArgumentException("Seat not found with id: " + reservation.getSeatId());
        }
        if (!"AVAILABLE".equalsIgnoreCase(seat.getStatus())) {
            throw new IllegalArgumentException("Seat is not available for reservation");
        }

        // Executes atomic SQL Transaction
        return reservationRepository.createReservationTransaction(reservation);
    }

    public void cancelReservation(long reservationId) {
        if (reservationId <= 0) {
            throw new IllegalArgumentException("Reservation ID must be positive for cancellation");
        }
        TicketReservation reservation = reservationRepository.findById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found with id: " + reservationId);
        }

        // Update seat back to AVAILABLE
        Seat seat = seatRepository.findById(reservation.getSeatId());
        if (seat != null) {
            seat.setStatus("AVAILABLE");
            seatRepository.update(seat);
        }

        // Increment event available_seats
        Event event = eventRepository.findById(reservation.getEventId());
        if (event != null) {
            event.setAvailableSeats(event.getAvailableSeats() + 1);
            eventRepository.update(event);
        }

        reservationRepository.delete(reservationId);
    }

    private void validateSeat(Seat seat) {
        if (seat == null) {
            throw new IllegalArgumentException("Seat cannot be null");
        }
        if (seat.getEventId() <= 0) {
            throw new IllegalArgumentException("Event ID must be positive for seat");
        }
        if (seat.getSeatNumber() == null || seat.getSeatNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Seat number cannot be null or empty");
        }
        if (seat.getPrice() <= 0) {
            throw new IllegalArgumentException("Seat price must be greater than 0");
        }
    }

    private void validateReservation(TicketReservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        if (reservation.getCustomerName() == null || reservation.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        if (reservation.getReservationDate() == null || reservation.getReservationDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Reservation date cannot be null or empty");
        }
        if (reservation.getEventId() <= 0) {
            throw new IllegalArgumentException("Event ID must be positive for reservation");
        }
        if (reservation.getSeatId() <= 0) {
            throw new IllegalArgumentException("Seat ID must be positive for reservation");
        }
    }
}
