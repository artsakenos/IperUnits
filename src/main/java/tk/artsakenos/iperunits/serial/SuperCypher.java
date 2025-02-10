package tk.artsakenos.iperunits.serial;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@SuppressWarnings({"unused", "WeakerAccess"})
public class SuperCypher {

    public static byte[] KEY = new byte[]{'c', 'o', 'd', 'd', 'i', 'n', 'g', 'a', 'f', 'a', 'y', 'k', 's', 'c', 'o', 'm'};

    private static final String AES = "AES";

    public static String encrypt(String cleartext) {
        if (cleartext == null) {
            return null;
        }
        byte[] rawKey = getRawKey();
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return toHex(result);
    }

    public static String decrypt(String encrypted) {
        if (encrypted == null) {
            return null;
        }
        try {
            byte[] enc = toByte(encrypted);
            byte[] result = decrypt(enc);
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    private static byte[] getRawKey() {
        SecretKey key = new SecretKeySpec(KEY, AES);
        return key.getEncoded();
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) {
        try {
            SecretKey skeySpec = new SecretKeySpec(raw, AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            return cipher.doFinal(clear);
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            return null;
        }
    }

    private static byte[] decrypt(byte[] encrypted)
            throws Exception {
        SecretKey skeySpec = new SecretKeySpec(KEY, AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(encrypted);
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        }
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null) {
            return "";
        }
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (byte aBuf : buf) {
            appendHex(result, aBuf);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    public static void encryptJavaString(String variable, String string) {
        String psfs = "public static final String ";
        System.out.println("// " + psfs + variable + " = \"" + string + "\";");
        System.out.println(psfs + variable + " = SuperCypher.decrypt(\"" + encrypt(string) + "\");");
    }
}
