/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.file;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static tk.artsakenos.iperunits.file.SuperFileText.append;

/**
 * @author Andrea - 2018.01.01
 * @version 2018.12.17
 */
@Deprecated
public class SuperLog {

    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
    public static String SUPERLOG_FILE_NAME = "SuperLog.log";

    /**
     * @param header l'header
     * @param log    il log
     * @return il log
     * @see #log(java.lang.String, java.lang.String, boolean). Senza scrittura
     * su file.
     */
    public static String log(String header, String log) {
        return log(header, log, false);
    }

    /**
     * Log di un'attività. Normalmente si inserisce un header che identifica
     * dove è stato chiamato il log. Un log è una linea, va accapo
     * automaticamente nel file e a console.
     *
     * @param header         l'header
     * @param log            il log
     * @param append_to_file se appendere a file (viene inserito \n)
     * @return il log (non viene aggiunto \n!)
     */
    public static String log(String header, String log, boolean append_to_file) {
        // Logger.getLogger("LocalLogger").log(Level.INFO, "[{0}] {1}", new Object[]{header, log});
        String date = DATE_FORMAT.format(Calendar.getInstance(Locale.getDefault()).getTime());
        String text = String.format("%s [%s] %s", date, header, log);
        System.out.println(text);
        if (append_to_file) {
            append(SUPERLOG_FILE_NAME, text + "\n");
        }
        return text;
    }

    /**
     * Mostra l'output dell'errore, secondo lo standard UltraUnits
     * ERROR([ownerClassName].[exceptionClassName], [errorMessage]);
     * {[personalMessage]}
     *
     * @param ownerClass      La classe di appartenenza
     * @param ex              L'eccezione lanciata
     * @param personalMessage Un messaggio personale aggiunto ad esempio
     *                        "metodo(parametri):messaggio"
     * @return Una stringa che descrive l'errore
     */
    public static String error(Class ownerClass, Exception ex, String personalMessage) {
        String ownerClassName = "_";
        String exceptionClassName = "Internal";
        if (ownerClass != null) {
            ownerClassName = ownerClass.getName();
        }
        String errorMessage = "";
        if (ex != null) {
            exceptionClassName = ex.getClass().toString();
            errorMessage = ex.getLocalizedMessage();
        }

        String error = "ERROR(" + exceptionClassName + "@" + ownerClassName
                + "," + errorMessage + "); "
                + "{" + personalMessage + "}";
        System.err.println(error);
        return error;
    }

}
