package tk.artsakenos.iperunits.system;

import lombok.extern.java.Log;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Utility class for screen operations like capturing screenshots and getting screen dimensions.
 */
@Log
public class Screen {

    public static Dimension size() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * Takes a screenshot of the specified rectangle after waiting for the specified time.
     * If rectangle is null, captures the entire screen.
     * If waitMillisecs is 0, takes the screenshot immediately.
     *
     * @param screenRect    rectangle to capture, or null for entire screen
     * @param waitMillisecs milliseconds to wait before taking the screenshot
     * @return the screenshot as a BufferedImage, or null if an error occurs
     */
    public static BufferedImage screenshot(Rectangle screenRect, long waitMillisecs) {
        if (waitMillisecs > 0) {
            try {
                Thread.sleep(waitMillisecs);
            } catch (InterruptedException ex) {
                log.severe("Interrupted while waiting to take screenshot: " + ex.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        Rectangle captureArea = screenRect != null
                ? screenRect
                : new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

        try {
            return new Robot().createScreenCapture(captureArea);
        } catch (AWTException ex) {
            log.severe("Failed to create Robot for screenshot: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Takes a screenshot of the entire screen.
     *
     * @return the screenshot as a BufferedImage, or null if an error occurs
     */
    public static BufferedImage screenshot() {
        return screenshot(null, 0);
    }

    /**
     * Takes a screenshot and saves it to a PNG file.
     *
     * @param outFileName   base filename without extension
     * @param screenRect    rectangle to capture, or null for entire screen
     * @param waitMillisecs milliseconds to wait before taking the screenshot
     * @return true if the screenshot was successfully saved, false otherwise
     */
    public static boolean screenshotToFile(String outFileName, Rectangle screenRect, long waitMillisecs) {

        BufferedImage image = screenshot(screenRect, waitMillisecs);
        if (image == null) {
            log.severe("Failed to capture screenshot for file: " + outFileName);
            return false;
        }

        try {
            File outputFile = Paths.get(outFileName).toFile();
            ImageIO.write(image, "PNG", outputFile);
            log.info(String.format("Saved screenshot (%d x %d pixels) to file \"%s\"",
                    image.getWidth(), image.getHeight(), outputFile.getAbsolutePath()));
            return true;
        } catch (IOException ex) {
            log.severe("Failed to save screenshot to file " + outFileName + ": " + ex.getMessage());
            return false;
        }
    }

    /**
     * Saves a screenshot of the entire screen to a file.
     *
     * @param outFileName base filename without extension
     * @return true if the screenshot was successfully saved, false otherwise
     */
    public static boolean screenshotToFile(String outFileName) {
        return screenshotToFile(outFileName, null, 0);
    }
}