package com.ticketreservation.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateTimeUtil {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter EU_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DateTimeUtil() {
        // Utility class
    }

    public static String toEuropeanFormat(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.trim().isEmpty()) {
            throw new IllegalArgumentException("Date time string cannot be null or empty");
        }
        try {
            LocalDateTime dateTime = LocalDateTime.parse(isoDateTime.trim(), ISO_FORMATTER);
            return dateTime.format(EU_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format, expected yyyy-MM-dd HH:mm: " + isoDateTime, e);
        }
    }

    public static String toIsoFormat(String euDateTime) {
        if (euDateTime == null || euDateTime.trim().isEmpty()) {
            throw new IllegalArgumentException("Date time string cannot be null or empty");
        }
        try {
            LocalDateTime dateTime = LocalDateTime.parse(euDateTime.trim(), EU_FORMATTER);
            return dateTime.format(ISO_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format, expected dd/MM/yyyy HH:mm: " + euDateTime, e);
        }
    }
}
