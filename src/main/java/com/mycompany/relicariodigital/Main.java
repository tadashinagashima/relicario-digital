package com.mycompany.relicariodigital;

import com.mycompany.relicariodigital.View.MainFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
