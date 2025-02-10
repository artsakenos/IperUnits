/*
 * Screen.java
 *
 * Created on 20 settembre 2006, 15.04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.system;

import lombok.extern.java.Log;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author <p style="color:red; font-family:verdana;">
 * <a href="mailto:a.addis@gmail.com">Andrea Addis</a> - &copy;<a
 * href="http://infodev.wordpress.com">Information Devices </a> </p>
 * @version 2005.07.12
 */
@Log
@SuppressWarnings("unused")
public class Screen {

    public static int getWidth() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        return screenSize.width;
    }

    public static int getHeight() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        return screenSize.height;
    }

    public static BufferedImage getScreenShot(int secsWait) {
        try {
            Thread.sleep(secsWait * 1000L);
        } catch (InterruptedException ex) {
            log.severe("getScreenShot(" + secsWait + ") - " + ex.getLocalizedMessage());
        }

        // determine current screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Rectangle screenRect = new Rectangle(screenSize);

        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            log.severe("getScreenShot(" + secsWait + ") - " + ex.getLocalizedMessage());
        }
        if (robot == null) {
            return null;
        }

        return robot.createScreenCapture(screenRect);
    }

    /**
     * Fa uno screenshot dello schermo e lo salva su un file PNG
     *
     * @param outFileName Il nome del file output. Non inserire l'estensione,
     *                    sar� messa automaticamente a .png
     * @param secsWait    Il numero di secondi di attesa prima dello scatto.
     */
    public static void screenShotToFile(String outFileName, int secsWait) {
        outFileName += ".png";
        BufferedImage image = getScreenShot(secsWait);
        if (image == null) {
            log.severe("screenShotToFile(" + outFileName + "," + secsWait + ") - No Image!!");
            return;
        }

        try {
            // save captured image to PNG file
            ImageIO.write(image, "png", new File(outFileName));
        } catch (IOException ex) {
            log.severe("screenShotToFile(" + outFileName + "," + secsWait + ") - " + ex.getLocalizedMessage());
        }
        // give feedback
        System.out.println("Saved screen shot (" + image.getWidth() + " x " + image.getHeight() + " pixels) to file \"" + outFileName + "\".");
    }

    public static BufferedImage getScreenShot(Rectangle screenRect) {

        // determine current screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        if (screenRect == null) {
            screenRect = new Rectangle(screenSize);
        }

        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            log.severe("screenShotToFile(" + screenRect + ") - " + ex.getLocalizedMessage());
        }
        if (robot == null) {
            return null;
        }
        return robot.createScreenCapture(screenRect);
    }
}
