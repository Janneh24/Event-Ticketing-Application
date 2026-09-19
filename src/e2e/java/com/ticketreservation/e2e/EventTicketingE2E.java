package com.ticketreservation.e2e;

import com.ticketreservation.controller.EventController;
import com.ticketreservation.controller.TicketController;
import com.ticketreservation.controller.UserController;
import com.ticketreservation.model.Event;
import com.ticketreservation.model.Seat;
import com.ticketreservation.model.User;
import com.ticketreservation.repository.mysql.AbstractMysqlRepositoryIT;
import com.ticketreservation.repository.mysql.MysqlEventRepository;
import com.ticketreservation.repository.mysql.MysqlReservationRepository;
import com.ticketreservation.repository.mysql.MysqlSeatRepository;
import com.ticketreservation.repository.mysql.MysqlUserRepository;
import com.ticketreservation.view.EventSwingView;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Dimension;

@GUITest
public class EventTicketingE2E extends AbstractMysqlRepositoryIT {

    private FrameFixture window;

    @BeforeEach
    void setUpE2E() {
        MysqlUserRepository userRepo = new MysqlUserRepository(dataSource);
        MysqlEventRepository eventRepo = new MysqlEventRepository(dataSource);
        MysqlSeatRepository seatRepo = new MysqlSeatRepository(dataSource);
        MysqlReservationRepository reservationRepo = new MysqlReservationRepository(dataSource);

        UserController userController = new UserController(userRepo);
        EventController eventController = new EventController(eventRepo);
        TicketController ticketController = new TicketController(reservationRepo, seatRepo, eventRepo);

        User user = userController.createUser(new User(0L, "e2e_user", "pass", "CUSTOMER", true));
        Event event = eventController.createEvent(new Event(0L, "E2E Concert", "2026-12-25", "Arena", 100, 100));
        ticketController.createSeat(new Seat(0L, event.getId(), "E2E-1", "VIP", 200.0, "AVAILABLE"));

        EventSwingView view = GuiActionRunner.execute(() -> {
            EventSwingView v = new EventSwingView(eventController, ticketController, user);
            v.setSize(new Dimension(700, 420));
            v.setLocation(0, 0);
            return v;
        });
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
    void testEndToEndReservationFlow() {
        window.table("eventTable").requireRowCount(1);
        window.table("seatTable").requireRowCount(1);

        window.textBox("customerNameField").setText("E2E Customer");
        GuiActionRunner.execute(() -> window.table("seatTable").target().setRowSelectionInterval(0, 0));
        GuiActionRunner.execute(() -> window.button("reserveButton").target().doClick());

        // Verify Seat status updated to RESERVED in table
        window.table("seatTable").cell(TableCell.row(0).column(5)).requireValue("RESERVED");

        // Cancel Reservation Flow
        GuiActionRunner.execute(() -> window.table("seatTable").target().setRowSelectionInterval(0, 0));
        GuiActionRunner.execute(() -> window.button("cancelButton").target().doClick());

        // Verify Seat status reset to AVAILABLE in table
        window.table("seatTable").cell(TableCell.row(0).column(5)).requireValue("AVAILABLE");
    }
}
