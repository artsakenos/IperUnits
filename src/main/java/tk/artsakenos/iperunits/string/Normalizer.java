/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.string;

/**
 * @author <font face="Georgia" color="red"> <strong>
 * <a href="mailto:a.addis@gmail.com">Andrea Addis</a> </strong> -
 * &copy;<a href="http://infodev.wordpress.com">Informatic Devices </a> </font>
 */
@SuppressWarnings("unused")
public class Normalizer {

    public static String[] convertList = new String[]{
            "'", "' ",
            "�", "a'",
            "�", "e'",
            "�", "e'",
            "�", "i'",
            "�", "o'",
            "�", "u'",
            "�", "e'",
            "�", "e'", //  Notare che metto le minuscole!!!
            "�", "e'",
            "à", "a'",
            "é", "e'",
            "è", "e'",
            "ì", "i'",
            "ò", "o'",
            "ù", "u'",
            "!", ".",
            ";", ".",
            "\\.", " . ",
            "\\?", " ? ",
            "[^\\w.?']", " " // Ogni carattere eccetto lettere, numeri, .?'
    };

    ///-----------------------------------------------------------------------------
    ///------------------   Text Utilities
    ///-----------------------------------------------------------------------------

    /**
     * Normalizza il testo sfruttando il normalizeWord. Elimina i \r e spazia
     * attorno ai \n Viene eseguito un trim() finale.
     *
     * @param text        Il testo da normalizzare
     * @param punctuation Indica se si vuole lasciare la punteggiatura (che
     *                    sara' comunque semplificata)
     * @param numbers     Indica se si vogliono lasciare i numeri
     * @return Il testo normalizzato
     */
    public static String normalizeText(String text, boolean punctuation, boolean numbers) {
        StringBuilder sb = new StringBuilder();

        text = text.replaceAll("\r", "");
        text = text.replaceAll("\n", " \n ");

        for (String word : text.split(" ")) {
            sb.append(normalizeWord(word, punctuation, numbers)).append(" ");
        }

        String output = " " + sb.toString() + " ";

        while (output.contains("  ")) {
            output = output.replaceAll(" {2}", " ");
        }
        while (output.contains("? . ")) {
            output = output.replaceAll("\\? \\. ", "? ");
        }
        while (output.contains(". ? ")) {
            output = output.replaceAll("\\. \\? ", "? ");
        }

        while (output.contains(". . ")) {
            output = output.replaceAll("\\. \\. ", ". ");
        }
        while (output.contains("? ? ")) {
            output = output.replaceAll("\\? \\? ", "? ");
        }

        return output.trim();
    }

    /**
     * Normalizza una parola se questo non è un url, un email un numero di
     * telefono, eseguendo le seguenti operazioni:
     * <pre>
     * 0) Si tratta di email/url/phone/mailto? Lo lascia come e'.
     * 1) Il lower case,
     * 2) La conversione dei caratteri speciali e dei punti in . e ?
     * 3) Eliminazione di punti e numeri
     * 4) esecuzione trim(). ATTENZIONE: Non vengono inseriti gli spazi di cornice!!
     * ATTENZIONE: SE number==false spazia tutto, da correggere!!
     * </pre>
     *
     * @param word        La parola da normalizzare
     * @param punctuation Indica se si vogliono lasciare i punti
     * @param numbers     Indica se si vogliono lasciare i numeri
     * @return restituisce la parola normalizzata
     */
    public static String normalizeWord(String word, boolean punctuation, boolean numbers) {

        ///-{ 1) Controlliamo che non sia un url o un indirizzo email
        if (word.matches(RegKeys.regexWWW)
                || word.matches(RegKeys.regexUrl)
                || word.matches(RegKeys.regexMailto)
                || word.matches(RegKeys.regexPhone)
                || word.matches(RegKeys.regexEmail)) {
            return word;
        }

        ///-{ 2) Lowercaseizziamo
        word = word.toLowerCase();

        ///-{ 3) Smanettiamo la word secondo la tabella di conversione
        for (int i = 0; i < convertList.length; i += 2) {
            word = word.replaceAll(convertList[i], convertList[i + 1]);
            // System.out.println(convertList[i]+"->"+convertList[i + 1]+"="+word);
        }

        if (!punctuation) {
            // word = word.replaceAll("\\W*", "");
            word = word.replaceAll("[\\W*&&[^']]", " ");
        }
        if (!numbers) {
            word = word.replaceAll("\\d*", " ");
        }

        while (word.contains("  ")) {
            word = word.replaceAll(" {2}", " ");
        }

        return word.trim();
    }

    ///-----------------------------------------------------------------------------
    ///------------------   Other Text Utilities
    ///-----------------------------------------------------------------------------

    /**
     * Rimuove la punteggiatura e le cifre dal testo, lascia le lettere al loro
     * case.
     *
     * @param doc il testo da elaborare
     * @return Il testo senza punteggiatura e cifre
     */
    public static String leaveLetters(String doc) {
        // Se ci sono problemi di sincronizzazione, usare StringBuffer
        StringBuilder sb = new StringBuilder(doc);
        int l = sb.length();
        char c;
        for (int index = 0; index < l; ++index) {
            c = sb.charAt(index);
            if (!(c == 32 || (c >= 65 && c <= 90) || (c >= 97 && c <= 122)
                    || c == 192 || c == 193 || c == 200 || c == 201 || c == 204 || c == 205
                    || c == 210 || c == 211 || c == 217 || c == 218 || c == 224 || c == 225
                    || c == 232 || c == 233 || c == 236 || c == 237 || c == 242 || c == 243
                    || c == 249 || c == 250)) {
                sb.setCharAt(index, (char) 32);
            }
        }

        return sb.toString();
    }

    /**
     * Elimina dal testo doppi spazi, doppi tab e doppi accapi, convertendo gli
     * accapi in \n
     *
     * @param text Il testo da normalizzare
     * @return il testo normalizzato
     */
    public static String normalizeSpecials(String text) {
        text = text.replaceAll("\r", "");
        while (text.contains("  ")) {
            text = text.replace("  ", " ");
        }

        while (text.contains(" \n")) {
            text = text.replace(" \n", "\n");
        }

        while (text.contains("\n ")) {
            text = text.replace("\n ", "\n");
        }

        while (text.contains("\n\n")) {
            text = text.replace("\n\n", "\n");
        }

        while (text.contains("\t\t")) {
            text = text.replace("\t\t", "\t");
        }

        return text;
    }

    ///-----------------------------------------------------------------------------
    ///------------------   Altre Utilities
    ///-----------------------------------------------------------------------------

    /**
     * Crea una versione normalizzata del nome del file, lasciando solo numeri e
     * caratteri e il '.'
     *
     * @param fileName  Il nome del file
     * @param keepspace Mantiene gli spazi
     * @return restituisce il nome del file normalizzato
     */
    public static String normalizeFileName(String fileName, boolean keepspace) {
        StringBuilder output = new StringBuilder();

        for (int index = 0; index < fileName.length(); ++index) {
            Character c = fileName.charAt(index);
            if (Character.isLetter(c) || Character.isDigit(c) || c.equals('.')) {
                output.append(c);
            }
            if (keepspace && c.equals(' ')) {
                output.append(c);
            }
        }

        return output.toString();
    }

}
