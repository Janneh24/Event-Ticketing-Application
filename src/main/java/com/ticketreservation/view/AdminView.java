package com.ticketreservation.view;

import com.ticketreservation.controller.EventController;
import com.ticketreservation.controller.TicketController;
import com.ticketreservation.controller.UserController;
import com.ticketreservation.model.Event;
import com.ticketreservation.model.Seat;
import com.ticketreservation.model.User;

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

public class AdminView extends JFrame {

    private static final long serialVersionUID = 1L;

    private final EventController eventController;
    private final TicketController ticketController;
    private final UserController userController;

    private JTable userTable;
    private DefaultTableModel userTableModel;

    private JTable eventTable;
    private DefaultTableModel eventTableModel;

    private JTextField titleField;
    private JTextField dateField;
    private JTextField venueField;
    private JTextField seatsField;
    private JButton createEventButton;
    private JButton editEventButton;
    private JButton deleteEventButton;
    private JLabel errorLabel;

    public AdminView(EventController eventController, TicketController ticketController, UserController userController) {
        this.eventController = eventController;
        this.ticketController = ticketController;
        this.userController = userController;

        initUI();
    }

    private void initUI() {
        setTitle("Event Ticket System - Organizer Dashboard");
        setSize(850, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        errorLabel = new JLabel(" ");
        errorLabel.setName("errorLabel");
        errorLabel.setForeground(Color.RED);
        add(errorLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        // Event Table
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
        centerPanel.add(new JScrollPane(eventTable));

        // User Table
        userTableModel = new DefaultTableModel(new Object[]{"User ID", "Username", "Role", "Enabled"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userTableModel);
        userTable.setName("userTable");
        centerPanel.add(new JScrollPane(userTable));

        add(centerPanel, BorderLayout.CENTER);

        // Form & Button Panel
        JPanel southPanel = new JPanel(new BorderLayout(5, 5));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        titleField.setName("titleField");
        formPanel.add(titleField);

        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        dateField.setName("dateField");
        formPanel.add(dateField);

        formPanel.add(new JLabel("Venue:"));
        venueField = new JTextField();
        venueField.setName("venueField");
        formPanel.add(venueField);

        formPanel.add(new JLabel("Total Seats:"));
        seatsField = new JTextField();
        seatsField.setName("seatsField");
        formPanel.add(seatsField);
        southPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        createEventButton = new JButton("Create Event");
        createEventButton.setName("createEventButton");
        createEventButton.addActionListener(e -> createEventAction());
        buttonPanel.add(createEventButton);

        editEventButton = new JButton("Update Event");
        editEventButton.setName("editEventButton");
        editEventButton.addActionListener(e -> editEventAction());
        buttonPanel.add(editEventButton);

        deleteEventButton = new JButton("Delete Event");
        deleteEventButton.setName("deleteEventButton");
        deleteEventButton.addActionListener(e -> deleteEventAction());
        buttonPanel.add(deleteEventButton);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

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
}
