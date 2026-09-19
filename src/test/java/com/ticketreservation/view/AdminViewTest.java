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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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

        view = GuiActionRunner.execute(() -> new AdminView(eventController, ticketController, userController));
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
        window.table("userTable").requireRowCount(1);
        window.textBox("titleField").requireEmpty();
        window.textBox("dateField").requireEmpty();
        window.textBox("venueField").requireEmpty();
        window.textBox("seatsField").requireEmpty();
        window.label("errorLabel").requireText(" ");
    }

    @Test
    void testCreateEventSuccess() {
        Event event = new Event(10L, "Rock Show", "2026-11-20", "Stadium", 500, 500);
        when(eventController.createEvent(any(Event.class))).thenReturn(event);

        window.textBox("titleField").setText("Rock Show");
        window.textBox("dateField").setText("2026-11-20");
        window.textBox("venueField").setText("Stadium");
        window.textBox("seatsField").setText("500");

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
}
