/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.system;

import java.awt.*;

import static tk.artsakenos.iperunits.system.SuperExecutor.*;

/**
 * La classe Human, si propone di simulare la presenza di un utente alla
 * console.
 * <p>
 * E' un linguaggio di Script.
 *
 * @author <p style="color:red; font-family:verdana;">
 * <a href="mailto:a.addis@gmail.com">Andrea Addis</a> - &copy;<a
 * href="http://infodev.wordpress.com">Information Devices </a> </p>
 * @version 2005.07.12
 */
@SuppressWarnings("unused")
public class Human {

    /**
     * Il comando si esprime come CONTEXT COMMAND {Comando}. Le prime parole
     * sono maiuscole (case sensitive}
     * <pre>
     * CONTEXT      ACTION      Comando
     * -----------|-----------|-------------------------------------------------
     * KEYBOARD     TYPE        {Stringa}   Stringa nel formato Keyboard.Write
     *
     * MOUSE        MOVETO      {x,y}       Muove il mouse alla posizione {x,y}
     * MOUSE        MOVETOR     {x,y}       Muove rapidamente il mouse alla posizione {x,y}
     * MOUSE        MOVEBY      {dx,dy}     Muove il mouse relativamente dalla posizione {dx, dy}
     * MOUSE        CLICK       {String}    Stringa nel formato Mouse.Click
     * MOUSE        WHEEL       delta       Ruota la rotella di delat (+ gi�, -s�)
     *
     * SYSTEM       WAIT        {int}       Millisecondi di attesa
     * SYSTEM       WAITCHANGE  {x,y}       Attende il cambio del pixel in {x,y}
     * SYSTEM       WAITCOLOR   {x,y,r,g,b} Attende il colore color(r,g,b) nel pixel in {x,y}
     * SYSTEM       EXECUTE     {String}    Il percorso del programma da eseguire
     * SYSTEM       START       {String}    Il percorso del file da eseguire (solo windows)
     * SYSTEM       BEEP        freq        Un beep di sistema alla frequenza freq
     *
     * SYSTEM       OPENFOLDER  {String}    Apre la cartella (win/linux)
     * SYSTEM       OPENURL     {String}    Apre l'URL (win/linux)
     * </pre>
     *
     * @param command Il comando
     */
    public static void parseCommand(String command) {
        command = command.replaceAll("\t", " ").trim();
        if (command.isEmpty()) {
            return;
        }
        String context = command.substring(0, command.indexOf(" ")).trim();
        command = command.substring(command.indexOf(" ")).trim();
        String action = command.substring(0, command.indexOf(" ")).trim();
        command = command.substring(command.indexOf(" ")).trim();

        // System.out.println("Context:" + context + ";");
        // System.out.println("Action:" + action + ";");
        // System.out.println("Command:" + command + ";");
        if (context.equals("KEYBOARD")) {
            if (action.equals("TYPE")) {
                Keyboard.Write(command);
            }
        }

        if (context.equals("MOUSE")) {
            if (action.equals("MOVETO")) {
                int x = Integer.parseInt(command.split(",")[0]);
                int y = Integer.parseInt(command.split(",")[1]);
                Mouse.setPosSmooth(x, y);
            }
            if (action.equals("MOVETOR")) {
                int x = Integer.parseInt(command.split(",")[0]);
                int y = Integer.parseInt(command.split(",")[1]);
                Mouse.setPos(x, y);
            }
            if (action.equals("MOVEBY")) {
                int x = Integer.parseInt(command.split(",")[0]);
                int y = Integer.parseInt(command.split(",")[1]);
                Mouse.setPosBy(x, y);
            }
            if (action.equals("CLICK")) {
                Mouse.click(command);
            }
            if (action.equals("WHEEL")) {
                Mouse.wheel(Integer.parseInt(command));
            }
        }

        if (context.equals("SYSTEM")) {
            if (action.equals("WAIT")) {
                SuperTimer.sleep(Integer.parseInt(command));
            }
            if (action.equals("WAITCHANGE")) {
                int x = Integer.parseInt(command.split(",")[0]);
                int y = Integer.parseInt(command.split(",")[1]);
                Color c = Mouse.getPixelColor(x, y);
                while (Mouse.getPixelColor(x, y).equals(c)) {
                    SuperTimer.sleep(100);
                }
            }
            if (action.equals("WAITCOLOR")) {
                int x = Integer.parseInt(command.split(",")[0]);
                int y = Integer.parseInt(command.split(",")[1]);
                int r = Integer.parseInt(command.split(",")[2]);
                int g = Integer.parseInt(command.split(",")[3]);
                int b = Integer.parseInt(command.split(",")[4]);
                Color c = new Color(r, g, b);

                while ((Mouse.getPixelColor(x, y).getRed() != c.getRed())
                        && (Mouse.getPixelColor(x, y).getGreen() != c.getGreen())
                        && (Mouse.getPixelColor(x, y).getBlue() != c.getBlue())) {
                    SuperTimer.sleep(100);
                }
            }

            if (action.equals("EXECUTE")) {
                execute("", command);
            }
            if (action.equals("START")) {
                execute("start", command);
            }
            if (action.equals("BEEP")) {
                Environment.Beep();
            }
            if (action.equals("OPENFOLDER")) {
                openFolder(command);
            }
            if (action.equals("OPENURL")) {
                executeUrl(command);
            }
        }

    }

}
