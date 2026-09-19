package com.ticketreservation.repository;

import com.ticketreservation.model.TicketReservation;
import java.util.List;

public interface ReservationRepository {
    List<TicketReservation> findAll();
    TicketReservation findById(long id);
    List<TicketReservation> findByUserId(long userId);
    List<TicketReservation> findByEventId(long eventId);
    
    /**
     * Executes an atomic MySQL transaction:
     * 1. Verifies seat status is AVAILABLE
     * 2. Updates seat status to RESERVED
     * 3. Deducts 1 from available_seats in the events table
     * 4. Inserts the ticket_reservation record
     * Handles setAutoCommit(false), commit(), and rollback() on failure/conflict.
     */
    TicketReservation createReservationTransaction(TicketReservation reservation);
    
    void delete(long id);
}
