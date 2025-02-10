/*
 * Mouse.java
 *
 * Created on 20 settembre 2006, 15.00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.system;

import java.awt.*;
import java.awt.event.InputEvent;

/**
 * Questa classe consente di gestire il comportamento del Mouse
 *
 * @author <p style="color:red; font-family:verdana;">
 * <a href="mailto:a.addis@gmail.com">Andrea Addis</a> - &copy;<a
 * href="http://infodev.wordpress.com">Information Devices </a> </p>
 * @version 2005.07.12
 */
@SuppressWarnings("unused")
public class Mouse {

    public static final int BLEFT = InputEvent.BUTTON1_DOWN_MASK;
    public static final int BCENTER = InputEvent.BUTTON2_DOWN_MASK;
    public static final int BRIGHT = InputEvent.BUTTON3_DOWN_MASK;

    @SuppressWarnings("CallToPrintStackTrace")
    private static Robot getRobotInstance() {
        Robot r = null;
        try {
            r = new Robot();
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
        return r;
    }

    ///-------------------------------------------------------------------------
    ///-----------  Recupero e settaggio Posizione
    ///-------------------------------------------------------------------------

    /**
     * Prende la X del mouse nello schermo
     *
     * @return La X del mouse nello schermo
     */
    public static Point getPoint() {
        PointerInfo pInfo = java.awt.MouseInfo.getPointerInfo();
        return pInfo.getLocation();
    }

    /**
     * Prende la X del mouse nello schermo
     *
     * @return La X del mouse nello schermo
     */
    public static double getX() {
        PointerInfo pInfo = java.awt.MouseInfo.getPointerInfo();
        Point point = pInfo.getLocation();
        return point.getX();
    }

    /**
     * Prende la Y del mouse nello schermo
     *
     * @return La Y del mouse nello schermo
     */
    public static double getY() {
        PointerInfo pInfo = java.awt.MouseInfo.getPointerInfo();
        Point point = pInfo.getLocation();
        return point.getY();
    }

    /**
     * Imposta la posizione del Mouse sulle coordinate più vicine al punto X, Y
     *
     * @param x La coordinata X
     * @param y La coordinata Y
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static void setPos(int x, int y) {
        Robot r = null;
        try {
            r = new Robot();
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
        if (r != null) {
            r.mouseMove(x, y);
        }
    }

    public static void setPosBy(int dx, int dy) {
        Mouse.setPos((int) getX() + dx, (int) getY() + dy);
    }

    /**
     * Imposta la posizione del Mouse sulle coordinate più vicine al punto X, Y
     * in maniera "umana"
     *
     * @param x2 La coordinata X
     * @param y2 La coordinata Y
     */
    public static void setPosSmooth(int x2, int y2) {
        double x1 = Mouse.getX();
        double y1 = Mouse.getY();

        for (double i = 0; i < 100; i++) {
            SuperTimer.sleep(10);
            double xc = x1 + (((double) x2 - x1) / 100.0 * i) * Math.sin((i / 200.0) * Math.PI);
            double yc = y1 + (((double) y2 - y1) / 100.0 * i) * (-Math.cos((i / 100.0) * Math.PI));
            setPos((int) xc, (int) yc);
            // System.out.println(xc+","+yc);
        }
        setPos(x2, y2);    // Aggiustamento finale.
    }

    ///-------------------------------------------------------------------------
    ///-----------  Clicckaggio
    ///-------------------------------------------------------------------------

    /**
     * Simula la pressione o il rilascio di un pulsante del mouse. Nota, per
     * fare il doppio click � sufficiente fare un singolo click a distanza
     * ravvicinata (i.e. 50 millisecs)
     *
     * @param button Il bottone del mouse, si chiama con InputEvent.BUTTON... o
     *               con Mouse.BLeft ...
     * @param down   Indica se è Press (true) o Release (false)
     */
    public static void click(int button, boolean down) {
        Robot r = getRobotInstance();
        if (down) {
            r.mousePress(button);
        } else {
            r.mouseRelease(button);
        }
    }

    /**
     * Il comando è composto da 2 caratteri: {Operazione|Tasto} <br>
     * Operazione può essere: 1:=Singolo Click, 2:= Doppio click, D:Drag&Drop
     * <br>
     * Tasto può essere: L:=Tasto sinidtro, R:=Tasto Destro, C:=Tasto Centrale
     * <br>
     * Per il Drag&Drop bisogna aggiungere posizione di partenza e di arrivo nel
     * formato x,y#x,y.Esempi:
     * <ol>
     * <li>2L</li> Doppio click con il tasto sinistro
     * <li>1C</li> Click con il tasto centrale
     * <li>DL 120,50</li> Drag & Drop dalla posizione corrente a quella
     * indicata
     * </ol>
     * Il tempo tra due click del doubleclick è 50 millisecondi.
     *
     * @param sequenza La sequenza di click
     */
    public static void click(String sequenza) {
        char operation = sequenza.charAt(0);
        char tasto = sequenza.charAt(1);

        switch (tasto) {
            case 'L':
                tasto = InputEvent.BUTTON1_DOWN_MASK;
                break;
            case 'C':
                tasto = InputEvent.BUTTON2_DOWN_MASK;
                break;
            case 'R':
                tasto = InputEvent.BUTTON3_DOWN_MASK;
                break;
        }

        switch (operation) {
            case '1': {
                click(tasto, true);
                click(tasto, false);
                break;
            }
            case '2': {
                click(tasto, true);
                click(tasto, false);
                SuperTimer.sleep(50);
                click(tasto, true);
                click(tasto, false);
                break;
            }
            case 'D': {
                ///-{ Stabiliamo le coordinate
                sequenza = sequenza.substring(3);
                int x = Integer.parseInt(sequenza.split(",")[0]);
                int y = Integer.parseInt(sequenza.split(",")[1]);

                ///-{ Facciamo il Drag&Drop
                click(tasto, true);
                setPosSmooth(x, y);
                click(tasto, false);
                break;
            }
        }

    }

    public static void clickLeft() {
        Robot r = getRobotInstance();
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    /**
     * Ruota la rotella del mouse di "delta"
     *
     * @param delta Il delta di rotazione (positivo in basso, negativo in alto)
     */
    public static void wheel(int delta) {
        getRobotInstance().mouseWheel(delta);
    }

    ///-------------------------------------------------------------------------
    ///-----------  Funzioni avanzate
    ///-------------------------------------------------------------------------

    /**
     * Recupera il colore nella posizione x,y del mouse
     *
     * @param x la x del mouse
     * @param y la y del mouse
     * @return Restituisce il colore nella posizione x, y, del mouse
     */
    public static Color getPixelColor(int x, int y) {
        return getRobotInstance().getPixelColor(x, y);
    }

    // Indica se il mouse si trova in posizione 0,0 (utilizzato spesso come evento)
    public static boolean isMouse00() {
        return (getX() == 0 && getY() == 0);
    }

}
