/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.serial;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Andrea
 * @version Apr 03, 2024
 * @since Apr 24, 2020
 */
@SuppressWarnings("unused")
public class Hash {

    /**
     * Compute Hash of the Data according to the selected Algorithm, e.g.,
     * SHA256.
     *
     * @param text      The Text to Hash
     * @param algorithm The Algorithm, e.g., SHA256 (if null)
     * @return The Hash of the Text
     */
    public static String hash(String text, String algorithm) {
        if (algorithm == null || algorithm.isEmpty()) algorithm = "SHA256";
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] encodedHash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(algorithm + " algorithm not available", e);
        }
    }

}
