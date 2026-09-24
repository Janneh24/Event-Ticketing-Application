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

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
    void testReserveButtonFailureShowsError() {
        when(ticketController.reserveTicket(any(TicketReservation.class)))
                .thenThrow(new RuntimeException("Seat already booked"));

        window.textBox("customerNameField").setText("Alice");
        GuiActionRunner.execute(() -> window.table("seatTable").target().setRowSelectionInterval(0, 0));
        GuiActionRunner.execute(() -> window.button("reserveButton").target().doClick());

        window.label("errorLabel").requireText("Seat already booked");
    }

    @Test
    void testCancelButtonWithoutSelectingSeatShowsError() {
        GuiActionRunner.execute(() -> window.table("seatTable").target().clearSelection());
        GuiActionRunner.execute(() -> window.button("cancelButton").target().doClick());
        window.label("errorLabel").requireText("Please select a seat to cancel reservation");
    }

    @Test
    void testCancelButtonWhenNoActiveReservationFoundShowsError() {
        when(ticketController.getAllReservations()).thenReturn(Collections.emptyList());

        GuiActionRunner.execute(() -> window.table("seatTable").target().setRowSelectionInterval(0, 0));
        GuiActionRunner.execute(() -> window.button("cancelButton").target().doClick());

        window.label("errorLabel").requireText("No active reservation found for selected seat");
    }

    @Test
    void testCancelButtonSuccess() {
        TicketReservation res = new TicketReservation(1L, "2026-09-16", "Alice", 150.0, 1L, 10L, 50L);
        when(ticketController.getAllReservations()).thenReturn(List.of(res));

        GuiActionRunner.execute(() -> window.table("seatTable").target().setRowSelectionInterval(0, 0));
        GuiActionRunner.execute(() -> window.button("cancelButton").target().doClick());

        verify(ticketController).cancelReservation(1L);
        window.label("errorLabel").requireText(" ");
    }

    @Test
    void testCancelButtonFailureShowsError() {
        TicketReservation res = new TicketReservation(1L, "2026-09-16", "Alice", 150.0, 1L, 10L, 50L);
        when(ticketController.getAllReservations()).thenReturn(List.of(res));
        doThrow(new RuntimeException("Cancel failed")).when(ticketController).cancelReservation(1L);

        GuiActionRunner.execute(() -> window.table("seatTable").target().setRowSelectionInterval(0, 0));
        GuiActionRunner.execute(() -> window.button("cancelButton").target().doClick());

        window.label("errorLabel").requireText("Cancel failed");
    }

    @Test
    void testSelectEventInTableUpdatesEventCombo() {
        GuiActionRunner.execute(() -> window.table("eventTable").target().setRowSelectionInterval(0, 0));
        window.comboBox("eventCombo").requireSelection(0);
    }

    @Test
    void testExportPdfButtonWithoutSelectedEventShowsError() {
        GuiActionRunner.execute(() -> window.comboBox("eventCombo").target().setSelectedItem(null));
        GuiActionRunner.execute(() -> window.button("exportPdfButton").target().doClick());
        window.label("errorLabel").requireText("Please select an event to export PDF");
    }

    @Test
    void testTableCellsAreNotEditable() {
        assertThat(window.table("eventTable").target().isCellEditable(0, 0)).isFalse();
        assertThat(window.table("seatTable").target().isCellEditable(0, 0)).isFalse();
    }

    @Test
    void testReserveButtonWhenUserIsNull() {
        view.setCurrentUser(null);
        TicketReservation res = new TicketReservation(2L, "2026-09-16", "Bob", 150.0, null, 10L, 50L);
        when(ticketController.reserveTicket(any(TicketReservation.class))).thenReturn(res);

        window.textBox("customerNameField").setText("Bob");
        GuiActionRunner.execute(() -> window.table("seatTable").target().setRowSelectionInterval(0, 0));
        GuiActionRunner.execute(() -> window.button("reserveButton").target().doClick());

        verify(ticketController).reserveTicket(any(TicketReservation.class));
        window.label("errorLabel").requireText(" ");
    }

    @Test
    void testCancelButtonWhenReservationDoesNotMatchSeatOrEvent() {
        TicketReservation mismatchSeat = new TicketReservation(2L, "2026-09-16", "Bob", 100.0, 2L, 10L, 999L);
        TicketReservation mismatchEvent = new TicketReservation(3L, "2026-09-16", "Bob", 100.0, 2L, 999L, 50L);
        when(ticketController.getAllReservations()).thenReturn(List.of(mismatchSeat, mismatchEvent));

        GuiActionRunner.execute(() -> window.table("seatTable").target().setRowSelectionInterval(0, 0));
        GuiActionRunner.execute(() -> window.button("cancelButton").target().doClick());

        window.label("errorLabel").requireText("No active reservation found for selected seat");
    }

    @Test
    void testExportPdfButtonSuccessWithCustomFileChooser() throws Exception {
        java.io.File tempFile = java.io.File.createTempFile("test_export_", ".pdf");
        tempFile.deleteOnExit();

        javax.swing.JFileChooser mockChooser = org.mockito.Mockito.mock(javax.swing.JFileChooser.class);
        when(mockChooser.showSaveDialog(any())).thenReturn(javax.swing.JFileChooser.APPROVE_OPTION);
        when(mockChooser.getSelectedFile()).thenReturn(tempFile);

        view.setFileChooser(mockChooser);

        TicketReservation matchingRes = new TicketReservation(1L, "2026-09-16", "Alice", 150.0, 1L, 10L, 50L);
        TicketReservation nonMatchingRes = new TicketReservation(2L, "2026-09-16", "Bob", 50.0, 2L, 99L, 60L);
        when(ticketController.getAllReservations()).thenReturn(List.of(matchingRes, nonMatchingRes));

        GuiActionRunner.execute(() -> window.button("exportPdfButton").target().doClick());

        window.label("errorLabel").requireText(" ");
        assertThat(tempFile.length()).isGreaterThan(0L);
    }

    @Test
    void testExportPdfButtonCancelDialog() {
        javax.swing.JFileChooser mockChooser = org.mockito.Mockito.mock(javax.swing.JFileChooser.class);
        when(mockChooser.showSaveDialog(any())).thenReturn(javax.swing.JFileChooser.CANCEL_OPTION);

        view.setFileChooser(mockChooser);

        when(ticketController.getAllReservations()).thenReturn(Collections.emptyList());
        GuiActionRunner.execute(() -> window.button("exportPdfButton").target().doClick());

        window.label("errorLabel").requireText(" ");
    }

    @Test
    void testExportPdfToFileExceptionHandled() {
        Event event = new Event(10L, "Opera Show", "2026-10-15", "Florence Theatre", 100, 100);
        java.io.File invalidFile = new java.io.File("/non_existent_folder_xyz123/impossible_file.pdf");

        view.exportPdfToFile(event, List.of(), invalidFile);

        assertThat(window.label("errorLabel").target().getText()).contains("Failed to export PDF");
    }

    @Test
    void testEventWrapperToString() {
        assertThat(window.comboBox("eventCombo").target().getItemAt(0).toString())
                .isEqualTo("Opera Show (2026-10-15)");
    }

    @Test
    void testGetFileChooserDefaultAndCustom() {
        view.setFileChooser(null);
        assertThat(view.getFileChooser()).isNotNull();

        javax.swing.JFileChooser mockChooser = org.mockito.Mockito.mock(javax.swing.JFileChooser.class);
        view.setFileChooser(mockChooser);
        assertThat(view.getFileChooser()).isSameAs(mockChooser);
    }

    @Test
    void testSelectEventInTableEdgeCases() {
        // Ensure a row is selected first
        GuiActionRunner.execute(() -> window.table("eventTable").target().setRowSelectionInterval(0, 0));
        assertThat(window.table("eventTable").target().getSelectedRow()).isEqualTo(0);

        // Clear selection to fire event with row == -1
        GuiActionRunner.execute(() -> window.table("eventTable").target().clearSelection());
        assertThat(window.table("eventTable").target().getSelectedRow()).isEqualTo(-1);

        // Remove combo items and select row 0 from cleared state so row >= eventCombo.getItemCount()
        GuiActionRunner.execute(() -> {
            window.comboBox("eventCombo").target().removeAllItems();
            window.table("eventTable").target().setRowSelectionInterval(0, 0);
        });
        assertThat(window.table("eventTable").target().getSelectedRow()).isEqualTo(0);
    }
}

