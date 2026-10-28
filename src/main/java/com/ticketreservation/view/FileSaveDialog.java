package com.ticketreservation.view;

import java.awt.Component;
import java.io.File;

public interface FileSaveDialog {
    File showSaveDialog(Component parent, String defaultFileName);
}
