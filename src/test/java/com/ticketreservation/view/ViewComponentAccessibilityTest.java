package com.ticketreservation.view;

import com.ticketreservation.controller.EventController;
import com.ticketreservation.controller.SeatController;
import com.ticketreservation.controller.UserController;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class ViewComponentAccessibilityTest extends AssertJSwingJUnitTestCase {

    private UserController userController;
    private EventController eventController;
    private SeatController seatController;

    @Override
    protected void onSetUp() {
        userController = Mockito.mock(UserController.class);
        eventController = Mockito.mock(EventController.class);
        seatController = Mockito.mock(SeatController.class);
    }

    @Test
    @DisplayName("Verify LoginSwingView component names for UI test accessibility")
    void testLoginSwingViewComponentNames() {
        LoginSwingView view = GuiActionRunner.execute(() -> new LoginSwingView(userController));
        FrameFixture window = new FrameFixture(robot(), view);

        assertThat(window.textBox("usernameField")).isNotNull();
        assertThat(window.textBox("passwordField")).isNotNull();
        assertThat(window.button("loginButton")).isNotNull();
        assertThat(window.label("errorMessageLabel")).isNotNull();

        window.cleanUp();
    }

    @Test
    @DisplayName("Verify EventSwingView component names for UI test accessibility")
    void testEventSwingViewComponentNames() {
        EventSwingView view = GuiActionRunner.execute(() -> new EventSwingView(eventController, seatController, "alice"));
        FrameFixture window = new FrameFixture(robot(), view);

        assertThat(window.comboBox("eventComboBox")).isNotNull();
        assertThat(window.table("seatTable")).isNotNull();
        assertThat(window.button("bookSeatButton")).isNotNull();
        assertThat(window.button("cancelReservationButton")).isNotNull();
        assertThat(window.button("generateReportButton")).isNotNull();
        assertThat(window.label("errorMessageLabel")).isNotNull();
        assertThat(window.label("totalAmountLabel")).isNotNull();

        window.cleanUp();
    }

    @Test
    @DisplayName("Verify AdminView component names for UI test accessibility")
    void testAdminViewComponentNames() {
        AdminView view = GuiActionRunner.execute(() -> new AdminView(eventController, seatController, userController));
        FrameFixture window = new FrameFixture(robot(), view);

        assertThat(window.table("eventsTable")).isNotNull();
        assertThat(window.table("reservationTable")).isNotNull();
        assertThat(window.table("userTable")).isNotNull();

        assertThat(window.textBox("titleField")).isNotNull();
        assertThat(window.textBox("dateField")).isNotNull();
        assertThat(window.textBox("locationField")).isNotNull();
        assertThat(window.textBox("seatsField")).isNotNull();
        assertThat(window.textBox("priceField")).isNotNull();

        assertThat(window.button("createEventButton")).isNotNull();
        assertThat(window.button("updateEventButton")).isNotNull();
        assertThat(window.button("deleteEventButton")).isNotNull();

        assertThat(window.textBox("usernameField")).isNotNull();
        assertThat(window.textBox("passwordField")).isNotNull();
        assertThat(window.comboBox("roleCombo")).isNotNull();
        assertThat(window.button("createUserButton")).isNotNull();

        assertThat(window.label("bookingDetailsLabel")).isNotNull();
        assertThat(window.label("errorMessageLabel")).isNotNull();

        window.cleanUp();
    }
}
