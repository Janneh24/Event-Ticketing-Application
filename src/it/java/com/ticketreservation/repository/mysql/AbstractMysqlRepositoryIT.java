package com.ticketreservation.repository.mysql;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.time.Duration;
import java.util.Collections;
import java.util.Scanner;

public abstract class AbstractMysqlRepositoryIT {

    public static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_ticketingdb")
            .withUsername("test")
            .withEnv("MYSQL_ALLOW_EMPTY_PASSWORD", "yes")
            .withTmpFs(Collections.singletonMap("/var/lib/mysql", "rw"))
            .withStartupTimeout(Duration.ofMinutes(5))
            .withConnectTimeoutSeconds(300);

    protected DataSource dataSource;

    @BeforeAll
    public static void startContainer() {
        if (!MYSQL_CONTAINER.isRunning()) {
            MYSQL_CONTAINER.start();
        }
    }

    @BeforeEach
    public void setUpDatabase() throws Exception {
        MysqlDataSource mysqlDs = new MysqlDataSource();
        mysqlDs.setURL(MYSQL_CONTAINER.getJdbcUrl());
        mysqlDs.setUser(MYSQL_CONTAINER.getUsername());
        mysqlDs.setPassword(MYSQL_CONTAINER.getPassword());
        this.dataSource = mysqlDs;

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Execute init.sql schema setup
            InputStream is = getClass().getClassLoader().getResourceAsStream("db/init.sql");
            if (is != null) {
                Scanner scanner = new Scanner(is).useDelimiter(";");
                while (scanner.hasNext()) {
                    String sql = scanner.next().trim();
                    if (!sql.isEmpty()) {
                        stmt.execute(sql);
                    }
                }
            }

            // Cleanup data between tests
            stmt.execute("DELETE FROM ticket_reservations");
            stmt.execute("DELETE FROM seats");
            stmt.execute("DELETE FROM events");
            stmt.execute("DELETE FROM users WHERE username != 'organizer'");
        }
    }
}
