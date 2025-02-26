/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.serial;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Utiliti per la manipolazione di Bits e Bytes.
 *
 * @author Andrea
 * @version 09 dic 2019
 */
@SuppressWarnings({"WeakerAccess", "unused", "UnusedReturnValue"})
public class BitUtils {

    public static byte[] setBit(byte[] bb, int position, boolean bit) {
        int byteNumber = bb.length - position / 8 - 1;
        int bytePosition = position % 8;
        if (byteNumber < 0) {
            return bb;
        }
        bb[byteNumber] = setBit(bb[byteNumber], bytePosition, bit);
        return bb;
    }

    public static byte setBit(byte b, int position, boolean bit) {
        if (position > 7) {
            return b;
        }
        if (bit) {
            b = (byte) (b | (1 << position));   // longer version, or
            // b |= 1 << position;              // shorthand
        } else {
            b = (byte) (b & ~(1 << position));  // longer version, or
            // b &= ~(1 << position);           // shorthand
        }
        return b;
    }

    public static int getBit(byte b, int position) {
        return (b >> position) & 1;
    }

    public static int getBit(byte[] bb, int position) {
        int byteNumber = bb.length - position / 8 - 1;
        int bytePosition = position % 8;
        return (bb[byteNumber] >> bytePosition) & 1;
    }

    public static String toString(byte[] bb, boolean space) {
        StringBuilder output = new StringBuilder();
        for (byte b : bb) {
            String bs = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            output.append(bs).append(space ? " " : "");
        }
        return output.toString().trim();
    }

    /**
     * Restituisce i bytes in Hex format, 0xAB 0xCD.
     *
     * @param in bytes di input
     * @return la stringa hex.
     */
    public static String toString(byte[] in) {
        if (in == null) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            String bx = "0x" + String.format("%02x", b).toUpperCase() + " ";
            builder.append(bx);
        }
        return builder.toString().trim();
    }

    /**
     * Restituisce un array riempito con la chiave.
     *
     * @param fraseLen lunghezza in bytes dell'array
     * @param key      l'id della key, e.g., 3 sarà una key 000011
     * @return Il byte con la key
     */
    public static byte[] getKey(final int fraseLen, final byte key, final int keyLen) {
        byte[] xorFrase = new byte[fraseLen];
        for (int pos = 0; pos < fraseLen * 8; pos++) {
            int bitPos = pos % keyLen;
            int bitVal = getBit(key, bitPos);
            setBit(xorFrase, pos, bitVal == 1);
        }

        return xorFrase;
    }

    /**
     * Restituisce un array riempito con la chiave.
     *
     * @param fraseLen lunghezza in bytes dell'array
     * @param key      l'id della key, e.g., 3 sarà una key 000011
     * @return Il byte con la key
     */
    public static byte[] getKey(final int fraseLen, final byte[] key, final int keyLen) {
        byte[] xorFrase = new byte[fraseLen];
        for (int pos = 0; pos < fraseLen * 8; pos++) {
            int bitPos = pos % keyLen;
            int bitVal = getBit(key, bitPos);
            setBit(xorFrase, pos, bitVal == 1);
        }

        return xorFrase;
    }

    public static byte[] ByteArray_fromHex(String s) {
        s = s.replaceAll(" ", "");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                  + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte[] ByteArray_fromString(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] ByteArray_fromInteger(int integer) {
        return ByteBuffer.allocate(4).putInt(integer).array();
    }

    /**
     * Fa il confronto byte to byte di due array, perciò si usa per controllare
     * la corrispondenza di una chiave.
     *
     * @param byte_array_1 Array di Byte 1
     * @param byte_array_2 Array di Byte 2
     * @return true se gli array di byte contengono gli stessi valori, false se
     * sono diversi o uno dei due null.
     */
    public static boolean equals(byte[] byte_array_1, byte[] byte_array_2) {
        if (byte_array_1 == null || byte_array_2 == null) {
            return false;
        }

        if (byte_array_1.length != byte_array_2.length) {
            return false;
        }

        for (int i = 0; i < byte_array_1.length; i++) {
            if (byte_array_1[i] != byte_array_2[i]) {
                return false;
            }
        }

        return true;
    }

    public void testBytes() {
        // bb[1] = setBit(bb[1], 5, true);
        for (int i = 0; i < 50; i++) {
            byte[] bb = new byte[3];
            setBit(bb, i, false);
            System.out.println(toString(bb, true));
        }
        for (int i = 0; i < 50; i++) {
            byte[] bb = new byte[]{(byte) 255, (byte) 255, (byte) 255};
            setBit(bb, i, false);
            System.out.println(toString(bb, true));
        }
    }

}
