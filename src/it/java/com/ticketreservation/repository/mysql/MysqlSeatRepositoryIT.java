package com.ticketreservation.repository.mysql;

import com.ticketreservation.model.Event;
import com.ticketreservation.model.Seat;
import com.ticketreservation.repository.EventRepository;
import com.ticketreservation.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MysqlSeatRepositoryIT extends AbstractMysqlRepositoryIT {

    private SeatRepository seatRepository;
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        seatRepository = new MysqlSeatRepository(dataSource);
        eventRepository = new MysqlEventRepository(dataSource);
    }

    @Test
    void testSaveFindByEventIdUpdateAndDelete() {
        Event event = eventRepository.save(new Event(0L, "Ballet Show", "2026-12-01", "Theatre", 50, 50));
        Seat seat1 = seatRepository.save(new Seat(0L, event.getId(), "A-1", "VIP", 120.0, "AVAILABLE"));
        Seat seat2 = seatRepository.save(new Seat(0L, event.getId(), "A-2", "VIP", 120.0, "AVAILABLE"));

        assertThat(seat1.getId()).isGreaterThan(0L);

        List<Seat> seats = seatRepository.findByEventId(event.getId());
        assertThat(seats).extracting(Seat::getSeatNumber).containsExactlyInAnyOrder("A-1", "A-2");

        seat1.setStatus("RESERVED");
        seatRepository.update(seat1);

        Seat updated = seatRepository.findById(seat1.getId());
        assertThat(updated.getStatus()).isEqualTo("RESERVED");

        seatRepository.delete(seat2.getId());
        assertThat(seatRepository.findById(seat2.getId())).isNull();
    }

    @Test
    void testFindAll() {
        Event event = eventRepository.save(new Event(0L, "Opera", "2026-12-15", "Hall", 30, 30));
        Seat seat = seatRepository.save(new Seat(0L, event.getId(), "B-1", "VIP", 150.0, "AVAILABLE"));

        List<Seat> allSeats = seatRepository.findAll();
        assertThat(allSeats).extracting(Seat::getId).contains(seat.getId());
    }

    @Test
    void testFindByIdNotFoundReturnsNull() {
        assertThat(seatRepository.findById(999999L)).isNull();
    }
}
