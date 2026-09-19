package com.ticketreservation.util;

import com.ticketreservation.model.Event;
import com.ticketreservation.model.TicketReservation;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PdfReportExporterTest {

    @Test
    void testExportEventReport() throws Exception {
        PdfReportExporter exporter = new PdfReportExporter();
        Event event = new Event(1L, "Concert", "2026-10-10", "Hall", 100, 98);
        TicketReservation r1 = new TicketReservation(10L, "2026-09-16", "Alice", 150.0, 5L, 1L, 50L);
        TicketReservation r2 = new TicketReservation(11L, "2026-09-16", "Bob", 75.0, 6L, 1L, 51L);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        exporter.exportEventReport(event, List.of(r1, r2), baos);

        byte[] pdfBytes = baos.toByteArray();
        assertThat(pdfBytes).isNotEmpty();
        // PDF header magic bytes %PDF
        assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");
    }
}
