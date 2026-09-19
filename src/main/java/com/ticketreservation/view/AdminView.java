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

    private JTextField titleField;
    private JTextField dateField;
    private JTextField venueField;
    private JTextField seatsField;
    private JButton createEventButton;
    private JLabel errorLabel;

    public AdminView(EventController eventController, TicketController ticketController, UserController userController) {
        this.eventController = eventController;
        this.ticketController = ticketController;
        this.userController = userController;

        initUI();
    }

    private void initUI() {
        setTitle("Event Ticket System - Organizer Dashboard");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        errorLabel = new JLabel(" ");
        errorLabel.setName("errorLabel");
        errorLabel.setForeground(Color.RED);
        add(errorLabel, BorderLayout.NORTH);

        userTableModel = new DefaultTableModel(new Object[]{"User ID", "Username", "Role", "Enabled"}, 0);
        userTable = new JTable(userTableModel);
        userTable.setName("userTable");
        add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
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

        createEventButton = new JButton("Create Event");
        createEventButton.setName("createEventButton");
        createEventButton.addActionListener(e -> createEventAction());
        formPanel.add(createEventButton);

        add(formPanel, BorderLayout.SOUTH);

        refreshData();
    }

    private void refreshData() {
        userTableModel.setRowCount(0);
        List<User> users = userController.getAllUsers();
        for (User user : users) {
            userTableModel.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getRole(),
                    user.isEnabled()
            });
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
            eventController.createEvent(event);
            refreshData();
        } catch (RuntimeException ex) {
            errorLabel.setText(ex.getMessage());
        }
    }
}
