package com.ticketreservation.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

class InputValidatorTest {

    @Test
    @DisplayName("Should validate username correctly")
    void testIsValidUsername() {
        assertThat(InputValidator.isValidUsername("valid_user")).isTrue();
        assertThat(InputValidator.isValidUsername("abc")).isTrue();
        assertThat(InputValidator.isValidUsername("12345678901234567890")).isTrue();

        assertThat(InputValidator.isValidUsername(null)).isFalse();
        assertThat(InputValidator.isValidUsername("")).isFalse();
        assertThat(InputValidator.isValidUsername("   ")).isFalse();
        assertThat(InputValidator.isValidUsername("ab")).isFalse(); // too short
        assertThat(InputValidator.isValidUsername("this_username_is_way_too_long_to_be_valid")).isFalse();
        assertThat(InputValidator.isValidUsername("user@name")).isFalse(); // invalid chars
    }

    @Test
    @DisplayName("Should validate price correctly")
    void testIsValidPrice() {
        assertThat(InputValidator.isValidPrice(0.0)).isTrue();
        assertThat(InputValidator.isValidPrice(99.99)).isTrue();
        assertThat(InputValidator.isValidPrice(1000.0)).isTrue();

        assertThat(InputValidator.isValidPrice(-0.01)).isFalse();
        assertThat(InputValidator.isValidPrice(-100.0)).isFalse();
        assertThat(InputValidator.isValidPrice(Double.POSITIVE_INFINITY)).isFalse();
        assertThat(InputValidator.isValidPrice(Double.NEGATIVE_INFINITY)).isFalse();
        assertThat(InputValidator.isValidPrice(Double.NaN)).isFalse();
    }

    @Test
    @DisplayName("Should validate seat count correctly")
    void testIsValidSeatCount() {
        assertThat(InputValidator.isValidSeatCount(1)).isTrue();
        assertThat(InputValidator.isValidSeatCount(50000)).isTrue();
        assertThat(InputValidator.isValidSeatCount(250)).isTrue();

        assertThat(InputValidator.isValidSeatCount(0)).isFalse();
        assertThat(InputValidator.isValidSeatCount(-1)).isFalse();
        assertThat(InputValidator.isValidSeatCount(50001)).isFalse();
    }

    @Test
    @DisplayName("Should validate event title correctly")
    void testIsValidEventTitle() {
        assertThat(InputValidator.isValidEventTitle("Rock Concert")).isTrue();
        assertThat(InputValidator.isValidEventTitle("A")).isTrue();

        assertThat(InputValidator.isValidEventTitle(null)).isFalse();
        assertThat(InputValidator.isValidEventTitle("")).isFalse();
        assertThat(InputValidator.isValidEventTitle("   ")).isFalse();
        assertThat(InputValidator.isValidEventTitle("A".repeat(101))).isFalse();
    }

    @Test
    @DisplayName("Private constructor should be callable via reflection")
    void privateConstructorTest() throws Exception {
        Constructor<InputValidator> constructor = InputValidator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InputValidator instance = constructor.newInstance();
        assertThat(instance).isNotNull();
    }
}
