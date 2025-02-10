/*
 * Environment.java
 *
 * Created on 5 febbraio 2007, 11.30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.system;

import lombok.extern.java.Log;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <p style="color:red; font-family:verdana;">
 * <a href="mailto:a.addis@gmail.com">Andrea Addis</a> - &copy;<a
 * href="http://infodev.wordpress.com">Information Devices </a> </p>
 * @version 2005.07.12
 */
@Log
@SuppressWarnings("unused")
public class Environment {

    public static void Beep() {
        Toolkit t = Toolkit.getDefaultToolkit();
        t.beep();
    }

    // -------------------------------------------------------------------------

    /**
     * Se in formato testo prende restituisce il system clipboard
     *
     * @return Restituisce il contenuto del system clipboard
     */
    public static String getClipboard() {
        String str;

        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            str = ((String) c.getContents(null).getTransferData(DataFlavor.stringFlavor));

        } catch (UnsupportedFlavorException | IOException e) {
            str = "";
        }

        return str;

    }

    /**
     * Inserisce una stringa nel clipboard di sistema
     *
     * @param aString La stringa da copiare sul clipboard
     */
    public static void setClipboard(String aString) {
        StringSelection stringSelection = new StringSelection(aString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    /**
     * In caso di classi:
     * file:/C:/Documents%20and%20Settings/Administrator/Documenti/JProject/JavaApplication19/build/classes/prova
     * relative:=prova
     * <p>
     * In caso di jar:
     * jar:file:/C:/Documents%20and%20Settings/Administrator/Documenti/JProject/JavaApplication19/dist/JavaApplication19.jar!/prova
     * relative:=prova
     *
     * @param parent   passare il this di chi utilizza il metodo
     * @param relative se true il nome del package corrente, se false il
     *                 percorso delle cartelle o del file jar completo
     * @return il nome del package
     */
    public static String getPackageName(Object parent, boolean relative) {
        ClassLoader cl = parent.getClass().getClassLoader();
        URL u = cl.getResource(parent.getClass().getPackage().getName());
        if (u == null) return null;

        if (relative) {
            return parent.getClass().getPackage().getName();
        } else {
            return u.toString();
        }
    }

    /**
     * Restituisce un identificativo univoco per il PC
     *
     * @return un ID del PC
     */
    public static String getComputerName() {
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME")) {
            return env.get("COMPUTERNAME");
        } else return env.getOrDefault("HOSTNAME", "Unknown Computer");
    }

    public static String getSystemInformations() {
        StringBuilder output = new StringBuilder("[System Information for " + getComputerName() + "]\n");

        output.append("\n### Environment ###\n");
        for (String s : System.getenv().keySet()) {
            output.append(s).append(":=").append(System.getenv(s)).append("\n");
        }

        output.append("\n### Properties ###\n");
        for (String p : System.getProperties().stringPropertyNames()) {
            output.append(p).append(":=").append(System.getProperty(p)).append("\n");
        }

        output.append("\n### Operative System Usage ###\n");
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().startsWith("get") && Modifier.isPublic(method.getModifiers())) {
                Object value;
                try {
                    value = method.invoke(operatingSystemMXBean);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    value = e;
                } // try
                output.append(method.getName()).append(" = ").append(value).append("\n");
            } // if
        } // for

        output.append("\nTotal Memory:").append(Runtime.getRuntime().totalMemory()).append("\n");
        output.append("Used Memory:").append(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()).append("\n");
        return output.toString();
    }

    /**
     * Open a folder on the working directory
     */
    public static void openFolder() {
        String currentDir = System.getProperty("user.dir");
        try {
            Desktop.getDesktop().open(new File(currentDir));
        } catch (IOException ignore) {
            // Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Open the folder path
     *
     * @param path The Path
     */
    public static void openFolder(String path) {
        try {
            Desktop.getDesktop().open(new File(path));
        } catch (IOException ignore) {
            // Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Apre il browser su un URL. Può essere anche un file locale, utilizzare
     * qualcosa di questo tipo:
     * <pre>
     * Environment.open_browser(new File("chinazi/html/t_untagged.html").getAbsolutePath().replaceAll("\\\\", "/"));
     * </pre>
     *
     * @param url L'URL da aprire.
     */
    public static void open_browser(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (URISyntaxException | IOException | UnsupportedOperationException ex) {
                Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, null, ex);
                log.info("UltraCMS, UC_Helpers: Desktop not supported!");
            }
        }
    }

}
