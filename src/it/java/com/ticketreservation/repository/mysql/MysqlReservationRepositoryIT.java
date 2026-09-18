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
}
