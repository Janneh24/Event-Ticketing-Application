package com.ticketreservation.repository.mysql;

import com.ticketreservation.model.Event;
import com.ticketreservation.model.Seat;
import com.ticketreservation.model.TicketReservation;
import com.ticketreservation.model.User;
import com.ticketreservation.repository.EventRepository;
import com.ticketreservation.repository.RepositoryException;
import com.ticketreservation.repository.ReservationRepository;
import com.ticketreservation.repository.SeatRepository;
import com.ticketreservation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MysqlReservationRepositoryIT extends AbstractMysqlRepositoryIT {

    private ReservationRepository reservationRepository;
    private EventRepository eventRepository;
    private SeatRepository seatRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        reservationRepository = new MysqlReservationRepository(dataSource);
        eventRepository = new MysqlEventRepository(dataSource);
        seatRepository = new MysqlSeatRepository(dataSource);
        userRepository = new MysqlUserRepository(dataSource);
    }

    @Test
    void testCreateReservationTransactionSuccess() {
        User user = userRepository.save(new User(0L, "dave", "pass", "CUSTOMER", true));
        Event event = eventRepository.save(new Event(0L, "Rock Show", "2026-10-25", "Stadium", 10, 10));
        Seat seat = seatRepository.save(new Seat(0L, event.getId(), "C-1", "REGULAR", 50.0, "AVAILABLE"));

        TicketReservation reservation = new TicketReservation(0L, "2026-09-16", "Dave", 50.0, user.getId(), event.getId(), seat.getId());

        // Execute Transaction
        TicketReservation created = reservationRepository.createReservationTransaction(reservation);

        assertThat(created.getId()).isGreaterThan(0L);

        // Verify Seat status updated to RESERVED
        Seat updatedSeat = seatRepository.findById(seat.getId());
        assertThat(updatedSeat.getStatus()).isEqualTo("RESERVED");

        // Verify Event available_seats decremented from 10 to 9
        Event updatedEvent = eventRepository.findById(event.getId());
        assertThat(updatedEvent.getAvailableSeats()).isEqualTo(9);

        // Verify Reservation record created
        TicketReservation found = reservationRepository.findById(created.getId());
        assertThat(found).isNotNull();
        assertThat(found.getCustomerName()).isEqualTo("Dave");
    }

    @Test
    void testCreateReservationTransactionWithoutUserId() {
        Event event = eventRepository.save(new Event(0L, "Comedy Night", "2026-11-15", "Club", 5, 5));
        Seat seat = seatRepository.save(new Seat(0L, event.getId(), "D-1", "VIP", 80.0, "AVAILABLE"));

        TicketReservation reservation = new TicketReservation(0L, "2026-09-16", "Guest", 80.0, null, event.getId(), seat.getId());

        TicketReservation created = reservationRepository.createReservationTransaction(reservation);
        assertThat(created.getId()).isGreaterThan(0L);

        TicketReservation found = reservationRepository.findById(created.getId());
        assertThat(found).isNotNull();
        assertThat(found.getUserId()).isNull();
        assertThat(found.getCustomerName()).isEqualTo("Guest");
    }

    @Test
    void testCreateReservationTransactionRollbackWhenSeatNotAvailable() {
        User user = userRepository.save(new User(0L, "eve", "pass", "CUSTOMER", true));
        Event event = eventRepository.save(new Event(0L, "Rock Show", "2026-10-25", "Stadium", 10, 10));
        Seat seat = seatRepository.save(new Seat(0L, event.getId(), "C-2", "REGULAR", 50.0, "RESERVED")); // Already RESERVED

        TicketReservation reservation = new TicketReservation(0L, "2026-09-16", "Eve", 50.0, user.getId(), event.getId(), seat.getId());

        // Expect Exception and Rollback
        assertThatThrownBy(() -> reservationRepository.createReservationTransaction(reservation))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Seat is not available");

        // Verify Event available_seats remained unchanged at 10
        Event unchangedEvent = eventRepository.findById(event.getId());
        assertThat(unchangedEvent.getAvailableSeats()).isEqualTo(10);

        // Verify No Reservation record inserted
        List<TicketReservation> reservations = reservationRepository.findByEventId(event.getId());
        assertThat(reservations).isEmpty();
    }

    @Test
    void testCreateReservationTransactionRollbackWhenEventHasNoAvailableSeats() {
        User user = userRepository.save(new User(0L, "frank", "pass", "CUSTOMER", true));
        // Event has 0 available seats
        Event event = eventRepository.save(new Event(0L, "Sold Out Show", "2026-10-30", "Hall", 5, 0));
        Seat seat = seatRepository.save(new Seat(0L, event.getId(), "E-1", "REGULAR", 40.0, "AVAILABLE"));

        TicketReservation reservation = new TicketReservation(0L, "2026-09-16", "Frank", 40.0, user.getId(), event.getId(), seat.getId());

        assertThatThrownBy(() -> reservationRepository.createReservationTransaction(reservation))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("No available seats left for event");
    }

    @Test
    void testFindAllFindByUserIdFindByEventIdAndDelete() {
        User user = userRepository.save(new User(0L, "grace", "pass", "CUSTOMER", true));
        Event event = eventRepository.save(new Event(0L, "Art Expo", "2026-12-10", "Gallery", 20, 20));
        Seat seat = seatRepository.save(new Seat(0L, event.getId(), "G-1", "STANDARD", 30.0, "AVAILABLE"));

        TicketReservation reservation = new TicketReservation(0L, "2026-09-16", "Grace", 30.0, user.getId(), event.getId(), seat.getId());
        TicketReservation created = reservationRepository.createReservationTransaction(reservation);

        // Test findAll
        List<TicketReservation> all = reservationRepository.findAll();
        assertThat(all).extracting(TicketReservation::getId).contains(created.getId());

        // Test findByUserId
        List<TicketReservation> byUser = reservationRepository.findByUserId(user.getId());
        assertThat(byUser).extracting(TicketReservation::getCustomerName).contains("Grace");

        // Test findByEventId
        List<TicketReservation> byEvent = reservationRepository.findByEventId(event.getId());
        assertThat(byEvent).extracting(TicketReservation::getCustomerName).contains("Grace");

        // Test delete
        reservationRepository.delete(created.getId());
        assertThat(reservationRepository.findById(created.getId())).isNull();
    }

    @Test
    void testFindByIdNotFoundReturnsNull() {
        assertThat(reservationRepository.findById(999999L)).isNull();
    }
}
