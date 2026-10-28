package com.ticketreservation.view;

import javax.swing.JFileChooser;
import java.awt.Component;
import java.io.File;

public class DefaultFileSaveDialog implements FileSaveDialog {

    @Override
    public File showSaveDialog(Component parent, String defaultFileName) {
        JFileChooser chooser = new JFileChooser();
        if (defaultFileName != null) {
            chooser.setSelectedFile(new File(defaultFileName));
        }
        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }
}
