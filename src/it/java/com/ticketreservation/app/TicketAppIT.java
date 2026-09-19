package com.ticketreservation.app;

import com.ticketreservation.repository.mysql.AbstractMysqlRepositoryIT;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;

import static org.assertj.core.api.Assertions.assertThatCode;

class TicketAppIT extends AbstractMysqlRepositoryIT {

    @Test
    void testMainWithRealDatabase() throws Exception {
        System.setProperty("db.host", MYSQL_CONTAINER.getHost());
        System.setProperty("db.port", String.valueOf(MYSQL_CONTAINER.getFirstMappedPort()));
        System.setProperty("db.name", MYSQL_CONTAINER.getDatabaseName());
        System.setProperty("db.user", MYSQL_CONTAINER.getUsername());
        System.setProperty("db.password", MYSQL_CONTAINER.getPassword());

        assertThatCode(() -> TicketApp.main(new String[0]))
                .doesNotThrowAnyException();

        // Flush Event Dispatch Thread to ensure invokeLater executes completely
        SwingUtilities.invokeAndWait(() -> {
            // Wait for EDT queue
        });
    }
}
