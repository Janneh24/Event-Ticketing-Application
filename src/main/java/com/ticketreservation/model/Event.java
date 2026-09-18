package com.ticketreservation.model;

import java.util.Objects;

public class Event {
    private long id;
    private String title;
    private String eventDate;
    private String venueName;
    private int totalSeats;
    private int availableSeats;

    public Event() {}

    public Event(long id, String title, String eventDate, String venueName, int totalSeats, int availableSeats) {
        this.id = id;
        this.title = title;
        this.eventDate = eventDate;
        this.venueName = venueName;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id &&
                totalSeats == event.totalSeats &&
                availableSeats == event.availableSeats &&
                Objects.equals(title, event.title) &&
                Objects.equals(eventDate, event.eventDate) &&
                Objects.equals(venueName, event.venueName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, eventDate, venueName, totalSeats, availableSeats);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", venueName='" + venueName + '\'' +
                ", totalSeats=" + totalSeats +
                ", availableSeats=" + availableSeats +
                '}';
    }
}
