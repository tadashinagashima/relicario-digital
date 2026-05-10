package com.mycompany.relicariodigital;

import com.mycompany.relicariodigital.view.TelaInicial;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // O visual padrao do Swing continua funcionando se o Nimbus nao estiver disponivel.
        }

        SwingUtilities.invokeLater(() -> new TelaInicial().setVisible(true));
    }
}
