/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.string;

/**
 * @author addis
 */
@SuppressWarnings("unused")
public class RegKeys {

    // http://www.regular-expressions.info/reference.html
    public static final String regexEmail = "[a-z0-9A-Z!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
    // other regex email: "[A-Z0-9._%+-]+@(?:[A-Z0-9-]+\\.)+[A-Z]{2,4}"; // "(\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,6})";
    public static final String regexUrl = "((https?|ftp|gopher|telnet|file|notes|ms-help):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\.&]*)";
    public static final String regexMailto = "mailto:" + regexEmail;
    public static final String regexWWW = "www.\\w+\\.\\S*"; // www.<parola>.<qualsiasi cosa_senza spazi o ritorni>
    public static final String regexPhone = "/^((\\+\\d{1,3}(-| )?\\(?\\d\\)?(-| )?\\d{1,5})|(\\(?\\d{2,6}\\)?))(-| )?(\\d{3,4})(-| )?(\\d{4})(( x| ext)\\d{1,5}){0,1}$/";
    /**
     * Il nome di una variabile può contenere solo lettere, numeri, underscore.
     * Attenzione puo' partire con un numero!
     */
    public static final String regVariable = "[A-Za-z0-9_]{1,}";
    /**
     * Una property può contenere caratteri, numeri, underscore e punti.
     */
    public static final String regProperty = "[A-Za-z0-9_\\.]{1,}";

}
