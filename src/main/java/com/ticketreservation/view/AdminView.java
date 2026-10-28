package com.ticketreservation.view;

import com.ticketreservation.controller.EventController;
import com.ticketreservation.controller.TicketController;
import com.ticketreservation.controller.UserController;
import com.ticketreservation.model.Event;
import com.ticketreservation.model.Seat;
import com.ticketreservation.model.TicketReservation;
import com.ticketreservation.model.User;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Locale;

public class AdminView extends JFrame {

    private static final long serialVersionUID = 1L;

    private final EventController eventController;
    private final TicketController ticketController;
    private final UserController userController;

    private JTable eventTable;
    private DefaultTableModel eventTableModel;
    private JTextField titleField;
    private JTextField dateField;
    private JTextField venueField;
    private JTextField seatsField;

    private JTable reservationTable;
    private DefaultTableModel reservationTableModel;
    private JLabel bookingDetailsLabel;

    private JTable userTable;
    private DefaultTableModel userTableModel;
    private JTextField usernameField;
    private JTextField passwordField;
    private JComboBox<String> roleCombo;

    private JLabel errorLabel;

    public AdminView(EventController eventController, TicketController ticketController, UserController userController) {
        this.eventController = eventController;
        this.ticketController = ticketController;
        this.userController = userController;

        initUI();
    }

