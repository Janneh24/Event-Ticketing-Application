package com.ticketreservation.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class CurrencyFormatter {

    private CurrencyFormatter() {
        // Utility class
    }

    public static String formatEuro(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMAN);
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("€ #,##0.00", symbols);
        return df.format(amount);
    }

    public static double parseEuro(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Amount text cannot be null or empty");
        }
        String cleaned = text.replace("€", "").trim().replace(".", "").replace(",", ".");
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid currency format: " + text, e);
        }
    }
}
