package com.ticketreservation.app;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.ticketreservation.controller.EventController;
import com.ticketreservation.controller.TicketController;
import com.ticketreservation.controller.UserController;
import com.ticketreservation.model.User;
import com.ticketreservation.repository.mysql.MysqlEventRepository;
import com.ticketreservation.repository.mysql.MysqlReservationRepository;
import com.ticketreservation.repository.mysql.MysqlSeatRepository;
import com.ticketreservation.repository.mysql.MysqlUserRepository;
import com.ticketreservation.view.EventSwingView;

import javax.sql.DataSource;
import javax.swing.SwingUtilities;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketApp {

    private static final Logger LOGGER = Logger.getLogger(TicketApp.class.getName());

    private static EventSwingView currentView;

    public static EventSwingView getCurrentView() {
        return currentView;
    }

    public static void setCurrentView(EventSwingView view) {
        currentView = view;
    }

    public static void main(String[] args) {
        String host = System.getProperty("db.host", "localhost");
        int port = Integer.parseInt(System.getProperty("db.port", "3306"));
        String database = System.getProperty("db.name", "ticketingdb");
        String user = System.getProperty("db.user", "root");
        String password = System.getProperty("db.password", "root");

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName(host);
        dataSource.setPort(port);
        dataSource.setDatabaseName(database);
        dataSource.setUser(user);
        dataSource.setPassword(password);

        initializeDatabase(dataSource);

        MysqlUserRepository userRepo = new MysqlUserRepository(dataSource);
        MysqlEventRepository eventRepo = new MysqlEventRepository(dataSource);
        MysqlSeatRepository seatRepo = new MysqlSeatRepository(dataSource);
        MysqlReservationRepository reservationRepo = new MysqlReservationRepository(dataSource);

        UserController userController = new UserController(userRepo);
        EventController eventController = new EventController(eventRepo);
        TicketController ticketController = new TicketController(reservationRepo, seatRepo, eventRepo);

        SwingUtilities.invokeLater(() -> {
            User defaultUser = userController.getUserByUsername("organizer");
            currentView = new EventSwingView(eventController, ticketController, defaultUser);
            currentView.setVisible(true);
        });
    }

    public static void initializeDatabase(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            InputStream is = TicketApp.class.getClassLoader().getResourceAsStream("db/init.sql");
            if (is != null) {
                Scanner scanner = new Scanner(is).useDelimiter(";");
                while (scanner.hasNext()) {
                    String sql = scanner.next().trim();
                    if (!sql.isEmpty()) {
                        stmt.execute(sql);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Database auto-initialization skipped or failed", e);
        }
    }
}
