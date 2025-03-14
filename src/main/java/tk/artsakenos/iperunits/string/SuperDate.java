package tk.artsakenos.iperunits.string;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Questa classe fornisce metodi innoveativi per l'utilizzo delle date.
 *
 * @author Andrea, 11 marzo 2005, 13.02
 * @version 2019.03.01
 */
public class SuperDate {

    public final static String SD_DATABASE = "yyyy-MM-dd HH:mm:ss";
    public final static String SD_ITALIAN = "dd/MM/yyyy HH:mm:ss";
    public final static String SD_AMERICAN = "MM/dd/yyyy HH:mm:ss";

    //--------------------------------------------------------------------------
    //------------  Date Utility
    //--------------------------------------------------------------------------

    /**
     * Restituisc la Unix Epoch corrente
     *
     * @return la Unix Epoch corrente.
     */
    public static long now() {
        return Calendar.getInstance().getTimeInMillis();
    }

    /**
     * Restituisce la data e/o l'ora corrente in base al formato. e.g.,
     * yyyy-MM-dd HH:mm:ss
     *
     * @param dateFormat Il formato
     * @return la data e/o l'ora corrente in base al formto scelto.
     */
    public static String now(String dateFormat) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return sdf.format(cal.getTime());
    }

    /**
     * Mettere a 0 hour_24 se non si vogliono inserire informazioni sull'ora.
     *
     * @param year    The year
     * @param month   The month
     * @param day     The day
     * @param hour_24 The Hour_24
     * @param minute  The minute
     * @param seconds The seconds
     * @return Restituisce una data preimpostata
     */
    public static Date getDate(int year, int month, int day, int hour_24, int minute, int seconds) {
        Calendar c = Calendar.getInstance();
        if (hour_24 == 0) {
            c.set(year, month, day);
        } else {
            c.set(year, month, day, hour_24, minute, seconds);
        }
        return c.getTime();
    }

    /**
     * Mostra l'elapsed time nel formato 23h:34m:45s discorsivo
     *
     * @param time il time in Epoch
     * @return l'elapsed time
     */
    public static String elapsed(long time) {
        long days = TimeUnit.MILLISECONDS.toDays(time);
        long hours = TimeUnit.MILLISECONDS.toHours(time);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));

        if (days != 0) {
            hours = hours % 24;
            return String.format(Locale.getDefault(), "%d Days, %02dh:%02dm", days, hours, minutes);
        }

        if (hours != 0) {
            return String.format(Locale.getDefault(), "%02dh:%02dm:%02ds", hours, minutes, seconds);
        }

        long milliseconds = TimeUnit.MILLISECONDS.toMillis(time) % 1000;
        return String.format(Locale.getDefault(), "%02dm:%02ds.%03d", minutes, seconds, milliseconds);
    }

    /**
     * Mostra l'elapsed time in mm:ss.
     *
     * @param initialTime Il tempo di start
     * @return l'elapsed time
     */
    public static String elapsed_mmss(long initialTime) {
        SimpleDateFormat sdf_minsec = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return sdf_minsec.format(Calendar.getInstance().getTimeInMillis() - initialTime);
    }

    //--------------------------------------------------------------------------
    //------------  get_date_italian
    //--------------------------------------------------------------------------

    /**
     * Restituisce la data corrente nel formato [dd/MM/yyyy]
     *
     * @return Stringa contenente la data formattata come [dd/MM/yyyy]
     */
    public static String get_date_italian() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("[dd/MM/yyyy]", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    /**
     * Restituisce la data corrente nel formato [GG/MM/AAAA OO:MM:ss]
     *
     * @return Stringa contenente la data formattata come [GG/MM/AAAA OO:MM:ss]
     */
    public static String get_datetime_italian() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("[" + SD_ITALIAN + "]", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    /**
     * Restituisce il current time nel formato HHmmssSSS. Utilizzabile per
     * rendere unici i nomi di entita' create su di un sistema in momenti
     * differenti
     *
     * @return Restituisce il current time nel formato HHmmssSSS
     */
    public static String get_date_simple() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmssSSS", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    /**
     * Restituisce il current time nel formato yyyyMMdd-HHmmssSSS Utilizzabile
     * per rendere unici i nomi di entità create su di un sistema in momenti
     * differenti o file.
     *
     * @return Restituisce il current time nel formato
     * <i>yyyyMMdd-HHmmssSSS</i>.
     */
    public static String get_datetime_simple() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmssSSS", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    /**
     * Ora in formato SQL: yyyy-MM-dd HH:mm:ss.
     *
     * @return L'ora in formato SQL.
     */
    public static String get_datetime_sql() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    /**
     * Ora in formato SQL: yyyy-MM-dd.
     *
     * @return L'ora in formato SQL.
     */
    public static String get_date_sql() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    /**
     * Data ora attuale in ISO 8601 (utile per il dynamic mapping in Elastic
     * Search).
     *
     * @return una stringa ISO 8601
     */
    public static String get_datetime_iso8601() {
        return Instant.ofEpochMilli(now()).toString();
    }

    /**
     * Data ora in ISO 8601 (utile per il dynamic mapping in Elastic Search).
     *
     * @param epoch_time epoch_time
     * @return una stringa ISO 8601
     */
    public static String get_datetime_iso8601(long epoch_time) {
        return Instant.ofEpochMilli(epoch_time).toString();
    }

    //----------------------------------------------------------------------------------------------
    //------------  Conversioni da String a Date
    //----------------------------------------------------------------------------------------------

    /**
     * Trasforma la stringa in data dal formato: dd/MM/yyyy HH:mm:ss
     *
     * @param StringDate la data nel formato dd/MM/yyyy HH:mm:ss
     * @return La data
     */
    public static Date StringItalianToDate(String StringDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SD_ITALIAN, Locale.getDefault());
        try {
            return dateFormat.parse(StringDate);
        } catch (ParseException ex) {
            return null;
        }
    }

    /**
     * Trasforma la stringa in data dal formato AMERICANO: MM/dd/yyyy HH:mm:ss
     *
     * @param StringDate la data nel formato AMERICANO MM/dd/yyyy HH:mm:ss
     * @return La data
     */
    public static Date StringAmericanToDate(String StringDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SD_AMERICAN, Locale.getDefault());
        try {
            return dateFormat.parse(StringDate);
        } catch (ParseException ex) {
            return null;
        }
    }

    /**
     * Trasforma la stringa in data dal formato SQL Postgres: yyyy-MM-dd
     * HH:mm:ss
     *
     * @param StringDate la data nel formato POSTGRES yyyy-MM-dd HH:mm:ss
     * @return La data
     */
    public static Date StringDatabaseToDate(String StringDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SD_DATABASE, Locale.getDefault());
        try {
            return dateFormat.parse(StringDate);
        } catch (ParseException ex) {
            return null;
        }
    }

    //----------------------------------------------------------------------------------------------
    //------------  Conversioni da Date a String
    //----------------------------------------------------------------------------------------------

    /**
     * Trasforma la data in stringa nel formato: dd/MM/yyyy HH:mm:ss
     *
     * @param myDate la Data
     * @return la stringa nel formato: dd/MM/yyyy HH:mm:ss
     */
    public static String DateToStringItalian(Date myDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SD_ITALIAN, Locale.getDefault());
        return dateFormat.format(new Date(myDate.getTime()));
    }

    /**
     * Trasforma la data in stringa nel formato AMERICANO x SQL: MM/dd/yyyy
     * HH:mm:ss per il maledetto standard americano bozzato negli access db
     *
     * @param myDate la Data
     * @return la stringa nel formato AMERICANO x SQL: MM/dd/yyyy HH:mm:ss
     */
    public static String DateToStringAmericano(Date myDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SD_AMERICAN, Locale.getDefault());
        return dateFormat.format(new Date(myDate.getTime()));
    }

    /**
     * Trasforma la data in stringa nel formato SQL: MM/dd/yyyy HH:mm:ss per il
     * maledetto standard americano bozzato negli access db
     *
     * @param myDate la Data
     * @return la stringa nel formato AMERICANO x SQL: MM/dd/yyyy HH:mm:ss
     */
    public static String DateToStringDatabase(Date myDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SD_DATABASE, Locale.getDefault());
        return dateFormat.format(new Date(myDate.getTime()));
    }
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
}
