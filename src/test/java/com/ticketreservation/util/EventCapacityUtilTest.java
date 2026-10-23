package com.ticketreservation.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventCapacityUtilTest {

    @Test
    @DisplayName("Should calculate occupancy percentage correctly")
    void testCalculateOccupancyPercentage() {
        assertThat(EventCapacityUtil.calculateOccupancyPercentage(50, 100)).isEqualTo(50.0);
        assertThat(EventCapacityUtil.calculateOccupancyPercentage(0, 100)).isEqualTo(0.0);
        assertThat(EventCapacityUtil.calculateOccupancyPercentage(100, 100)).isEqualTo(100.0);
        assertThat(EventCapacityUtil.calculateOccupancyPercentage(150, 100)).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when totalSeats <= 0 or bookedSeats < 0")
    void testCalculateOccupancyPercentageInvalid() {
        assertThatThrownBy(() -> EventCapacityUtil.calculateOccupancyPercentage(10, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Total seats must be greater than zero");

        assertThatThrownBy(() -> EventCapacityUtil.calculateOccupancyPercentage(10, -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Total seats must be greater than zero");

        assertThatThrownBy(() -> EventCapacityUtil.calculateOccupancyPercentage(-1, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Booked seats cannot be negative");
    }

    @Test
    @DisplayName("Should calculate projected revenue correctly")
    void testCalculateProjectedRevenue() {
        assertThat(EventCapacityUtil.calculateProjectedRevenue(10, 25.5)).isEqualTo(255.0);
        assertThat(EventCapacityUtil.calculateProjectedRevenue(0, 50.0)).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for negative revenue inputs")
    void testCalculateProjectedRevenueInvalid() {
        assertThatThrownBy(() -> EventCapacityUtil.calculateProjectedRevenue(-1, 20.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Booked seats cannot be negative");

        assertThatThrownBy(() -> EventCapacityUtil.calculateProjectedRevenue(10, -5.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ticket price cannot be negative");
    }

    @Test
    @DisplayName("Should determine if event is sold out")
    void testIsSoldOut() {
        assertThat(EventCapacityUtil.isSoldOut(100, 100)).isTrue();
        assertThat(EventCapacityUtil.isSoldOut(105, 100)).isTrue();
        assertThat(EventCapacityUtil.isSoldOut(99, 100)).isFalse();

        assertThatThrownBy(() -> EventCapacityUtil.isSoldOut(50, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Total seats must be greater than zero");
    }

    @Test
    @DisplayName("Should calculate remaining seats correctly")
    void testGetRemainingSeats() {
        assertThat(EventCapacityUtil.getRemainingSeats(40, 100)).isEqualTo(60);
        assertThat(EventCapacityUtil.getRemainingSeats(100, 100)).isEqualTo(0);
        assertThat(EventCapacityUtil.getRemainingSeats(110, 100)).isEqualTo(0);
        assertThat(EventCapacityUtil.getRemainingSeats(-5, 100)).isEqualTo(100);

        assertThatThrownBy(() -> EventCapacityUtil.getRemainingSeats(50, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Total seats must be greater than zero");
    }

    @Test
    @DisplayName("Private constructor should be callable via reflection")
    void privateConstructorTest() throws Exception {
        Constructor<EventCapacityUtil> constructor = EventCapacityUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        EventCapacityUtil instance = constructor.newInstance();
        assertThat(instance).isNotNull();
    }
}
