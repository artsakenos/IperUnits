/*
 * Keyboard.java
 *
 * Created on 9 febbraio 2007, 18.12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.system;

import lombok.extern.java.Log;
import tk.artsakenos.iperunits.string.SuperString;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Consente di gestire una tastiera
 *
 * @author <p style="color:red; font-family:verdana;">
 * <a href="mailto:a.addis@gmail.com">Andrea Addis</a> - &copy;<a
 * href="http://infodev.wordpress.com">Information Devices </a> </p>
 * @version 2005.07.12
 */
@Log
@SuppressWarnings({"unused", "ExtractMethodRecommender"})
public class Keyboard {

    private static final Logger logger = Logger.getLogger(Keyboard.class.getName());

    public static final String KEYBOARD_CTRL_C = "[CONTROL]c[/CONTROL]";
    public static final String KEYBOARD_CTRL_V = "[CONTROL]v[/CONTROL]";
    public static final String KEYBOARD_NEW_BROWSER_TAB = "[CONTROL]t[/CONTROL]";
    public static final String KEYBOARD_CTRL_F4 = "[CONTROL][F4][/CONTROL]";

    private static Robot getRobotInstance() {
        Robot r = null;
        try {
            r = new Robot();
        } catch (AWTException ex) {
            log.severe(Keyboard.class + "::getRebootInstance() - " + ex.getLocalizedMessage());
        }
        return r;
    }

    ///-------------------------------------------------------------------------
    ///-----------  Clicckaggio e scrittura
    ///-------------------------------------------------------------------------

    /**
     * Simula la pressione o il rilascio di un tasto espresso mediante l'int VK
     *
     * @param keyEvent il Virtual Key (es: KeyEvent.VK_A)
     * @param down     se true è una pressione, altrimenti un rilascio
     * @see java.awt.event.KeyEvent
     */
    public static void Click(int keyEvent, boolean down) {
        try {
            Robot r = getRobotInstance();
            if (down) {
                r.keyPress(keyEvent);
            } else {
                r.keyRelease(keyEvent);
            }
        } catch (IllegalArgumentException ie) {
            System.err.println("[IperUnits Keyboard] Invalid key -" + ((char) keyEvent) + "-: " + keyEvent);
        }

    }

    /**
     * Simula la pressione e il rilascio di un tasto espresso mediante il suo VK
     *
     * @param keyEvent il Virtual Key (es: KeyEvent.VK_A)
     * @see java.awt.event.KeyEvent
     */
    public static void Click(int keyEvent) {
        Click(keyEvent, true);
        Click(keyEvent, false);
    }

    /**
     * Serve per le combinazioni semplici, per caratteri come !"£$%&/()=...
     *
     * @param keyEvent1 The KeyEvent 1
     * @param keyEvent2 The KeyEvent 2
     */
    public static void Click(int keyEvent1, int keyEvent2) {
        Click(keyEvent1, true);
        Click(keyEvent2, true);
        Click(keyEvent2, false);
        Click(keyEvent1, false);
    }

    /**
     * Simula la pressione e il rilascio di un tasto identificato dal suo
     * carattere corrispondente, non dal suo VirtualKey. Attenzione, vengono
     * stampati caratteri numeri e i simboli rappresentati in TastieraItaliana
     * (vedi printKeyboard()); Per essere sicuri della traduzione di un
     * carattere ascii utilizzare il metodo write.
     *
     * @param key Il carattere corrispondente al tasto da premere
     */
    public static void Click(char key) {
        int keyEvent = key;
        if ((key >= 'a') && (key <= 'z')) {
            keyEvent = keyEvent - 32;
        }
        Click(keyEvent);
    }

