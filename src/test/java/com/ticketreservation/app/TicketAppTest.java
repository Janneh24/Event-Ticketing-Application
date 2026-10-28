package com.ticketreservation.app;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class TicketAppTest {

    @Test
    void testConstructor() {
        assertThatCode(TicketApp::new).doesNotThrowAnyException();
    }

    @Test
    void testCurrentViewGetterAndSetter() {
        TicketApp.setCurrentView(null);
        assertThat(TicketApp.getCurrentView()).isNull();
    }
}
