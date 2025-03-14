/*
 * SuperGraphics.java
 *
 * Created on 16 ottobre 2006, 13.26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.form;

import tk.artsakenos.iperunits.system.Screen;

import javax.swing.*;
import java.awt.*;

/**
 * La classe SuperGraphics fornisce dei metodi statici per la gestione di
 * aspetti grafici delle vostre applicazioni. SuperGraphics è un'altra ottima
 * classe delle UltraUnits
 *
 * @author <p style="color:red; font-family:verdana;">
 * <a href="mailto:a.addis@gmail.com">Andrea Addis</a> - &copy;<a
 * href="http://infodev.wordpress.com">Information Devices </a> </p>
 * @version 2005.07.12
 */
@SuppressWarnings("unused")
public class FormGraphics {

    /**
     * Setta il look and fill, lasciare "" per mettere nimbus di default.
     *
     * @param className Il nome della classe, se "" verrà messo quello di
     *                  nimbus. Altre opzioni shortcode sono: system, cross, metal.
     */
    public static void setLookAndFeel(String className) {
        if (className == null || className.isEmpty()) {
            // Usa Nimbus come default moderno
            className = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
        } else if ("system".equalsIgnoreCase(className)) {
            // Usa il Look and Feel del sistema operativo
            className = UIManager.getSystemLookAndFeelClassName();
        } else if ("cross".equalsIgnoreCase(className)) {
            // Usa il Look and Feel cross-platform
            className = UIManager.getCrossPlatformLookAndFeelClassName();
        } else if ("metal".equalsIgnoreCase(className)) {
            // Usa il Metal Look and Feel (Ocean Theme)
            className = "javax.swing.plaf.metal.MetalLookAndFeel";
        }

        // Imposta il Look and Feel
        try {
            UIManager.setLookAndFeel(className);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Effettua un super refresh di un JComponent. Attenzione anche se le
     * eccezioni vengono gestite pu� partire qualche stack trace da dentro ai
     * componenti.
     *
     * @param component Il componente di cui effettuare il refresh
     */
    public static void superSatanicRefresh(JComponent component) {
        try {
            // System.out.println("ModelLstTopics:"+modelLstTopics+";");
            component.repaint();
            component.validate();
            component.revalidate();
            component.updateUI();
        } catch (Exception ignored) {
        }
        System.out.print("");
        System.out.flush();
    }

    /**
     * Imposta la posizione del frame. Immaginare lo schermo diviso in 9
     * rettangoli numerati da 1 a 9.
     * <p>
     * e.g., 1 in alto a sinistra, 5 è il centrale, etc...
     *
     * @param frame    Il frame da spostare
     * @param position La posizione (da 1 a 9)
     * @param margin   Il margine desiderato in pixel (di solito = 15)
     * @param startBar La larghezza della startBar (di solito = 40)
     */
    public static void setFramePosition(JFrame frame, int position, int margin, int startBar) {
        int sw = Screen.size().width;
        int sh = Screen.size().height;
        Rectangle fr = frame.getBounds();
        int px = 0;
        int py = 0;

        if (position == 1 || position == 2 || position == 3) {
            py = margin;
        }
        if (position == 4 || position == 5 || position == 6) {
            py = (int) (sh - fr.getHeight()) / 2;
        }
        if (position == 7 || position == 8 || position == 9) {
            py = (int) (sh - fr.getHeight() - margin - startBar);
        }
        if (position == 1 || position == 4 || position == 7) {
            px = margin;
        }
        if (position == 2 || position == 5 || position == 8) {
            px = (int) (sw - fr.getWidth()) / 2;
        }
        if (position == 3 || position == 6 || position == 9) {
            px = (int) (sw - fr.getWidth() - margin);
        }

        frame.setLocation(px, py);
    }

    public static Image setIconResource(JFrame frame, String iconResourcePath) {
        // Imposto l'immagine di icona
        java.net.URL url = ClassLoader.getSystemResource(iconResourcePath);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(url);
        frame.setIconImage(img);
        return img;
    }
}
