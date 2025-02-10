/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.serial;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Andrea
 * @version Apr 24, 2020
 */
@SuppressWarnings("unused")
public class Hash {

    /**
     * Compute Hash of the Data according to the selected Algorithm, e.g.,
     * SHA256.
     *
     * @param dataToHash The Data to Hash
     * @return Hash of the data
     */
    public static String computeHash(String algorithm, String dataToHash) {
        MessageDigest digest;
        byte[] bytes;
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Hash.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        bytes = digest.digest(dataToHash.getBytes(UTF_8));
        StringBuilder buffer = new StringBuilder();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

}
