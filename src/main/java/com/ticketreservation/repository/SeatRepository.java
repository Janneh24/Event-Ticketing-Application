package com.ticketreservation.repository;

import com.ticketreservation.model.Seat;
import java.util.List;

public interface SeatRepository {
    List<Seat> findAll();
    List<Seat> findByEventId(long eventId);
    Seat findById(long id);
    Seat save(Seat seat);
    Seat update(Seat seat);
    void delete(long id);
}
