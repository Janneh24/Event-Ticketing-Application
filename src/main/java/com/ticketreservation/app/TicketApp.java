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
import com.ticketreservation.view.AdminView;
import com.ticketreservation.view.EventSwingView;
import com.ticketreservation.view.LoginView;

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
            try {
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            showLogin(userController, eventController, ticketController);
        });
    }

    public static void showLogin(UserController userController, EventController eventController, TicketController ticketController) {
        LoginView loginView = new LoginView(userController);
        loginView.setTitle("Event Ticketing System - Login");

        javax.swing.JPanel hintPanel = new javax.swing.JPanel(new java.awt.GridLayout(2, 1, 2, 2));
        hintPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 4, 12));
        hintPanel.add(new javax.swing.JLabel("👑 Admin Login: username 'organizer', password 'organizer'"));
        hintPanel.add(new javax.swing.JLabel("👤 Customer Login: username 'customer', password 'customer'"));
        loginView.add(hintPanel, java.awt.BorderLayout.NORTH);
        loginView.pack();
        loginView.setLocationRelativeTo(null);
        loginView.setVisible(true);

        loginView.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                User user = loginView.getAuthenticatedUser();
                if (user != null) {
                    if ("ORGANIZER".equalsIgnoreCase(user.getRole())) {
                        openAdminPortal(user, userController, eventController, ticketController);
                    } else {
                        openCustomerPortal(user, userController, eventController, ticketController);
                    }
                }
            }
        });
    }

    private static void openAdminPortal(User adminUser, UserController userController, EventController eventController, TicketController ticketController) {
        AdminView adminView = new AdminView(eventController, ticketController, userController);
        adminView.setTitle("Organizer & Admin Dashboard - " + adminUser.getUsername());

        javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu navigationMenu = new javax.swing.JMenu("Navigation");
        javax.swing.JMenuItem customerViewItem = new javax.swing.JMenuItem("Open Customer Reservation Portal");
        customerViewItem.addActionListener(e -> openCustomerPortal(adminUser, userController, eventController, ticketController));
        navigationMenu.add(customerViewItem);

        javax.swing.JMenuItem logoutItem = new javax.swing.JMenuItem("Logout / Switch Account");
        logoutItem.addActionListener(e -> {
            adminView.dispose();
            showLogin(userController, eventController, ticketController);
        });
        navigationMenu.add(logoutItem);
        menuBar.add(navigationMenu);
        adminView.setJMenuBar(menuBar);

        adminView.setLocationRelativeTo(null);
        adminView.setVisible(true);
    }

    private static void openCustomerPortal(User user, UserController userController, EventController eventController, TicketController ticketController) {
        EventSwingView view = new EventSwingView(eventController, ticketController, user);
        currentView = view;
        view.setTitle("Customer Portal - Logged in as: " + user.getUsername() + " (" + user.getRole() + ")");

        javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu portalMenu = new javax.swing.JMenu("Account");
        if ("ORGANIZER".equalsIgnoreCase(user.getRole())) {
            javax.swing.JMenuItem adminItem = new javax.swing.JMenuItem("Return to Admin Dashboard");
            adminItem.addActionListener(e -> {
                view.dispose();
                openAdminPortal(user, userController, eventController, ticketController);
            });
            portalMenu.add(adminItem);
        }
        javax.swing.JMenuItem logoutItem = new javax.swing.JMenuItem("Logout / Switch Account");
        logoutItem.addActionListener(e -> {
            view.dispose();
            showLogin(userController, eventController, ticketController);
        });
        portalMenu.add(logoutItem);
        menuBar.add(portalMenu);
        view.setJMenuBar(menuBar);

        view.setLocationRelativeTo(null);
        view.setVisible(true);
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
