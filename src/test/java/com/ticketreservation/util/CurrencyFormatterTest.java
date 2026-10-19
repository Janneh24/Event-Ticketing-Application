package com.ticketreservation.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrencyFormatterTest {

    @Test
    @DisplayName("Should format double value into Euro currency string")
    void shouldFormatEuro() {
        assertThat(CurrencyFormatter.formatEuro(100.5)).isEqualTo("€ 100,50");
        assertThat(CurrencyFormatter.formatEuro(1250.0)).isEqualTo("€ 1.250,00");
        assertThat(CurrencyFormatter.formatEuro(0.0)).isEqualTo("€ 0,00");
    }

    @Test
    @DisplayName("Should parse valid Euro currency string to double")
    void shouldParseEuro() {
        assertThat(CurrencyFormatter.parseEuro("€ 100,50")).isEqualTo(100.5);
        assertThat(CurrencyFormatter.parseEuro("€ 1.250,00")).isEqualTo(1250.0);
        assertThat(CurrencyFormatter.parseEuro("50,25")).isEqualTo(50.25);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when parsing null or empty text")
    void shouldThrowWhenParsingNullOrEmpty() {
        assertThatThrownBy(() -> CurrencyFormatter.parseEuro(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");

        assertThatThrownBy(() -> CurrencyFormatter.parseEuro("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when parsing invalid currency format")
    void shouldThrowWhenParsingInvalidFormat() {
        assertThatThrownBy(() -> CurrencyFormatter.parseEuro("abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid currency format");
    }

    @Test
    @DisplayName("Private constructor should be callable via reflection")
    void privateConstructorTest() throws Exception {
        Constructor<CurrencyFormatter> constructor = CurrencyFormatter.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        CurrencyFormatter instance = constructor.newInstance();
        assertThat(instance).isNotNull();
    }
}
