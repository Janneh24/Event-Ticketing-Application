package com.ticketreservation.model;

import java.util.Objects;

public class Seat {
    private long id;
    private long eventId;
    private String seatNumber;
    private String section;
    private double price;
    private String status;

    public Seat() {
        this.status = "AVAILABLE";
    }

    public Seat(long id, long eventId, String seatNumber, String section, double price, String status) {
        this.id = id;
        this.eventId = eventId;
        this.seatNumber = seatNumber;
        this.section = section;
        this.price = price;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return id == seat.id &&
                eventId == seat.eventId &&
                Double.compare(seat.price, price) == 0 &&
                Objects.equals(seatNumber, seat.seatNumber) &&
                Objects.equals(section, seat.section) &&
                Objects.equals(status, seat.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventId, seatNumber, section, price, status);
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", seatNumber='" + seatNumber + '\'' +
                ", section='" + section + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}