    /**
     * Schiaccia tutti i tasti relativi ai caratteri presenti nella stringa.
     * <br>
     * I caratteri di controllo vanno inseriti come [CONTROL] ... [/CONTROL].
     * <br>
     * Per quelli per i quali è prevista pressione e rilascio c'è un '/'. <br>
     * I caratteri disponibili sono:
     * <pre>
     * [/SHIFT], [/CONTROL], [/ALT]
     * [LEFT], [RIGHT], [ENTER], [BACK_SPACE], [DELETE], [SPACE]
     * [TAB], [CAPS_LOCK], [ESCAPE], [F*]
     * abcdefghijklmnopqrstuvwxyz != ABCDEFGHIJKLMNOPQRSTUVWXYZ
     *      I tasti maiuscoli vengono costruiti automaticamente con lo [/SHIFT]
     * 1234567890   ,-.\*+-./ <>'
     * </pre> Il tempo che intercorre tra la pressione di due tasti è 100
     * millisecondi
     *
     * @param text Il testo la cui scrittura va simulata
     */
    public static void Write(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            SuperTimer.sleep(100);

            ///-{ Traduzione dei tasti maiuscoli
            if ((c >= 'A') && (c <= 'Z')) {
                Click(KeyEvent.VK_SHIFT, c);
                continue;
            }

            ///-{ Traduzione dei tasti di controllo:
            //      [ALT], [SHIFT], [CTRL]
            if (c == '[') {
                StringBuilder control = new StringBuilder();
                boolean down = true;

                while (c != ']') {
                    control.append(c);
                    c = text.charAt(++i);
                }

                control = new StringBuilder(control.substring(1));
                if (control.charAt(0) == '/') {
                    down = false;
                    control = new StringBuilder(control.substring(1));
                }

                ///-{ Quelli con rilascio
                if (control.toString().equals("SHIFT")) {
                    Click(KeyEvent.VK_SHIFT, down);
                }
                if (control.toString().equals("CONTROL")) {
                    Click(KeyEvent.VK_CONTROL, down);
                }
                if (control.toString().equals("ALT")) {
                    Click(KeyEvent.VK_ALT, down);
                }

                ///-{ Quelli con pressione e rilascio
                if (control.toString().equals("LEFT")) {
                    Click(KeyEvent.VK_LEFT);
                }
                if (control.toString().equals("RIGHT")) {
                    Click(KeyEvent.VK_RIGHT);
                }
                if (control.toString().equals("UP")) {
                    Click(KeyEvent.VK_UP);
                }
                if (control.toString().equals("DOWN")) {
                    Click(KeyEvent.VK_DOWN);
                }
                if (control.toString().equals("BACK_SPACE")) {
                    Click(KeyEvent.VK_BACK_SPACE);
                }
                if (control.toString().equals("DELETE")) {
                    Click(KeyEvent.VK_DELETE);
                }
                if (control.toString().equals("ENTER")) {
                    Click(KeyEvent.VK_ENTER);
                }
                if (control.toString().equals("SPACE")) {
                    Click(KeyEvent.VK_SPACE);
                }

                if (control.toString().equals("TAB")) {
                    Click(KeyEvent.VK_TAB);
                }
                if (control.toString().equals("CAPS_LOCK")) {
                    Click(KeyEvent.VK_CAPS_LOCK);
                }
                if (control.toString().equals("ESCAPE")) {
                    Click(KeyEvent.VK_ESCAPE);
                }
                if (control.toString().startsWith("F") && SuperString.isNumber(control.substring(1))) {
                    int f = Integer.parseInt(control.substring(1));
                    Click(KeyEvent.VK_F1 - 1 + f);
                }

                continue;
            }
            ///-} I tasti di controllo sono stati tradotti

            HashMap<Character, Integer> charmap_single = new HashMap<>();
            charmap_single.put('\'', KeyEvent.VK_QUOTE);
            charmap_single.put('_', KeyEvent.VK_UNDERSCORE);
            charmap_single.put('+', KeyEvent.VK_PLUS);
            charmap_single.put('-', KeyEvent.VK_MINUS);
            charmap_single.put('.', KeyEvent.VK_PERIOD);
            charmap_single.put('\n', KeyEvent.VK_ENTER);
            // charmap_single.put(':', KeyEvent.VK_COLON);
            // charmap_single.put('!', KeyEvent.VK_EXCLAMATION_MARK);

            HashMap<Character, Integer> charmap_shift = new HashMap<>();
            charmap_shift.put('!', KeyEvent.VK_1);
            charmap_shift.put('"', KeyEvent.VK_2);
            charmap_shift.put('“', KeyEvent.VK_2);
            charmap_shift.put('”', KeyEvent.VK_2);
            charmap_shift.put('$', KeyEvent.VK_4);
            charmap_shift.put('%', KeyEvent.VK_5);
            charmap_shift.put('&', KeyEvent.VK_6);
            charmap_shift.put('/', KeyEvent.VK_7);
            charmap_shift.put('(', KeyEvent.VK_8);
            charmap_shift.put(')', KeyEvent.VK_9);
            charmap_shift.put('=', KeyEvent.VK_0);
            charmap_shift.put(':', KeyEvent.VK_PERIOD);
            charmap_shift.put(';', KeyEvent.VK_COMMA);
            charmap_shift.put('_', KeyEvent.VK_MINUS);

            HashMap<Character, Integer> charmap_grave = new HashMap<>();
            charmap_grave.put('à', KeyEvent.VK_A);
            charmap_grave.put('è', KeyEvent.VK_E);
            charmap_grave.put('é', KeyEvent.VK_E);

            if (charmap_single.containsKey(c)) {
                Click(charmap_single.get(c));
                continue;
            }
            if (charmap_shift.containsKey(c)) {
                Click(KeyEvent.VK_SHIFT, charmap_shift.get(c));
                continue;
            }
            if (charmap_grave.containsKey(c)) {
                Click(KeyEvent.VK_DEAD_GRAVE, true);
                Click(charmap_grave.get(c));
                Click(KeyEvent.VK_DEAD_GRAVE, false);
                continue;
            }

            if (c == '@') {
                logger.log(Level.WARNING, "Il carattere @ è problematico.");
                continue;
            }
            Click(c);
        }
    }

    ///-------------------------------------------------------------------------
    ///-----------  Estensioni utili
    ///-------------------------------------------------------------------------

    /**
     * Rappresenta i tasti stampabili, con l'* se si possono chiamare mediante
     * il metodo click.
     *
     * @param onlyValid Stampa solo i caratteri validi
     */
    public static void printKeyboard(boolean onlyValid) {
        String tastieraItaliana = ",-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ\\0123456789*+-./ <>'";

        System.out.println(" *** Rappresentazione della Keyboard *** validi:" + onlyValid);
        System.out.println("Tastiera Italiana: " + tastieraItaliana + "\r\n");

        for (int i = 0; i < 1000; i++) {
            String keyValid = "*";

            try {
                Keyboard.Click(i, false);
                // Keyboard.Click(i);   Se usi questo simula
                //                      pressione e rilascio e succede un casino
            } catch (IllegalArgumentException iae) {
                keyValid = "";
            }

            String key = SuperString.fixLength("Key:" + i + keyValid, 10);
            String keyText = SuperString.fixLength("Text:" + KeyEvent.getKeyText(i) + ";", 30);
            String keyModifier = SuperString.fixLength("Modifiers:" + InputEvent.getModifiersExText(i) + ";", 40);

            if (!keyValid.isEmpty() || !onlyValid) {
                System.out.println(key + keyText + keyModifier);
            }
        }

    }

    /**
     * Nota se si parte con le luci spente si visualizza una progress bar. Pi� o
     * meno il numero di times equivale al numero di secondi.
     *
     * @param times Il numero delle volte che le lucine si devono accendere
     */
    public static void progressLights(final int times) {
        new Thread(() -> {
            for (int i = 0; i < 6 * times; i++) {
                SuperTimer.sleep(60);
                int[] x = {144, 20, 145};
                Keyboard.Click(x[i % 3]);
            }
        }).start();
    }

}
