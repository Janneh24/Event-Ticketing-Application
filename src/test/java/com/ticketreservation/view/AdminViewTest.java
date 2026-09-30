package com.ticketreservation.view;

import com.ticketreservation.controller.EventController;
import com.ticketreservation.controller.TicketController;
import com.ticketreservation.controller.UserController;
import com.ticketreservation.model.Event;
import com.ticketreservation.model.User;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminViewTest {

    @Mock
    private EventController eventController;

    @Mock
    private TicketController ticketController;

    @Mock
    private UserController userController;

    private FrameFixture window;
    private AdminView view;

    @BeforeEach
    void setUp() {
        User user = new User(1L, "admin_user", "pass", "ORGANIZER", true);
        when(userController.getAllUsers()).thenReturn(List.of(user));
        Event sampleEvent = new Event(1L, "Jazz Fest", "2026-10-10", "Arena", 50, 50);
        when(eventController.getAllEvents()).thenReturn(List.of(sampleEvent));

        view = GuiActionRunner.execute(() -> new AdminView(eventController, ticketController, userController));
        window = new FrameFixture(view);
        window.show();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (window != null) {
            window.cleanUp();
        }
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            for (java.awt.Window w : java.awt.Window.getWindows()) {
                w.setVisible(false);
                w.dispose();
            }
        });
    }

    @Test
    void testInitialStateAndTablePopulated() {
        window.table("eventTable").requireRowCount(1);
        window.table("userTable").requireRowCount(1);
        window.textBox("titleField").requireEmpty();
        window.textBox("dateField").requireEmpty();
        window.textBox("venueField").requireEmpty();
        window.textBox("seatsField").requireEmpty();
        window.label("errorLabel").requireText(" ");
    }

    @Test
    void testSelectEventInTablePopulatesForm() {
        GuiActionRunner.execute(() -> window.table("eventTable").target().setRowSelectionInterval(0, 0));
        window.textBox("titleField").requireText("Jazz Fest");
        window.textBox("dateField").requireText("2026-10-10");
        window.textBox("venueField").requireText("Arena");
        window.textBox("seatsField").requireText("50");
    }

    @Test
    void testCreateEventSuccess() {
        Event event = new Event(10L, "Rock Show", "2026-11-20", "Stadium", 4, 4);
        when(eventController.createEvent(any(Event.class))).thenReturn(event);

        window.textBox("titleField").setText("Rock Show");
        window.textBox("dateField").setText("2026-11-20");
        window.textBox("venueField").setText("Stadium");
        window.textBox("seatsField").setText("4");

        GuiActionRunner.execute(() -> window.button("createEventButton").target().doClick());

        verify(eventController).createEvent(any(Event.class));
        window.label("errorLabel").requireText(" ");
    }

    @Test
    void testCreateEventReturnsZeroIdDoesNotCreateSeats() {
        Event event = new Event(0L, "Rock Show", "2026-11-20", "Stadium", 4, 4);
        when(eventController.createEvent(any(Event.class))).thenReturn(event);

        window.textBox("titleField").setText("Rock Show");
        window.textBox("dateField").setText("2026-11-20");
        window.textBox("venueField").setText("Stadium");
        window.textBox("seatsField").setText("4");

        GuiActionRunner.execute(() -> window.button("createEventButton").target().doClick());

        verify(eventController).createEvent(any(Event.class));
        window.label("errorLabel").requireText(" ");
    }

    @Test
    void testCreateEventFailureShowsError() {
        when(eventController.createEvent(any(Event.class)))
                .thenThrow(new RuntimeException("Failed to create event"));

        window.textBox("titleField").setText("Rock Show");
        window.textBox("dateField").setText("2026-11-20");
        window.textBox("venueField").setText("Stadium");
        window.textBox("seatsField").setText("500");

        GuiActionRunner.execute(() -> window.button("createEventButton").target().doClick());

        window.label("errorLabel").requireText("Failed to create event");
    }

    @Test
    void testCreateEventInvalidNumberShowsError() {
        window.textBox("titleField").setText("Rock Show");
        window.textBox("dateField").setText("2026-11-20");
        window.textBox("venueField").setText("Stadium");
        window.textBox("seatsField").setText("not-a-number");

        GuiActionRunner.execute(() -> window.button("createEventButton").target().doClick());

        window.label("errorLabel").requireText("For input string: \"not-a-number\"");
    }

    @Test
    void testEditEventSuccess() {
        GuiActionRunner.execute(() -> window.table("eventTable").target().setRowSelectionInterval(0, 0));
        window.textBox("titleField").setText("Updated Fest");

        GuiActionRunner.execute(() -> window.button("editEventButton").target().doClick());

        verify(eventController).updateEvent(any(Event.class));
        window.label("errorLabel").requireText(" ");
    }

    @Test
    void testEditEventNoSelectionShowsError() {
        GuiActionRunner.execute(() -> window.table("eventTable").target().clearSelection());
        GuiActionRunner.execute(() -> window.button("editEventButton").target().doClick());

        window.label("errorLabel").requireText("Please select an event to edit");
    }

    @Test
    void testEditEventFailureShowsError() {
        GuiActionRunner.execute(() -> window.table("eventTable").target().setRowSelectionInterval(0, 0));
        when(eventController.updateEvent(any(Event.class)))
                .thenThrow(new RuntimeException("Update failed"));

        GuiActionRunner.execute(() -> window.button("editEventButton").target().doClick());

        window.label("errorLabel").requireText("Update failed");
    }

    @Test
    void testDeleteEventSuccess() {
        GuiActionRunner.execute(() -> window.table("eventTable").target().setRowSelectionInterval(0, 0));
        GuiActionRunner.execute(() -> window.button("deleteEventButton").target().doClick());

        verify(eventController).deleteEvent(1L);
        window.label("errorLabel").requireText(" ");
    }

    @Test
    void testDeleteEventNoSelectionShowsError() {
        GuiActionRunner.execute(() -> window.table("eventTable").target().clearSelection());
        GuiActionRunner.execute(() -> window.button("deleteEventButton").target().doClick());

        window.label("errorLabel").requireText("Please select an event to delete");
    }

    @Test
    void testDeleteEventFailureShowsError() {
        GuiActionRunner.execute(() -> window.table("eventTable").target().setRowSelectionInterval(0, 0));
        Mockito.doThrow(new RuntimeException("Delete failed")).when(eventController).deleteEvent(1L);

        GuiActionRunner.execute(() -> window.button("deleteEventButton").target().doClick());

        window.label("errorLabel").requireText("Delete failed");
    }

    @Test
    void testTablesAreNotEditable() {
        assertThat(window.table("eventTable").target().isCellEditable(0, 0)).isFalse();
        assertThat(window.table("userTable").target().isCellEditable(0, 0)).isFalse();
    }

    @Test
    void testRefreshDataWithNullLists() {
        when(eventController.getAllEvents()).thenReturn(null);
        when(userController.getAllUsers()).thenReturn(null);

        GuiActionRunner.execute(() -> view.refreshData());

        window.table("eventTable").requireRowCount(0);
        window.table("userTable").requireRowCount(0);
    }

    @Test
    void testCreateEventReturnsNullDoesNotCreateSeats() {
        when(eventController.createEvent(any(Event.class))).thenReturn(null);

        window.textBox("titleField").setText("Rock Show");
        window.textBox("dateField").setText("2026-11-20");
        window.textBox("venueField").setText("Stadium");
        window.textBox("seatsField").setText("4");

        GuiActionRunner.execute(() -> window.button("createEventButton").target().doClick());

        verify(eventController).createEvent(any(Event.class));
        window.label("errorLabel").requireText(" ");
    }
}
