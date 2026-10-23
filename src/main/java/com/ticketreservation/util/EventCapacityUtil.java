package com.ticketreservation.util;

public final class EventCapacityUtil {

    private EventCapacityUtil() {
        // Utility class
    }

    public static double calculateOccupancyPercentage(int bookedSeats, int totalSeats) {
        if (totalSeats <= 0) {
            throw new IllegalArgumentException("Total seats must be greater than zero");
        }
        if (bookedSeats < 0) {
            throw new IllegalArgumentException("Booked seats cannot be negative");
        }
        double percentage = ((double) bookedSeats / totalSeats) * 100.0;
        return Math.min(percentage, 100.0);
    }

    public static double calculateProjectedRevenue(int bookedSeats, double ticketPrice) {
        if (bookedSeats < 0) {
            throw new IllegalArgumentException("Booked seats cannot be negative");
        }
        if (ticketPrice < 0) {
            throw new IllegalArgumentException("Ticket price cannot be negative");
        }
        return bookedSeats * ticketPrice;
    }

    public static boolean isSoldOut(int bookedSeats, int totalSeats) {
        if (totalSeats <= 0) {
            throw new IllegalArgumentException("Total seats must be greater than zero");
        }
        return bookedSeats >= totalSeats;
    }

    public static int getRemainingSeats(int bookedSeats, int totalSeats) {
        if (totalSeats <= 0) {
            throw new IllegalArgumentException("Total seats must be greater than zero");
        }
        return Math.max(0, totalSeats - Math.max(0, bookedSeats));
    }
}
