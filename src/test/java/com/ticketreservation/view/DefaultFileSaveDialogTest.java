package com.ticketreservation.view;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class DefaultFileSaveDialogTest {

    @Test
    void testConstructor() {
        assertThatCode(DefaultFileSaveDialog::new).doesNotThrowAnyException();
    }

    @Test
    void testInstance() {
        DefaultFileSaveDialog dialog = new DefaultFileSaveDialog();
        assertThat(dialog).isNotNull();
    }
}
