/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.web.pebblechain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A block of a "social targeted" BlockChain. A pebble of the PebbleChain.
 *
 * @author artsakenos
 */
@SuppressWarnings("unused")
@Data
@NoArgsConstructor
@Log
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pebble implements Serializable {

    public static final String P_ALGORITHM = "SHA-256";
    public static final String P_START_HASH = "💎THROWN#GENESIS";
    public static final String[] P_START_LINKS = new String[]{};
    public static final String P_START_TARGET = "bc00";
    public static final int P_TARGET_MINLENGTH = 4;
    public static final int P_DATA_MAXSIZE = 10 * 1024 * 1024;

    // Pebble Properties
    private String previousHash = P_START_HASH;
    private String[] previousLinks = P_START_LINKS;
    private String target = P_START_TARGET;
    private int depth = 0;
    private boolean verbose = true;

    private String owner;   // Can exploit OpenId, plus PGP.
    private String data;    // Can be a BASE64 representation of an object.
    private long created;
    private String hash;
    private String merkle;
    private int nonce = 0;

    // -------------------------------------------------------------------------

    /**
     * Creates a new Pebble.
     *
     * @param data The Data. Can be a BASE64 representation of an Object.
     * @throws PebbleException The PebbleException.
     */
    public Pebble(String data) throws PebbleException {
        this(P_START_HASH, P_START_LINKS, P_START_TARGET, P_START_TARGET, data, null, Calendar.getInstance(Locale.getDefault()).getTimeInMillis(), 0);
    }

    /**
     * Loads an existing Pebble.
     *
     * @param previousHash  The Hash of the previous Pebble in the Chain (null if
     *                      genesis)
     * @param previousLinks The link URI where to find the previous Pebbles
     * @param target        The chosen target. Lower cased hex. Its length must be >
     *                      P_PREFIX_MINLENGTH, and will be a proof of work giving a value to the
     *                      Pebble according to the target length and nonce value.
     * @param data          The data of the Pebble.
     * @param hash          The hash.
     * @param created_epoch The epoch.
     * @param nonce         The nonce.
     */
    public Pebble(String previousHash, String[] previousLinks, String target, String owner, String data,
                  String hash, long created_epoch, int nonce) throws PebbleException {
        if (!target.matches("-?[0-9a-f]+")) {
            throw new PebbleException(PebbleException.ET_INVALID_HEX, target + " is not exadecimal.");
        }
        this.created = created_epoch;
        this.previousHash = previousHash;
        this.previousLinks = previousLinks;
        this.data = data;
        this.target = target;
        this.hash = hash;
        this.nonce = nonce;
        this.owner = owner;

        if (hash != null && !isValid()) {
            throw new PebbleException(PebbleException.ET_INVALID_HEX, "Hash is not valid.");
        }
    }

    public Pebble(Pebble previousPebble, String data) {
        this.setTarget(previousPebble.target);
        this.setData(data);
        this.setDepth(previousPebble.getDepth() + 1);
        this.setOwner(previousPebble.getOwner());
        this.setPreviousHash(previousPebble.getHash());
    }

    /**
     * The Header is a 320 bit (40 byte) string used to sign the block. Two
     * sub-block apart from the Merkle Root, because once the version is chosen
     * they're the ones who knock the door.
     *
     * @return the block header
     */
    private String retrieveHeader() {
        return getMerkle() + getCreated() + getNonce();
    }

    /**
     * Mines the block, making it valid. Sets its Merkle.
     */
    public void mineBlock() {
        this.merkle = retrieveMerkleRoot();
        this.setCreated(Calendar.getInstance().getTimeInMillis());
        String hashGuess = "";
        while (!hashGuess.startsWith(target)) {
            ++nonce;
            hashGuess = computeHash(P_ALGORITHM, retrieveHeader());
            if (verbose) {
                verboseMining();
            }
            // Updates timestamp time to time
            if (nonce % 1_000_000 == 0) {
                this.setCreated(Calendar.getInstance().getTimeInMillis());
            }
        }
        setHash(hashGuess);
    }

    /**
     * Shows the progress of the mining process.
     */
    private void verboseMining() {
        if (getNonce() % 10_000 == 0) {
            System.out.print(".");
        }
        if (getNonce() % 1_000_000 == 0) {
            String nf = NumberFormat.getNumberInstance(Locale.US).format(getNonce());
            // Gives an idea of how much of the integer (half) spectrum has passed
            double percentage = (double) getNonce() / (double) Integer.MAX_VALUE * 100.0;
            System.out.printf(" %s; %,.2f%%%n", nf, percentage);
        }
    }

    /**
     * Check if a Pebble is valid according to its version.
     *
     * @return true if the Pebble is considered valid (rules can vary according
     * to the version).
     */
    public final boolean isValid() {
        this.merkle = retrieveMerkleRoot();
        String hash_computed = computeHash(P_ALGORITHM, retrieveHeader());
        String message = "";

        if (!hash_computed.equals(this.hash)) {
            message += "Hash Signature doesn't match; ";
        }

        // Check Prefix Length
        if (getTarget().length() < P_TARGET_MINLENGTH) {
            message += "Short Prefix " + getTarget().length() + "<" + P_TARGET_MINLENGTH + "; ";
        }

        // Check Previous Links
        if (getPreviousLinks() == null) {
            message += "Previous_link can't be null, can be empty though; ";
        }

        // Check Data Length, 10MB maximum right now.
        if (getData().length() > P_DATA_MAXSIZE) {
            message += "Data packet too big, >" + P_DATA_MAXSIZE + "; ";
        }

        if (message.isEmpty()) {
            return true;
        } else {
            log.warning(message.trim());
            return false;
        }

    }

    @Override
    public String toString() {
        return String.format("[%s] Lev:%d; PrevHash:%s; PrevLinks:%s; [%s]",
                getId(),
                getDepth(),
                getPreviousHash(),
                Arrays.toString(getPreviousLinks()),
                isValid() ? "valid" : "invalid");
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            log.severe(ex.getLocalizedMessage());
        }
        return null;
    }

    public static Pebble fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, Pebble.class);
        } catch (IOException ex) {
            log.severe(ex.getLocalizedMessage());
        }
        return null;
    }

    // -------------------------------------------------------------------------

    /**
     * Compute Hash of the Data according to the selected Algorithm, e.g.,
     * SHA256.
     *
     * @param dataToHash The Data
     * @return Hash of the data The Corresponding Hash
     */
    public final String computeHash(String algorithm, String dataToHash) {

        if (algorithm == null || dataToHash == null) {
            log.severe("Algorithm and Data can't be null in computeHash(...)");
            return "";
        }

        MessageDigest digest;
        byte[] bytes;
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            log.severe(ex.getLocalizedMessage());
            return "";
        }
        bytes = digest.digest(dataToHash.getBytes(UTF_8));
        StringBuilder buffer = new StringBuilder();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

    /**
     * Retrieve the Merkle Root Hash to build the header, according to the
     * pebble version.
     *
     * @return The Merkle root.
     */
    private String retrieveMerkleRoot() {
        StringBuilder links = new StringBuilder();
        for (String link : getPreviousLinks()) {
            links.append(link).append("\n");
        }
        String merkleData
                = getPreviousHash() + "\n"
                + getTarget() + "\n"
                + getOwner() + "\n"
                + links
                + getData() + "\n";

        return computeHash(P_ALGORITHM, merkleData);
    }

    // -------------------------------------------------------------------------

    /**
     * The value of the Pebble. The price is calculated according to an
     * opinionated view taking into account the calculation time.
     * <p>
     * In this case, The nonce should be the minimum allowing the target to be
     * reached. Which is not if you selected it randomly, or if you reset it
     * during the computation.
     *
     * @return a block pseudo-value
     */
    public double getValue() {
        return Math.pow(10, getTarget().length() - 20) * getNonce() * retrieveHeader().length();
    }

    /**
     * Returns a Pebble Identifier
     *
     * @return a Pebble identifier
     */
    public String getId() {
        return "💎" + getTarget() + "#" + getHash();
    }

    /**
     * Creation date in IS8601
     *
     * @return the date created in Milliseconds Epoch Time
     */
    public String getCreatedISO8601() {
        return Instant.ofEpochMilli(getCreated()).toString();
    }

    // -------------------------------------------------------------------------

    /**
     * A PebbleException.
     *
     * @author artsakenos
     */
    public static class PebbleException extends IOException {

        public static final String ET_INVALID_HEX = "Invalid Hex";

        public PebbleException(String type, String message) {
            super("[" + type + "] " + message);
        }

    }
}
