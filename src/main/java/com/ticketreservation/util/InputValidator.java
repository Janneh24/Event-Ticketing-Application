package com.ticketreservation.util;

public final class InputValidator {

    private InputValidator() {
        // Utility class
    }

    public static boolean isValidUsername(String username) {
        return username != null && username.trim().matches("^[a-zA-Z0-9_]{3,20}$");
    }

    public static boolean isValidPrice(double price) {
        return price >= 0.0 && !Double.isInfinite(price) && !Double.isNaN(price);
    }

    public static boolean isValidSeatCount(int count) {
        return count > 0 && count <= 50000;
    }

    public static boolean isValidEventTitle(String title) {
        return title != null && !title.trim().isEmpty() && title.trim().length() <= 100;
    }
}
