/** @author María Fortuna */

//Código por María Fortuna Rodríguez Márquez
//Realice este código curante el primer año de programación durante exámenes ; 
//Tenía experiencia previa trabajando en nodos tras hacer sistemas de scripts en mods de videojuegos.
//Durante el primer año, mi primer proyecto trató de ser un juego rpg de terror... pero era muy complejo
//Más adelante, cree esta plataforma para poder hacer mis propios juegos con un estilo similar a 
//los final fantasy clásicos y los primeros rpgs.

package com.rpgmaker;

import com.rpgmaker.controller.MainController;
import com.rpgmaker.editor.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;

public class Main {
    public static void main(String[] args) {
        configureLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            MainController controller = new MainController();
            MainFrame frame = new MainFrame(controller);
            frame.setVisible(true);
        });
    }

    private static void configureLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    UIManager.put("control", new Color(50, 50, 50));
                    UIManager.put("info", new Color(50, 50, 50));
                    UIManager.put("nimbusBase", new Color(30, 30, 30));
                    UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
                    UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
                    UIManager.put("nimbusFocus", new Color(115, 164, 209));
                    UIManager.put("nimbusGreen", new Color(176, 179, 50));
                    UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
                    UIManager.put("nimbusLightBackground", new Color(45, 45, 45));
                    UIManager.put("nimbusOrange", new Color(191, 98, 4));
                    UIManager.put("nimbusRed", new Color(169, 46, 34));
                    UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
                    UIManager.put("nimbusSelectionBackground", new Color(70, 130, 180));
                    UIManager.put("text", new Color(220, 220, 220));
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al establecer el tema Nimbus: " + e.getMessage());
        }
    }
}
