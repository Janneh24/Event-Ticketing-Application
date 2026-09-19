package com.ticketreservation.controller;

import com.ticketreservation.model.Event;
import com.ticketreservation.repository.EventRepository;

import java.util.List;

public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(long id) {
        Event event = eventRepository.findById(id);
        if (event == null) {
            throw new IllegalArgumentException("Event not found with id: " + id);
        }
        return event;
    }

    public Event createEvent(Event event) {
        validateEvent(event);
        return eventRepository.save(event);
    }

    public Event updateEvent(Event event) {
        if (event.getId() <= 0) {
            throw new IllegalArgumentException("Event ID must be positive for update");
        }
        validateEvent(event);
        return eventRepository.update(event);
    }

    public void deleteEvent(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Event ID must be positive for deletion");
        }
        eventRepository.delete(id);
    }

    private void validateEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Event title cannot be null or empty");
        }
        if (event.getEventDate() == null || event.getEventDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Event date cannot be null or empty");
        }
        if (event.getVenueName() == null || event.getVenueName().trim().isEmpty()) {
            throw new IllegalArgumentException("Venue name cannot be null or empty");
        }
        if (event.getTotalSeats() <= 0) {
            throw new IllegalArgumentException("Total seats must be greater than 0");
        }
        if (event.getAvailableSeats() < 0 || event.getAvailableSeats() > event.getTotalSeats()) {
            throw new IllegalArgumentException("Available seats must be between 0 and total seats");
        }
    }
}
