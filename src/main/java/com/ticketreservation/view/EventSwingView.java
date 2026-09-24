package com.ticketreservation.view;

import com.ticketreservation.controller.EventController;
import com.ticketreservation.controller.TicketController;
import com.ticketreservation.model.Event;
import com.ticketreservation.model.Seat;
import com.ticketreservation.model.TicketReservation;
import com.ticketreservation.model.User;
import com.ticketreservation.util.PdfReportExporter;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventSwingView extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EventSwingView.class.getName());

    private final EventController eventController;
    private final TicketController ticketController;
    private User currentUser;
    private JFileChooser fileChooser;

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setFileChooser(JFileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }

    public JFileChooser getFileChooser() {
        return (this.fileChooser != null) ? this.fileChooser : new JFileChooser();
    }

    private JTable eventTable;
    private DefaultTableModel eventTableModel;
    private JTable seatTable;
    private DefaultTableModel seatTableModel;

    private JComboBox<EventWrapper> eventCombo;
    private JTextField customerNameField;
    private JButton reserveButton;
    private JButton cancelButton;
    private JButton exportPdfButton;
    private JLabel errorLabel;

    public EventSwingView(EventController eventController, TicketController ticketController, User currentUser) {
        this.eventController = eventController;
        this.ticketController = ticketController;
        this.currentUser = currentUser;

        initUI();
    }

    private void initUI() {
        setTitle("Event Ticket Reservation - Customer Portal");
        setSize(700, 420);
        setLocation(0, 0);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        // Top Panel: Error & Information
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        errorLabel = new JLabel(" ");
        errorLabel.setName("errorLabel");
        errorLabel.setForeground(Color.RED);
        topPanel.add(errorLabel);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Event & Seat Tables
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        // Event Table
        eventTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Date", "Venue", "Available Seats"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        eventTable = new JTable(eventTableModel);
        eventTable.setName("eventTable");
        eventTable.getSelectionModel().addListSelectionListener(e -> {
            int row = eventTable.getSelectedRow();
            if (row >= 0 && row < eventCombo.getItemCount()) {
                eventCombo.setSelectedIndex(row);
            }
        });
        JScrollPane eventScrollPane = new JScrollPane(eventTable);
        eventScrollPane.setPreferredSize(new Dimension(650, 110));
        centerPanel.add(eventScrollPane);

        // Seat Table
        seatTableModel = new DefaultTableModel(new Object[]{"Seat ID", "Event ID", "Seat Number", "Section", "Price", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        seatTable = new JTable(seatTableModel);
        seatTable.setName("seatTable");
        JScrollPane seatScrollPane = new JScrollPane(seatTable);
        seatScrollPane.setPreferredSize(new Dimension(650, 110));
        centerPanel.add(seatScrollPane);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel: Form & Actions
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 2, 2));

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        formPanel.add(new JLabel("Customer Name:"));
        customerNameField = new JTextField(12);
        customerNameField.setName("customerNameField");
        formPanel.add(customerNameField);

        formPanel.add(new JLabel("Select Event:"));
        eventCombo = new JComboBox<>();
        eventCombo.setName("eventCombo");
        eventCombo.addActionListener(e -> refreshSeats());
        formPanel.add(eventCombo);

        bottomPanel.add(formPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        reserveButton = new JButton("Reserve Selected Seat");
        reserveButton.setName("reserveButton");
        reserveButton.addActionListener(e -> reserveSeatAction());
        buttonPanel.add(reserveButton);

        cancelButton = new JButton("Cancel Reservation");
        cancelButton.setName("cancelButton");
        cancelButton.addActionListener(e -> cancelReservationAction());
        buttonPanel.add(cancelButton);

        exportPdfButton = new JButton("Export Summary PDF");
        exportPdfButton.setName("exportPdfButton");
        exportPdfButton.addActionListener(e -> exportPdfAction());
        buttonPanel.add(exportPdfButton);

        bottomPanel.add(buttonPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        // Load initial data
        refreshData();
    }

    public void refreshData() {
        eventTableModel.setRowCount(0);

        ActionListener[] listeners = eventCombo.getActionListeners();
        for (ActionListener l : listeners) {
            eventCombo.removeActionListener(l);
        }

        eventCombo.removeAllItems();

        List<Event> events = eventController.getAllEvents();
        for (Event event : events) {
            eventTableModel.addRow(new Object[]{
                    event.getId(),
                    event.getTitle(),
                    event.getEventDate(),
                    event.getVenueName(),
                    event.getAvailableSeats() + " / " + event.getTotalSeats()
            });
            eventCombo.addItem(new EventWrapper(event));
        }

        for (ActionListener l : listeners) {
            eventCombo.addActionListener(l);
        }

        refreshSeats();
    }

    public void refreshSeats() {
        seatTableModel.setRowCount(0);
        EventWrapper selected = (EventWrapper) eventCombo.getSelectedItem();
        if (selected != null) {
            List<Seat> seats = ticketController.getSeatsForEvent(selected.getEvent().getId());
            for (Seat seat : seats) {
                seatTableModel.addRow(new Object[]{
                        seat.getId(),
                        seat.getEventId(),
                        seat.getSeatNumber(),
                        seat.getSection(),
                        String.format(Locale.US, "%.2f", seat.getPrice()),
                        seat.getStatus()
                });
            }
        }
    }

    private void reserveSeatAction() {
        int selectedRow = seatTable.getSelectedRow();
        if (selectedRow == -1) {
            errorLabel.setText("Please select a seat from the table");
            return;
        }

        String customerName = customerNameField.getText().trim();
        if (customerName.isEmpty()) {
            errorLabel.setText("Customer name cannot be empty");
            return;
        }

        long seatId = Long.parseLong(seatTableModel.getValueAt(selectedRow, 0).toString());
        long eventId = Long.parseLong(seatTableModel.getValueAt(selectedRow, 1).toString());
        double price = Double.parseDouble(seatTableModel.getValueAt(selectedRow, 4).toString());

        TicketReservation reservation = new TicketReservation(
                0L, "2026-09-16", customerName, price,
                currentUser != null ? currentUser.getId() : null,
                eventId, seatId
        );

        try {
            ticketController.reserveTicket(reservation);
            errorLabel.setText(" ");
            refreshData();
        } catch (RuntimeException ex) {
            LOGGER.log(Level.INFO, "Reservation action failed", ex);
            errorLabel.setText(ex.getMessage());
        }
    }

    private void cancelReservationAction() {
        int selectedRow = seatTable.getSelectedRow();
        if (selectedRow == -1) {
            errorLabel.setText("Please select a seat to cancel reservation");
            return;
        }

        long seatId = Long.parseLong(seatTableModel.getValueAt(selectedRow, 0).toString());
        long eventId = Long.parseLong(seatTableModel.getValueAt(selectedRow, 1).toString());

        List<TicketReservation> reservations = ticketController.getAllReservations();
        TicketReservation target = null;
        for (TicketReservation res : reservations) {
            if (res.getSeatId() == seatId && res.getEventId() == eventId) {
                target = res;
                break;
            }
        }

        if (target == null) {
            errorLabel.setText("No active reservation found for selected seat");
            return;
        }

        try {
            ticketController.cancelReservation(target.getId());
            errorLabel.setText(" ");
            refreshData();
        } catch (RuntimeException ex) {
            LOGGER.log(Level.INFO, "Cancel reservation failed", ex);
            errorLabel.setText(ex.getMessage());
        }
    }

    private void exportPdfAction() {
        EventWrapper selected = (EventWrapper) eventCombo.getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Please select an event to export PDF");
            return;
        }

        Event event = selected.getEvent();
        List<TicketReservation> reservations = ticketController.getAllReservations();
        List<TicketReservation> eventReservations = new ArrayList<>();
        for (TicketReservation r : reservations) {
            if (r.getEventId() == event.getId()) {
                eventReservations.add(r);
            }
        }

        JFileChooser chooser = getFileChooser();
        chooser.setSelectedFile(new File("Event_Summary_" + event.getId() + ".pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            exportPdfToFile(event, eventReservations, chooser.getSelectedFile());
        }
    }

    void exportPdfToFile(Event event, List<TicketReservation> eventReservations, File targetFile) {
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            PdfReportExporter exporter = new PdfReportExporter();
            exporter.exportEventReport(event, eventReservations, fos);
            errorLabel.setText(" ");
        } catch (IOException | RuntimeException ex) {
            LOGGER.log(Level.WARNING, "Error exporting PDF report", ex);
            errorLabel.setText("Failed to export PDF: " + ex.getMessage());
        }
    }

    private static class EventWrapper {
        private final Event event;

        public EventWrapper(Event event) {
            this.event = event;
        }

        public Event getEvent() {
            return event;
        }

        @Override
        public String toString() {
            return event.getTitle() + " (" + event.getEventDate() + ")";
        }
    }
}
