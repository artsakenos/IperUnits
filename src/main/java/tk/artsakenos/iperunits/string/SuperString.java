package tk.artsakenos.iperunits.string;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;

/**
 * Questa classe fornisce metodi innovativi per l'utilizzo delle stringhe.
 *
 * @author <p style="color:red; font-family:verdana;">
 * <a href="mailto:a.addis@gmail.com">Andrea Addis</a> - &copy;<a
 * href="http://infodev.wordpress.com">Information Devices </a> </p>
 * @version 2005.07.12
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class SuperString {

    ///-----------------------------------------------------------------------------
    ///------------------
    ///-----------------------------------------------------------------------------

    /**
     * Says if a String corresponds to an Integer/Long value
     *
     * @param s The string to check
     * @return true if it is an Integer
     */
    public static boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Fissa la lunghezza di una stringa, aggiungendo spazi alla fine o
     * tagliandola
     *
     * @param text   La stringa di cui fissare la lunghezza
     * @param length la lunghezza
     * @return La stringa con la nuova lunghezza
     */
    public static String fixLength(String text, int length) {
        return String.format("%-" + length + "." + length + "s", text);
    }

    /**
     * Taglia il text se la sua lunghezza massima supera length. Rimuove gli
     * accapi.
     *
     * @param text   Il testo
     * @param length la lunghezza massima.
     * @return Il testo eventualmente tagliato
     */
    public static String capLength(String text, int length) {
        if (text == null) {
            return "";
        }
        text = text.trim().replaceAll("\r", "").replaceAll("\n", " ");
        if (text.length() > length) {
            return text.substring(0, length);
        }
        return text;
    }

    /**
     * Fissa la lunghezza di un numero.
     *
     * @param number The Number
     * @param length la lunghezza totale
     * @return Il numero in String con i leading 0
     */
    public static String fixLength(int number, int length) {
        return String.format("%0" + length + "d", number);
    }

    /**
     * Fissa la lunghezza della stringa, allineando text a destra.
     *
     * @param text   Il text di cui fissare la lunghezza.
     * @param length La lunghezza.
     * @return La stringa con la lunghezza fixata.
     */
    public static String fixLengthRight(String text, int length) {
        if (text.length() > length) {
            return text.substring(text.length() - length);
        }
        return String.format("%" + length + "." + length + "s", text);
    }

    /**
     * Fissa la dimensione del double sulla stringa
     *
     * @param format Il formato espresso in: (lunghezza a disposizione).(numero
     *               dei decimali) es: 8.4
     * @param d      Il numero double
     * @return La stringa desiderata
     */
    public static String fixDouble(String format, double d) {
        return String.format(Locale.ENGLISH, "%" + format + "f", d);
    }

    /**
     * Nome con la maiuscola all'inizio (solo la prima lettera!).
     *
     * @param name The Proper Name
     * @return Restituisce il Name con la maiuscola all'inizio (solo la prima!).
     */
    public static String fixName(String name) {
        if (name == null) {
            return null;
        }
        name = name.trim();
        if (name.isEmpty()) {
            return "";
        }
        if (name.length() == 1) {
            return name.toUpperCase();
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    /**
     * Restituisce una rappresentazione in magnitude del numero. Ad esempio
     * 10,000 diventa 10K. 12345678 diventa 12M.
     *
     * @param number Il numero
     * @return La magnetudinizzazione del numero.
     */
    public static String fixNumberMagnitude(long number) {
        if (number < 10_000) {
            return number + "";
        }
        number /= 1000;
        if (number < 10_000) {
            return number + "K";
        }
        number /= 1000;
        if (number < 10_000) {
            return number + "M";
        }
        number /= 1000;
        return number + "G";
    }

    ///-----------------------------------------------------------------------------
    ///------------------   Convertitori Stringatari
    ///-----------------------------------------------------------------------------

    /**
     * Una rappresentazione su più linee dell'HashMap nel formato "key=value"
     *
     * @param hashmap Una HashMap
     * @return Una rappresentazione formato testo dell'HashMap
     */
    @SuppressWarnings("rawtypes")
    public static String toStringHashMap(HashMap hashmap) {
        String output = "";
        if (hashmap == null) {
            return output;
        }
        for (Object key : hashmap.keySet()) {
            Object value = hashmap.get(key);
            output = String.format("%s%s=%s\n", output, key.toString(), value);
        }
        return output;
    }

    /**
     * Converte un InputStream in un String
     *
     * @param is l'InputStream
     * @return la stringa corrispondente
     */
    public static String toStringStream(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try (is) {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            // error(SuperString.class, e, line);
        }
        // error(SuperString.class, e, line);

        return sb.toString();
    }

    /**
     * Fixa il format del double a 3.2
     *
     * @param value The Value
     * @return the fixed formatted value
     */
    public static String df32(double value) {
        return String.format(Locale.getDefault(), "%06.2f", value);
    }

    /**
     * Fixa il format del double a 4.0
     *
     * @param value The Value
     * @return the fixed formatted value
     */
    public static String df40(double value) {
        return String.format(Locale.getDefault(), "%04.0f", value);
    }

    /**
     * Fixa il format del double a 4.2
     *
     * @param value The Value
     * @return the fixed formatted value
     */
    public static String df42(double value) {
        return String.format(Locale.getDefault(), "%07.2f", value);
    }

    /**
     * Fixa il format del double a 4.4
     *
     * @param value The Value
     * @return the fixed formatted value
     */
    public static String df44(double value) {
        return String.format(Locale.getDefault(), "%09.4f", value);
    }

    /**
     * Fixxa il format delle values a 4.2.
     *
     * @param values The values
     * @return the fixed formatted values
     */
    public static String toStringValues(float[] values) {
        StringBuilder output = new StringBuilder();
        for (float value : values) {
            output.append(df42(value)).append("; ");
        }
        return output.toString();
    }

    /**
     * Fixxa il format delle values a 4.0.
     *
     * @param values The values
     * @return the fixed formatted values
     */
    public static String toStringValues40(float[] values) {
        StringBuilder output = new StringBuilder();
        for (float value : values) {
            output.append(df40(value)).append("; ");
        }
        return output.toString().trim();
    }

    /**
     * Restituisce una formatted 4.2 somma delle values.
     *
     * @param values The values
     * @return The formatted sum
     */
    public static String toStringValuesSum(float[] values) {
        return df42(sumValues(values));
    }

    /**
     * Restituisce una somma delle values
     *
     * @param values The values
     * @return the sum of the values
     */
    public static double sumValues(float[] values) {
        double total = 0;
        for (double value : values) {
            total += value * value;
        }
        return Math.sqrt(total);
    }

    ///-----------------------------------------------------------------------------
    ///------------------   Utilities
    ///-----------------------------------------------------------------------------

    /**
     * Indica se gli elementi del pattern sono contenuti nella stringa
     *
     * @param string  La String in ingresso
     * @param pattern Gli elementi del pattern (in AND)
     * @return true se i pattern sono contenuti, false altrimenti
     */
    public static boolean containsPattern(String string, String[] pattern) {
        for (String fil : pattern) {
            if (!string.contains(fil)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Restituisce la parola o le parole all'indice. E.g.,
     * <p>
     * getSubWords(sentence, 1, -1) restituisce dalla seconda parola all'ultima.
     * getSubWords(sentence, 2, 2) restituisce solo la seconda parola.
     *
     * @param sentence       La frase da cui estrare le parole
     * @param firstWordIndex 0-based
     * @param lastWordIndex  0-based, -1 se fino all'ultima
     * @return la/le parole selezionate.
     */
    public static String getSubWords(String sentence, int firstWordIndex, int lastWordIndex) {
        String[] words = sentence.split(" ");
        if ((lastWordIndex == -1) || (lastWordIndex > words.length)) {
            lastWordIndex = words.length - 1;
        }
        if (firstWordIndex < 0) {
            firstWordIndex = 0;
        }
        if (firstWordIndex > words.length) {
            firstWordIndex = words.length - 1;
        }
        if (lastWordIndex < firstWordIndex) {
            return "";
        }

        StringBuilder output = new StringBuilder();
        for (int i = firstWordIndex; i <= lastWordIndex; i++) {
            output.append(words[i]).append(" ");
        }

        return output.toString().trim();
    }

    /**
     * Restituisce la sottostringa successiva al pattern from. E.g.,
     * getFrom("Ciao Pippo e Topolino", "Pippo e") restituisce "Topolino".
     *
     * @param sentence La frase da cui estrarre la sottostringa
     * @param from     La parola dalla quale tagliare (viene tagliata anch'essa)
     * @return La sottostringa corrispondente al taglio. Stringa vuota se niente
     * è stato trovato.
     */
    public static String getFrom(String sentence, String from) {
        int pos = sentence.indexOf(from);
        if (pos < 0) {
            return "";
        }
        pos += from.length();
        return sentence.substring(pos);
    }

    /**
     * Restituisce la sottostringa successiva al pattern from.E.g.,
     * getFrom("Ciao Pippo e Topolino e Minni", "Pippo e", "e Minni")
     * restituisce "Topolino".
     *
     * @param sentence La frase da cui estrarre la sottostringa
     * @param from     La parola dalla quale tagliare (viene tagliata anch'essa)
     * @param to       La parola fino alla quale tagliare (viene tagliata anch'essa)
     * @return La sottostringa corrispondente al taglio. Stringa vuota se niente
     * è stato trovato.
     */
    public static String getFrom(String sentence, String from, String to) {
        int pos = sentence.indexOf(from);
        if (pos < 0) {
            return "";
        }
        pos += from.length();
        sentence = sentence.substring(pos);
        pos = sentence.indexOf(to);
        if (pos < 0) {
            return "";
        }
        return sentence.substring(0, pos);
    }

    /**
     * Restituisce il Long dal valore, se ci sono eccezioni vengono gestite.
     *
     * @param value     Il valore di partenza
     * @param forceZero Se vuoto, null o eccezioni, restituisce 0 anziché null.
     * @return Il Long corrispondente.
     */
    @SuppressWarnings("UseSpecificCatch")
    public static Double safeNumber(Object value, boolean forceZero) {
        if (value == null) {
            return forceZero ? 0.0 : null;
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception ignore) {
            return forceZero ? 0.0 : null;
        }

    }

    /**
     * Returns a String (or null), already trimmed. The value can be also a
     * chain of getters, the exception will be handled.
     *
     * @param value      the value
     * @param forceEmpty if true will return "" instead of null
     * @return una stringa o null, ma senza ropersi.
     */
    public static String safeString(Object value, boolean forceEmpty) {
        if (value == null) {
            return forceEmpty ? "" : null;
        }
        try {
            return value.toString().trim();
        } catch (Exception ignore) {
            return forceEmpty ? "" : null;
        }
    }

    /**
     * Conta le occorrenze della substring nella string
     *
     * @param string    La stringa di partenza
     * @param subString La sottostringa di cui contare le occorrenze
     * @return il numero di occorrenze
     */
    public static int countOccurrences(String string, String subString) {
        int count = 0, fromIndex = 0;
        while ((fromIndex = string.indexOf(subString, fromIndex)) != -1) {
            // System.out.println("Found at index: " + fromIndex);
            count++;
            fromIndex++;
        }
        return count;
    }


    public static int getLevenshteinDistance(String s, String t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        /*
        The difference between this impl. and the previous is that, rather
        than creating and retaining a matrix of size s.length()+1 by t.length()+1,
        we maintain two single-dimensional arrays of length s.length()+1.  The first, d,
        is the 'current working' distance array that maintains the newest distance cost
        counts as we iterate through the characters of String s.  Each time we increment
        the index of String t we are comparing, d is copied to p, the second int[].  Doing so
        allows us to retain the previous cost counts as required by the algorithm (taking
        the minimum of the cost count to the left, up one, and diagonally up and to the left
        of the current cost count being calculated).  (Note that the arrays aren't really
        copied anymore, just switched...this is clearly much better than cloning an array
        or doing a System.arraycopy() each time  through the outer loop.)

        Effectively, the difference between the two implementations is this one does not
        cause an out of memory condition when calculating the LD over two very large strings.
         */
        int n = s.length(); // length of s
        int m = t.length(); // length of t

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        int[] p = new int[n + 1]; //'previous' cost array, horizontally
        int[] d = new int[n + 1]; // cost array, horizontally
        int[] _d; //placeholder to assist in swapping p and d

        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t

        char t_j; // jth character of t

        int cost; // cost

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            t_j = t.charAt(j - 1);
            d[0] = j;

            for (i = 1; i <= n; i++) {
                cost = s.charAt(i - 1) == t_j ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }

            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // our last action in the above loop was to switch d and p, so p now
        // actually has the most recent cost counts
        return p[n];
    }

}
