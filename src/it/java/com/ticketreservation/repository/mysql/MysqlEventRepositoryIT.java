package com.ticketreservation.repository.mysql;

import com.ticketreservation.model.Event;
import com.ticketreservation.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MysqlEventRepositoryIT extends AbstractMysqlRepositoryIT {

    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        eventRepository = new MysqlEventRepository(dataSource);
    }

    @Test
    void testSaveFindByIdFindAllUpdateAndDelete() {
        Event event = new Event(0L, "Jazz Festival", "2026-11-20", "Main Hall", 200, 200);
        Event saved = eventRepository.save(event);

        assertThat(saved.getId()).isGreaterThan(0L);

        Event found = eventRepository.findById(saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Jazz Festival");

        List<Event> events = eventRepository.findAll();
        assertThat(events).extracting(Event::getTitle).contains("Jazz Festival");

        saved.setAvailableSeats(195);
        eventRepository.update(saved);

        Event updated = eventRepository.findById(saved.getId());
        assertThat(updated.getAvailableSeats()).isEqualTo(195);

        eventRepository.delete(saved.getId());
        assertThat(eventRepository.findById(saved.getId())).isNull();
    }

    @Test
    void testFindByIdNotFoundReturnsNull() {
        assertThat(eventRepository.findById(999999L)).isNull();
    }
}
