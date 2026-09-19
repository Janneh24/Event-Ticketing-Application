package com.ticketreservation.view;

import com.ticketreservation.controller.EventController;
import com.ticketreservation.controller.TicketController;
import com.ticketreservation.model.Event;
import com.ticketreservation.model.Seat;
import com.ticketreservation.model.TicketReservation;
import com.ticketreservation.model.User;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventSwingViewTest {

    @Mock
    private EventController eventController;

    @Mock
    private TicketController ticketController;

    private FrameFixture window;
    private EventSwingView view;

    @BeforeEach
    void setUp() {
        User user = new User(1L, "alice", "pass", "CUSTOMER", true);
        Event event = new Event(10L, "Opera Show", "2026-10-15", "Florence Theatre", 100, 100);
        Seat seat = new Seat(50L, 10L, "A-1", "VIP", 150.0, "AVAILABLE");

        when(eventController.getAllEvents()).thenReturn(List.of(event));
        when(ticketController.getSeatsForEvent(10L)).thenReturn(List.of(seat));

        view = GuiActionRunner.execute(() -> new EventSwingView(eventController, ticketController, user));
        window = new FrameFixture(view);
        window.show();
    }

    @AfterEach
    void tearDown() {
        if (window != null) {
            window.cleanUp();
        }
    }

    @Test
    void testInitialStateAndTablePopulated() {
        window.table("eventTable").requireRowCount(1);
        window.table("seatTable").requireRowCount(1);
        window.textBox("customerNameField").requireEmpty();
        window.label("errorLabel").requireText(" ");
    }

    @Test
    void testReserveButtonWithoutSelectingSeatShowsError() {
        GuiActionRunner.execute(() -> window.table("seatTable").target().clearSelection());
        window.textBox("customerNameField").setText("Alice");
        GuiActionRunner.execute(() -> window.button("reserveButton").target().doClick());
        window.label("errorLabel").requireText("Please select a seat from the table");
    }

    @Test
    void testReserveButtonWithoutCustomerNameShowsError() {
        GuiActionRunner.execute(() -> {
            window.table("seatTable").target().setRowSelectionInterval(0, 0);
            window.textBox("customerNameField").target().setText("");
        });
        GuiActionRunner.execute(() -> window.button("reserveButton").target().doClick());
        window.label("errorLabel").requireText("Customer name cannot be empty");
    }

    @Test
    void testReserveButtonSuccess() {
        TicketReservation reservation = new TicketReservation(1L, "2026-09-16", "Alice", 150.0, 1L, 10L, 50L);
        when(ticketController.reserveTicket(any(TicketReservation.class))).thenReturn(reservation);

        window.textBox("customerNameField").setText("Alice");
        GuiActionRunner.execute(() -> window.table("seatTable").target().setRowSelectionInterval(0, 0));
        GuiActionRunner.execute(() -> window.button("reserveButton").target().doClick());

        verify(ticketController).reserveTicket(any(TicketReservation.class));
        window.label("errorLabel").requireText(" ");
    }

    @Test
    void testCancelButtonWithoutSelectingSeatShowsError() {
        GuiActionRunner.execute(() -> window.table("seatTable").target().clearSelection());
        GuiActionRunner.execute(() -> window.button("cancelButton").target().doClick());
        window.label("errorLabel").requireText("Please select a seat to cancel reservation");
    }
}
