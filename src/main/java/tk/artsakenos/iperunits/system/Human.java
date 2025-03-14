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

    private static final String STOP_CONDITION = "STOP";
    private Point cMousePosition = null;
    private Color cMouseColor = null;

    /**
     * Indica il numero del loop corrente per lo script
     */
    private int cLoopsProcessed = 0;

    /**
     * Se lo script é in loop, basta stopLoop() per fermarlo.
     */
    private boolean cLoopRunning = false;

    /**
     * Ferma il loop se in corso.
     */
    public void stopLoop() {
        cLoopRunning = false;
    }

    private Point getPoint(String command) {
        int x = Integer.parseInt(command.split(",")[0].trim());
        int y = Integer.parseInt(command.split(",")[1].trim());
        return new Point(x, y);
    }

    /**
     * Indica il numero della linea in process
     */
    private int cLineProcessing = 0;

    /**
     * Indica il numero delle linee totali nello script
     */
    private int cLoopTotalLines = 0;

    /**
     * Estendi l'evento all'occorrenza.
     *
     * @param commandResult The Command Result
     */
    public void onCommandParsed(
            String command, String commandResult,
            int cLoopsProcessed, int cLineProcessing, int cLoopTotalLines) {
    }

    /**
     * Vedi il file <a href="./Readme.md">Readme.md</a>.
     *
     * @param command Il comando
     * @return Una stringa vuota se tutto OK, un messaggio altrimenti
     */
    public String parseCommand(String command) {
        ++cLineProcessing;
        command = command.replaceAll("\t", " ").trim();
        if (command.isEmpty() || command.startsWith("#")) {
            return "";
        }

        // Manteniamo il tuo metodo di parsing originale
        String context = command.substring(0, command.indexOf(" ")).trim();
        command = command.substring(command.indexOf(" ")).trim();
        String action = command.substring(0, command.indexOf(" ")).trim();
        command = command.substring(command.indexOf(" ")).trim();

        String commandResult = "";

        // Esecuzione dei comandi per contesto
        switch (context) {
            case "KEYBOARD":
                executeKeyboardCommand(action, command);
                break;
            case "MOUSE":
                executeMouseCommand(action, command);
                break;
            case "SYSTEM":
                executeSystemCommand(action, command);
                break;
            case "CONDITION":
                commandResult = checkCondition(action, command);
        }
        onCommandParsed(command, commandResult, cLoopsProcessed, cLineProcessing, cLoopTotalLines);
        return commandResult;
    }

    private String checkCondition(String action, String command) {
        if (action.equals("MOUSE")) {
            if (command.equals("MOVED")
                && cMousePosition != null
                && !cMousePosition.equals(Mouse.getPoint())) {
                return STOP_CONDITION;
            }
            if (command.equals("00") && Mouse.isMouse00()) return STOP_CONDITION;

            if (command.equals("PIXEL_COLOR_CHANGED")
                && cMouseColor != null
                && !cMouseColor.equals(Mouse.getPixelColor(cMousePosition.x, cMousePosition.y))) {
                return STOP_CONDITION;
            }
        }
        cMousePosition = Mouse.getPoint();
        cMouseColor = Mouse.getPixelColor(cMousePosition.x, cMousePosition.y);
        cLoopsProcessed++;
        return "";
    }

    private void executeKeyboardCommand(String action, String command) {
        if (action.equals("TYPE")) {
            Keyboard.Write(command);
        }
    }

    private void executeMouseCommand(String action, String command) {
        switch (action) {
            case "MOVETO" -> {
                Point mp = getPoint(command);
                Mouse.setPosSmooth(mp.x, mp.y);
            }
            case "MOVETOR" -> {
                Point mp = getPoint(command);
                Mouse.setPos(mp.x, mp.y);
            }
            case "MOVEBY" -> {
                Point mp = getPoint(command);
                Mouse.setPosBy(mp.x, mp.y);
            }
            case "CLICK" -> Mouse.click(command);
            case "WHEEL" -> Mouse.wheel(Integer.parseInt(command));
        }
    }

    private void executeSystemCommand(String action, String command) {
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
    public void parseScript(String script, long loopDelay) {
        String[] lines = script.replaceAll("\r", "").split("\n");
        cLoopTotalLines = lines.length;
        cLoopsProcessed = 0;
        if (loopDelay == -1) {
            for (String line : lines) {
                parseCommand(line);
            }
            return;
        }

        new Thread(() -> {
            cLoopRunning = true;
            do {
                cLineProcessing = 0;
                ++cLoopsProcessed;
                for (String line : lines) {
                    cLoopRunning &= !parseCommand(line).equals(STOP_CONDITION);
                }
                SuperTimer.sleep(loopDelay);
            } while (cLoopRunning);
        }).start();

    }

}
