package com.ticketreservation.app;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TicketAppTest {

    @Test
    void testConstructor() {
        assertThatCode(TicketApp::new).doesNotThrowAnyException();
    }

    @Test
    void testInitializeDatabaseSuccess() throws Exception {
        DataSource mockDs = mock(DataSource.class);
        Connection mockConn = mock(Connection.class);
        Statement mockStmt = mock(Statement.class);

        when(mockDs.getConnection()).thenReturn(mockConn);
        when(mockConn.createStatement()).thenReturn(mockStmt);

        TicketApp.initializeDatabase(mockDs);

        verify(mockDs).getConnection();
        verify(mockConn).createStatement();
        verify(mockStmt).execute(anyString());
    }

    @Test
    void testInitializeDatabaseHandlesSQLException() throws Exception {
        DataSource mockDs = mock(DataSource.class);
        when(mockDs.getConnection()).thenThrow(new SQLException("DB connection failed"));

        assertThatCode(() -> TicketApp.initializeDatabase(mockDs))
                .doesNotThrowAnyException();
    }

    @Test
    void testMainMethodExecution() {
        // Run main method with dummy properties to ensure graceful initialization
        System.setProperty("db.host", "localhost");
        System.setProperty("db.port", "3306");
        System.setProperty("db.name", "testdb");
        System.setProperty("db.user", "root");
        System.setProperty("db.password", "root");

        assertThatCode(() -> TicketApp.main(new String[0]))
                .doesNotThrowAnyException();
    }
}
