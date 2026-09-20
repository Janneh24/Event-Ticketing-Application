package com.ticketreservation.app;

import com.ticketreservation.repository.mysql.AbstractMysqlRepositoryIT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.awt.Window;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class TicketAppIT extends AbstractMysqlRepositoryIT {

    @AfterEach
    void tearDown() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            if (TicketApp.getCurrentView() != null) {
                TicketApp.getCurrentView().setVisible(false);
                TicketApp.getCurrentView().dispose();
                TicketApp.setCurrentView(null);
            }
            for (Window w : Window.getWindows()) {
                w.setVisible(false);
                w.dispose();
            }
        });
    }

    @Test
    void testMainWithRealDatabase() throws Exception {
        System.setProperty("db.host", MYSQL_CONTAINER.getHost());
        System.setProperty("db.port", String.valueOf(MYSQL_CONTAINER.getFirstMappedPort()));
        System.setProperty("db.name", MYSQL_CONTAINER.getDatabaseName());
        System.setProperty("db.user", MYSQL_CONTAINER.getUsername());
        System.setProperty("db.password", MYSQL_CONTAINER.getPassword());

        assertThatCode(() -> TicketApp.main(new String[0]))
                .doesNotThrowAnyException();

        // Wait up to 5 seconds for currentView to be initialized on EDT
        for (int i = 0; i < 50; i++) {
            if (TicketApp.getCurrentView() != null) {
                break;
            }
            Thread.sleep(100);
        }

        assertThat(TicketApp.getCurrentView()).isNotNull();

        // Immediately dispose the window so AWT EventQueue thread terminates cleanly
        SwingUtilities.invokeAndWait(() -> {
            if (TicketApp.getCurrentView() != null) {
                TicketApp.getCurrentView().setVisible(false);
                TicketApp.getCurrentView().dispose();
                TicketApp.setCurrentView(null);
            }
            for (Window w : Window.getWindows()) {
                w.setVisible(false);
                w.dispose();
            }
        });
    }
}
