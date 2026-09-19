package com.ticketreservation.repository;

import com.ticketreservation.model.Event;
import java.util.List;

public interface EventRepository {
    List<Event> findAll();
    Event findById(long id);
    Event save(Event event);
    Event update(Event event);
    void delete(long id);
}
