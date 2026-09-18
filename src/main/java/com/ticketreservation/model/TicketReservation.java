package com.ticketreservation.model;

import java.util.Objects;

public class TicketReservation {
    private long id;
    private String reservationDate;
    private String customerName;
    private double totalAmount;
    private Long userId;
    private long eventId;
    private long seatId;

    public TicketReservation() {}

    public TicketReservation(long id, String reservationDate, String customerName, double totalAmount, Long userId, long eventId, long seatId) {
        this.id = id;
        this.reservationDate = reservationDate;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.userId = userId;
        this.eventId = eventId;
        this.seatId = seatId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getSeatId() {
        return seatId;
    }

    public void setSeatId(long seatId) {
        this.seatId = seatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketReservation that = (TicketReservation) o;
        return id == that.id &&
                Double.compare(that.totalAmount, totalAmount) == 0 &&
                eventId == that.eventId &&
                seatId == that.seatId &&
                Objects.equals(reservationDate, that.reservationDate) &&
                Objects.equals(customerName, that.customerName) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reservationDate, customerName, totalAmount, userId, eventId, seatId);
    }

    @Override
    public String toString() {
        return "TicketReservation{" +
                "id=" + id +
                ", reservationDate='" + reservationDate + '\'' +
                ", customerName='" + customerName + '\'' +
                ", totalAmount=" + totalAmount +
                ", userId=" + userId +
                ", eventId=" + eventId +
                ", seatId=" + seatId +
                '}';
    }
}
