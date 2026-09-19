package com.ticketreservation.repository;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RepositoryExceptionTest {

    @Test
    void testConstructorAndGetters() {
        Throwable cause = new RuntimeException("DB error");
        RepositoryException ex = new RepositoryException("Failed operation", cause);

        assertThat(ex).hasMessage("Failed operation");
        assertThat(ex).hasCause(cause);
    }
}
