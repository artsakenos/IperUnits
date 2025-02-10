/*
 * SuperTimer.java
 *
 * Created on 14 febbraio 2007, 20.03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.system;

import lombok.Getter;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Il Super Timer della Informatic Devices. Tieni conto del fatto che terminato
 * il Timer termina il processo, quindi se lo vuoi far ripartire l'applicazione
 * non deve essere uscita.
 *
 * @author <p style="color:red; font-family:verdana;">
 * <a href="mailto:a.addis@gmail.com">Andrea Addis</a> - &copy;<a
 * href="http://infodev.wordpress.com">Information Devices </a> </p>
 * @version 2005.07.12
 */
@SuppressWarnings("unused")
public abstract class SuperTimer {

    private abstract static class DelayedTimer extends TimerTask {

        @Override
        public synchronized void run() {
            doTimerTask();
        }

        public abstract void doTimerTask();
    }

    private Timer t = new Timer();

    /**
     * Indica se il timer è tutt'ora attivo
     */
    @Getter
    private boolean active = false;
    private long timerDelay = 0;
    /**
     * Contiene l'oggetto che ha istanziato il Timer
     */
    @Getter
    private final Object parent;
    private Date timeStart = null;

    /**
     * Istanzia il Timer, bisogna passare l'oggetto che lo ha istanziato
     *
     * @param parent L'oggetto che ha istanziato il timer
     */
    public SuperTimer(Object parent) {
        this.parent = parent;
    }

    /**
     * Istanzia il Timer, bisogna passare l'oggetto che lo ha istanziato
     */
    public SuperTimer() {
        this(null);
    }

    public long getDelay() {
        return timerDelay;
    }

    public void onStart() {
    }

    public void onStop() {
    }

    /**
     * Fa partire il timer
     *
     * @param millisecDelay I millisecondi che intercorrono tra una chiamata e
     *                      un'altra
     */
    public void start(long millisecDelay) {
        // t = new Timer();
        onStart();
        DelayedTimer newTimerTask = new DelayedTimer() {

            @Override
            public synchronized void doTimerTask() {
                doTimer();
            }
        };

        timeStart = Calendar.getInstance().getTime();
        try {
            t.schedule(newTimerTask, timeStart, millisecDelay);
            timerDelay = millisecDelay;
        } catch (IllegalStateException ise) {
            t = new Timer();
            t.schedule(newTimerTask, timeStart, millisecDelay);
        }
        active = true;
    }

    /**
     * Blocca il timer
     */
    public void stop() {
        t.purge();
        t.cancel();
        active = false;
        onStop();
    }

    /**
     * Il tempo passato dall'ultimo avvio del timer
     *
     * @return Il tempo passato dall'ultimo avvio del timer espresso in
     * millisecondi
     */
    public long getTimePassed() {
        return Calendar.getInstance().getTimeInMillis() - timeStart.getTime();
    }

    /**
     * E' il metodo astratto che bisogna estendere per implementare l'azione che
     * va intrapresa durante il trigger del timer.
     */
    public abstract void doTimer();

    public static boolean checkExpired(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        int n_day = cal.get(Calendar.DAY_OF_MONTH);
        int n_month = cal.get(Calendar.MONTH) + 1;
        int n_year = cal.get(Calendar.YEAR);
        if (n_year > year) {
            return true;
        }
        if (n_month > month) {
            return true;
        }
        return n_day > day;
    }

    public static void sleep(long millisecs) {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException ex) {
            Logger.getLogger(SuperTimer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