    private void initUI() {
        setTitle("Event Ticket System - Organizer Dashboard");
        setSize(950, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        errorLabel = new JLabel(" ");
        errorLabel.setName("errorLabel");
        errorLabel.setForeground(Color.RED);
        add(errorLabel, BorderLayout.NORTH);

        JPanel mainCenterPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        // Section 1: Events
        JPanel eventSection = new JPanel(new BorderLayout(5, 5));
        eventSection.setBorder(BorderFactory.createTitledBorder("Events Management"));
        eventTableModel = new DefaultTableModel(new Object[]{"Event ID", "Title", "Date", "Venue", "Total Seats", "Available Seats"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        eventTable = new JTable(eventTableModel);
        eventTable.setName("eventTable");
        eventTable.getSelectionModel().addListSelectionListener(e -> {
            int row = eventTable.getSelectedRow();
            if (row >= 0) {
                titleField.setText(String.valueOf(eventTableModel.getValueAt(row, 1)));
                dateField.setText(String.valueOf(eventTableModel.getValueAt(row, 2)));
                venueField.setText(String.valueOf(eventTableModel.getValueAt(row, 3)));
                seatsField.setText(String.valueOf(eventTableModel.getValueAt(row, 4)));
            }
        });
        eventSection.add(new JScrollPane(eventTable), BorderLayout.CENTER);

        JPanel eventControls = new JPanel(new BorderLayout(5, 5));
        JPanel eventForm = new JPanel(new GridLayout(2, 4, 5, 5));
        eventForm.add(new JLabel("Title:"));
        titleField = new JTextField();
        titleField.setName("titleField");
        eventForm.add(titleField);

        eventForm.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        dateField.setName("dateField");
        eventForm.add(dateField);

        eventForm.add(new JLabel("Venue:"));
        venueField = new JTextField();
        venueField.setName("venueField");
        eventForm.add(venueField);

        eventForm.add(new JLabel("Total Seats:"));
        seatsField = new JTextField();
        seatsField.setName("seatsField");
        eventForm.add(seatsField);
        eventControls.add(eventForm, BorderLayout.CENTER);

        JPanel eventButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 3));
        JButton createEventButton = new JButton("Create Event");
        createEventButton.setName("createEventButton");
        createEventButton.addActionListener(e -> createEventAction());
        eventButtons.add(createEventButton);

        JButton editEventButton = new JButton("Update Event");
        editEventButton.setName("editEventButton");
        editEventButton.addActionListener(e -> editEventAction());
        eventButtons.add(editEventButton);

        JButton deleteEventButton = new JButton("Delete Event");
        deleteEventButton.setName("deleteEventButton");
        deleteEventButton.addActionListener(e -> deleteEventAction());
        eventButtons.add(deleteEventButton);
        eventControls.add(eventButtons, BorderLayout.SOUTH);
        eventSection.add(eventControls, BorderLayout.SOUTH);
        mainCenterPanel.add(eventSection);

        // Section 2: Booked Tickets / Reservations
        JPanel bookingSection = new JPanel(new BorderLayout(5, 5));
        bookingSection.setBorder(BorderFactory.createTitledBorder("Booked Tickets & Reservations"));
        reservationTableModel = new DefaultTableModel(new Object[]{"Res ID", "Customer Name", "Event ID", "Seat ID", "Amount (€)", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationTable = new JTable(reservationTableModel);
        reservationTable.setName("reservationTable");
        reservationTable.getSelectionModel().addListSelectionListener(e -> {
            int row = reservationTable.getSelectedRow();
            if (row >= 0) {
                bookingDetailsLabel.setText(String.format(
                        "Customer: %s | Date: %s | Amount: €%s | Res ID: %s | Event ID: %s | Seat ID: %s",
                        reservationTableModel.getValueAt(row, 1),
                        reservationTableModel.getValueAt(row, 5),
                        reservationTableModel.getValueAt(row, 4),
                        reservationTableModel.getValueAt(row, 0),
                        reservationTableModel.getValueAt(row, 2),
                        reservationTableModel.getValueAt(row, 3)
                ));
            }
        });
        bookingSection.add(new JScrollPane(reservationTable), BorderLayout.CENTER);

        JPanel bookingDetailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        bookingDetailsLabel = new JLabel("Select a reserved ticket to view booking details.");
        bookingDetailsLabel.setName("bookingDetailsLabel");
        bookingDetailsPanel.add(bookingDetailsLabel);
        bookingSection.add(bookingDetailsPanel, BorderLayout.SOUTH);
        mainCenterPanel.add(bookingSection);

        // Section 3: User Accounts
        JPanel userSection = new JPanel(new BorderLayout(5, 5));
        userSection.setBorder(BorderFactory.createTitledBorder("User Management"));
        userTableModel = new DefaultTableModel(new Object[]{"User ID", "Username", "Role", "Enabled"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userTableModel);
        userTable.setName("userTable");
        userSection.add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel userControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 3));
        userControls.add(new JLabel("Username:"));
        usernameField = new JTextField(10);
        usernameField.setName("usernameField");
        userControls.add(usernameField);

        userControls.add(new JLabel("Password:"));
        passwordField = new JTextField(10);
        passwordField.setName("passwordField");
        userControls.add(passwordField);

        userControls.add(new JLabel("Role:"));
        roleCombo = new JComboBox<>(new String[]{"ORGANIZER", "CUSTOMER"});
        roleCombo.setName("roleCombo");
        userControls.add(roleCombo);

        JButton createUserButton = new JButton("Add User");
        createUserButton.setName("createUserButton");
        createUserButton.addActionListener(e -> createUserAction());
        userControls.add(createUserButton);
        userSection.add(userControls, BorderLayout.SOUTH);
        mainCenterPanel.add(userSection);

        add(mainCenterPanel, BorderLayout.CENTER);

        refreshData();
    }

    public void refreshData() {
        eventTableModel.setRowCount(0);
        List<Event> events = eventController.getAllEvents();
        if (events != null) {
            for (Event event : events) {
                eventTableModel.addRow(new Object[]{
                        event.getId(),
                        event.getTitle(),
                        event.getEventDate(),
                        event.getVenueName(),
                        event.getTotalSeats(),
                        event.getAvailableSeats()
                });
            }
        }

        userTableModel.setRowCount(0);
        List<User> users = userController.getAllUsers();
        if (users != null) {
            for (User user : users) {
                userTableModel.addRow(new Object[]{
                        user.getId(),
                        user.getUsername(),
                        user.getRole(),
                        user.isEnabled()
                });
            }
        }

        reservationTableModel.setRowCount(0);
        List<TicketReservation> reservations = ticketController.getAllReservations();
        if (reservations != null) {
            for (TicketReservation res : reservations) {
                reservationTableModel.addRow(new Object[]{
                        res.getId(),
                        res.getCustomerName(),
                        res.getEventId(),
                        res.getSeatId(),
                        String.format(Locale.US, "%.2f", res.getTotalAmount()),
                        res.getReservationDate()
                });
            }
        }
    }

    private void createEventAction() {
        errorLabel.setText(" ");
        try {
            String title = titleField.getText().trim();
            String date = dateField.getText().trim();
            String venue = venueField.getText().trim();
            int seats = Integer.parseInt(seatsField.getText().trim());

            Event event = new Event(0L, title, date, venue, seats, seats);
            Event created = eventController.createEvent(event);
            if (created != null && created.getId() > 0) {
                int numSeats = Math.min(seats, 50);
                for (int i = 1; i <= numSeats; i++) {
                    String section = (i <= Math.max(1, numSeats / 4)) ? "VIP" : "REGULAR";
                    double price = (i <= Math.max(1, numSeats / 4)) ? 100.0 : 50.0;
                    ticketController.createSeat(new Seat(0L, created.getId(), "Seat-" + i, section, price, "AVAILABLE"));
                }
            }
            refreshData();
        } catch (RuntimeException ex) {
            errorLabel.setText(ex.getMessage());
        }
    }

    private void editEventAction() {
        errorLabel.setText(" ");
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            errorLabel.setText("Please select an event to edit");
            return;
        }
        try {
            long eventId = ((Number) eventTableModel.getValueAt(selectedRow, 0)).longValue();
            String title = titleField.getText().trim();
            String date = dateField.getText().trim();
            String venue = venueField.getText().trim();
            int seats = Integer.parseInt(seatsField.getText().trim());

            Event updated = new Event(eventId, title, date, venue, seats, seats);
            eventController.updateEvent(updated);
            refreshData();
        } catch (RuntimeException ex) {
            errorLabel.setText(ex.getMessage());
        }
    }

    private void deleteEventAction() {
        errorLabel.setText(" ");
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            errorLabel.setText("Please select an event to delete");
            return;
        }
        try {
            long eventId = ((Number) eventTableModel.getValueAt(selectedRow, 0)).longValue();
            eventController.deleteEvent(eventId);
            refreshData();
        } catch (RuntimeException ex) {
            errorLabel.setText(ex.getMessage());
        }
    }

    private void createUserAction() {
        errorLabel.setText(" ");
        try {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String role = String.valueOf(roleCombo.getSelectedItem());

            User user = new User(0L, username, password, role, true);
            userController.createUser(user);
            usernameField.setText("");
            passwordField.setText("");
            refreshData();
        } catch (RuntimeException ex) {
            errorLabel.setText(ex.getMessage());
        }
    }
}
