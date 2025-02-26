/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.system;

import java.awt.*;

import static tk.artsakenos.iperunits.system.SuperExecutor.*;

/**
 * La classe Human, si propone di simulare la presenza di un utente alla
 * console tramite un linguaggio di Script.
 *
 * @author <p style="color:red; font-family:verdana;">
 * <a href="mailto:a.addis@gmail.com">Andrea Addis</a> - &copy;<a
 * href="http://infodev.wordpress.com">Information Devices </a> </p>
 * @version 2005.07.12
 */
@SuppressWarnings("unused")
public class Human {

    private static final String KEYBOARD = "KEYBOARD";
    private static final String MOUSE = "MOUSE";
    private static final String SYSTEM = "SYSTEM";
    private static final String CONDITION = "CONDITION";

    /**
     * Vedi il file <a href="./Readme.md">Readme.md</a>.
     *
     * @param command Il comando
     */
    public static void parseCommand(String command) {
        command = command.replaceAll("\t", " ").trim();
        if (command.isEmpty()) {
            return;
        }

        // Manteniamo il tuo metodo di parsing originale
        String context = command.substring(0, command.indexOf(" ")).trim();
        command = command.substring(command.indexOf(" ")).trim();
        String action = command.substring(0, command.indexOf(" ")).trim();
        command = command.substring(command.indexOf(" ")).trim();

        // Esecuzione dei comandi per contesto
        switch (context) {
            case KEYBOARD:
                executeKeyboardCommand(action, command);
                break;
            case MOUSE:
                executeMouseCommand(action, command);
                break;
            case SYSTEM:
                executeSystemCommand(action, command);
                break;
            case CONDITION:
                checkCondition(action, command);
                break;
        }
    }


    private static Point cMousePosition = null;
    private static Color cMouseColor = null;

    private static boolean checkCondition(String action, String command) {
        if (action.equals("MOUSE") && command.equals("MOVED")) {
            if (cMousePosition != null && !cMousePosition.equals(Mouse.getPoint())) {
                return true;
            }
        }
        if (action.equals("MOUSE") && command.equals("00")) {
            if (Mouse.isMouse00()) return true;
        }
        if (action.equals("MOUSE") && command.equals("PIXEL_COLOR_CHANGED")) {
            if (cMouseColor != null && !cMouseColor.equals(Mouse.getPixelColor(cMousePosition.x, cMousePosition.y))) {
                return true;
            }
        }
        cMousePosition = Mouse.getPoint();
        cMouseColor = Mouse.getPixelColor(cMousePosition.x, cMousePosition.y);
        return false;
    }

    // Metodi separati per ogni contesto - semplice miglioramento organizzativo
    private static void executeKeyboardCommand(String action, String command) {
        if (action.equals("TYPE")) {
            Keyboard.Write(command);
        }
    }

    private static void executeMouseCommand(String action, String command) {
        switch (action) {
            case "MOVETO" -> {
                int x = Integer.parseInt(command.split(",")[0]);
                int y = Integer.parseInt(command.split(",")[1]);
                Mouse.setPosSmooth(x, y);
            }
            case "MOVETOR" -> {
                int x = Integer.parseInt(command.split(",")[0]);
                int y = Integer.parseInt(command.split(",")[1]);
                Mouse.setPos(x, y);
            }
            case "MOVEBY" -> {
                int x = Integer.parseInt(command.split(",")[0]);
                int y = Integer.parseInt(command.split(",")[1]);
                Mouse.setPosBy(x, y);
            }
            case "CLICK" -> Mouse.click(command);
            case "WHEEL" -> Mouse.wheel(Integer.parseInt(command));
        }
    }

    private static void executeSystemCommand(String action, String command) {
        switch (action) {
            case "WAIT" -> SuperTimer.sleep(Integer.parseInt(command));
            case "WAITCHANGE" -> {
                int x = Integer.parseInt(command.split(",")[0]);
                int y = Integer.parseInt(command.split(",")[1]);
                Color c = Mouse.getPixelColor(x, y);
                while (Mouse.getPixelColor(x, y).equals(c)) {
                    SuperTimer.sleep(100);
                }
            }
            case "WAITCOLOR" -> {
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
            case "EXECUTE" -> execute("", command);
            case "START" -> execute("start", command);
            case "BEEP" -> Environment.Beep();
            case "OPENFOLDER" -> openFolder(command);
            case "OPENURL" -> executeUrl(command);
        }
    }

    /**
     * Esegue uno script.
     *
     * @param script    Lo script
     * @param loopDelay Il ritardo tra un ciclo e l'altro. Se loopDelay==-1 effettua solo un ciclo.
     */
    public static void parseScript(String script, long loopDelay) {
        String[] lines = script.replaceAll("\r", "").split("\n");
        if (loopDelay == -1) {
            for (String line : lines) {
                parseCommand(line);
            }
        }
        boolean running = true;
        while (running) {
            for (String line : lines) {
                parseCommand(line);
            }
            SuperTimer.sleep(loopDelay);

        }
    }

}
