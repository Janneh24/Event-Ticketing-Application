package com.ticketreservation.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DateTimeUtilTest {

    @Test
    @DisplayName("Should convert ISO to European format")
    void testToEuropeanFormat() {
        assertThat(DateTimeUtil.toEuropeanFormat("2026-10-19 20:30")).isEqualTo("19/10/2026 20:30");
        assertThat(DateTimeUtil.toEuropeanFormat("2026-01-01 00:00")).isEqualTo("01/01/2026 00:00");
    }

    @Test
    @DisplayName("Should convert European to ISO format")
    void testToIsoFormat() {
        assertThat(DateTimeUtil.toIsoFormat("19/10/2026 20:30")).isEqualTo("2026-10-19 20:30");
        assertThat(DateTimeUtil.toIsoFormat("01/01/2026 00:00")).isEqualTo("2026-01-01 00:00");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when ISO string is null or empty")
    void testToEuropeanFormatNullOrEmpty() {
        assertThatThrownBy(() -> DateTimeUtil.toEuropeanFormat(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");

        assertThatThrownBy(() -> DateTimeUtil.toEuropeanFormat("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when ISO string is invalid")
    void testToEuropeanFormatInvalid() {
        assertThatThrownBy(() -> DateTimeUtil.toEuropeanFormat("invalid-date"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid date format");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when European string is null or empty")
    void testToIsoFormatNullOrEmpty() {
        assertThatThrownBy(() -> DateTimeUtil.toIsoFormat(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");

        assertThatThrownBy(() -> DateTimeUtil.toIsoFormat("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when European string is invalid")
    void testToIsoFormatInvalid() {
        assertThatThrownBy(() -> DateTimeUtil.toIsoFormat("invalid-date"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid date format");
    }

    @Test
    @DisplayName("Private constructor should be callable via reflection")
    void privateConstructorTest() throws Exception {
        Constructor<DateTimeUtil> constructor = DateTimeUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        DateTimeUtil instance = constructor.newInstance();
        assertThat(instance).isNotNull();
    }
}
