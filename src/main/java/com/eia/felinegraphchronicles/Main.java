package com.eia.felinegraphchronicles;

import com.eia.felinegraphchronicles.gui.MainFrame;

import javax.swing.*;

public final class Main {
    public static void main(String[] args) {
        // Swing exige que la GUI se construya en su propio hilo (Event
        // Dispatch Thread), no directamente en main()
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}