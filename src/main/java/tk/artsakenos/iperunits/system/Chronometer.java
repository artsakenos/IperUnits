/*
 * Chronometer.java
 *
 * Created on 21 febbraio 2007, 13.23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.system;

import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Un comodissimo cronometro per misurare le prestazioni delle vostre
 * applicazioni. Funziona senza l'utilizzo di thread.
 * <p>
 * Utilizzare le variabili internet total e step, anziché crearne di nuove.
 *
 * @author <p style="color:red; font-family:verdana;">
 * <a href="mailto:a.addis@gmail.com">Andrea Addis</a> - &copy;<a
 * href="http://infodev.wordpress.com">Information Devices </a> </p>
 * @version 2019.01.01
 */
@SuppressWarnings("unused")
public class Chronometer {

    private long timeBegin = 0;
    private long timeTotal = 0;

    /**
     * Dice se il cronometro sta andando.
     */
    @Getter
    private boolean active = false;

    public int total = 0;
    public int step = 0;

    public Chronometer() {
    }

    public Chronometer(int total) {
        this.total = total;
    }

    private long now() {
        return Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
    }

    /**
     * Fa partire il cronometro.
     */
    public void start() {
        timeBegin = now();
        active = true;
    }

    /**
     * Mette in pausa il cronometro. Si può far ripartire con start()
     * continuando dal time elapsed totalizzato.
     */
    public void pause() {
        timeTotal = timeTotal + now() - timeBegin;
        active = false;
    }

    /**
     * Resetta il cronometro, bloccandolo e azzerando il tempo.
     */
    public void reset() {
        timeTotal = 0;
        active = false;
    }

    /**
     * Resituisce il tempo in millisecondi.
     *
     * @return Il tempo in millisecondi
     */
    public long getTimePassedMillisecs() {
        if (active) {
            return timeTotal + now() - timeBegin;
        } else {
            return timeTotal;
        }
    }

    /**
     * Restituisce il tempo cronometrato in formato HH:mm:ss:SSS
     *
     * @param header l'header
     * @return Il tempo passato.
     */
    public String getTimePassed(String header) {
        Date myDate = new Date(getTimePassedMillisecs());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC+8"));
        String header_compiled = header.isEmpty() ? "" : "[Chrono " + header + "] ";
        return header_compiled + dateFormat.format(myDate);
    }

    /**
     * Restituisce un ETA in HH:MM:SS.
     *
     * @param currentStep Lo step corrente.
     * @param stepsCount  Il totale degli step.
     * @return l'ETA
     */
    public String getETA(int currentStep, int stepsCount) {
        long eta = getTimePassedMillisecs() / currentStep * (stepsCount - currentStep);
        return String.format("%02dh:%02dm:%02ds",
                TimeUnit.MILLISECONDS.toHours(eta),
                TimeUnit.MILLISECONDS.toMinutes(eta) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(eta)),
                TimeUnit.MILLISECONDS.toSeconds(eta) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(eta)));
    }

    /**
     * Restituisce l'ETA con informazioni sugli steps, nel formato:
     * <p>
     * Step count/total: HH:MM:SS
     *
     * @param addStep Indica se deve incrementare gli step.
     * @return l'ETA extended.
     */
    public String getETAX(boolean addStep) {
        if (addStep) {
            step++;
        }
        return "Step " + step + "/" + total + ": " + getETA(step, total);
    }

    /**
     * Restituisce una rappresentazione testuale dello stato attuale Start, Now,
     * Chronometer + millisecs.
     *
     * @return Rappresentazione testuale del chronometer
     */
    @Override
    public String toString() {
        Date myDate = new Date(getTimePassedMillisecs() - 3600 * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        String timeChrono = dateFormat.format(myDate);

        String timeStart = Chronometer.dateToTimeCounterString(timeBegin);
        String timeNow = Chronometer.dateToTimeCounterString(now());
        String timeMilliseconds = "" + getTimePassedMillisecs();

        return "ID Chronometer - Start (" + timeStart + "), Now ("
               + timeNow + "), Chronometer (" + timeChrono + " - " + timeMilliseconds + ")";
    }
    //----------------------------------------------------------------------------------------------
    //------------  Utilities per eventuali counters
    //----------------------------------------------------------------------------------------------

    /**
     * Metodo statico per la conversione di una data "Contatore" in stringa.
     *
     * @param myTime Il cronometraggio in formato Date
     * @return Il cronometraggio in formato HH:mm:ss:SSS
     */
    public static String dateToTimeCounterString(Date myTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        return dateFormat.format(new Date(myTime.getTime() - 3600 * 1000));
    }

    /**
     * Metodo statico per la conversione di un long "Contatore" in stringa.
     *
     * @param myTime Il cronometraggio in formato long.
     * @return Il cronometraggio in formato HH:mm:ss:SSS
     */
    public static String dateToTimeCounterString(long myTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        return dateFormat.format(new Date(myTime - 3600 * 1000));
    }

}
