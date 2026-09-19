package com.ticketreservation.util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.ticketreservation.model.Event;
import com.ticketreservation.model.TicketReservation;

import java.awt.Color;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

public class PdfReportExporter {

    public void exportEventReport(Event event, List<TicketReservation> reservations, OutputStream outputStream) throws DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
        Paragraph title = new Paragraph("Event Ticket Reservation Summary", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" ")); // Spacer

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        document.add(new Paragraph("Event Title: " + event.getTitle(), headerFont));
        document.add(new Paragraph("Date: " + event.getEventDate()));
        document.add(new Paragraph("Venue: " + event.getVenueName()));
        document.add(new Paragraph(String.format(Locale.US, "Available Seats: %d / %d", event.getAvailableSeats(), event.getTotalSeats())));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        addTableHeader(table, "Res ID");
        addTableHeader(table, "Date");
        addTableHeader(table, "Customer Name");
        addTableHeader(table, "Amount ($)");

        double grandTotal = 0.0;
        for (TicketReservation res : reservations) {
            table.addCell(String.valueOf(res.getId()));
            table.addCell(res.getReservationDate());
            table.addCell(res.getCustomerName());
            table.addCell(String.format(Locale.US, "%.2f", res.getTotalAmount()));
            grandTotal += res.getTotalAmount();
        }

        document.add(table);
        document.add(new Paragraph(" "));
        
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
        document.add(new Paragraph(String.format(Locale.US, "Total Revenue: $%.2f", grandTotal), totalFont));

        document.close();
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(Color.LIGHT_GRAY);
        header.setPhrase(new Paragraph(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        table.addCell(header);
    }
}
