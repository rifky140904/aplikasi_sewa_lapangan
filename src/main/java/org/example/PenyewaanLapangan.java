package org.example;

import javax.swing.*;

public class PenyewaanLapangan {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PenyewaanLapanganGUI();
            }
        });
    }
}
