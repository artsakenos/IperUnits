/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.file;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * To execute a random access on a File, the Random Access File exists.
 * SuperFile allows binary access to a file, either in input or output, with
 * some useful string operation like readLine();
 *
 * @author Andrea Addis
 * @version 2018.12.12
 */
@SuppressWarnings("unused")
public class SuperFileText {

    private BufferedReader br = null;
    private BufferedWriter bw = null;

    /**
     * @return the br
     */
    public BufferedReader getBufferedReader() {
        return br;
    }

    /**
     * @return the BufferedWriter
     */
    public BufferedWriter getBufferedWriter() {
        return bw;
    }

    //--------------------------------------------------------------------------
    public BufferedReader openInput(File iFile, Charset iCharset) throws FileNotFoundException {
        FileInputStream iFileStream = new FileInputStream(iFile);
        InputStreamReader iStream = new InputStreamReader(iFileStream, iCharset);
        return br = new BufferedReader(iStream);
    }

    public BufferedReader openInput(String fileName, String charsetName) throws FileNotFoundException {
        return openInput(new File(fileName), Charset.forName(charsetName));
    }

    /**
     * If the reader exists and is open, it closes it, else do nothing. No
     * exceptions are thrown.
     */
    public void closeInput() {
        if (br == null) {
            return;
        }
        try {
            br.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * Read a line from reader.
     *
     * @return the line from the reader, null if the reader is not open or in
     * case of exception
     */
    public String readLine() {
        if (br == null) {
            return null;
        }
        try {
            return br.readLine();
        } catch (IOException ex) {
            return null;
        }
    }
    //--------------------------------------------------------------------------

    public BufferedWriter openOutput(File file, Charset charset) throws FileNotFoundException {
        // StringBuilder oStringBuffer = new StringBuilder();
        FileOutputStream oFileStream = new FileOutputStream(file);
        OutputStreamWriter oStream = new OutputStreamWriter(oFileStream, charset);
        return bw = new BufferedWriter(oStream);
    }

    public BufferedWriter openOutput(String fileName, String charsetName) throws FileNotFoundException {
        return openOutput(new File(fileName), Charset.forName(charsetName));
    }

    /**
     * If the writer exists and is open, it closes it, else do nothing. No
     * exceptions are thrown.
     */
    public void closeOutput() {
        if (bw == null) {
            return;
        }
        try {
            bw.close();
        } catch (IOException ignored) {
        }
    }
    //--------------------------------------------------------------------------

    /**
     * Fetch the entire contents of a text file, and return it in a String. This
     * style of implementation does not throw Exceptions to the caller.
     */
    public static String getText(String fileName) {
        boolean utf8 = true;
        File file = new File(fileName);
        final int BUFF_SIZE = 100000;
        final char[] buffer = new char[BUFF_SIZE];
        BufferedReader input = null;
        StringBuilder contents = new StringBuilder();
        try {
            input = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

            while (true) {
                synchronized (buffer) {
                    int amountRead = input.read(buffer);
                    if (amountRead == -1) {
                        break;
                    }
                    contents.append(buffer, 0, amountRead);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SuperFileText.class.getName()).log(Level.SEVERE, "ERROR in getText(...):{0}", ex.getLocalizedMessage());
        } //--------------------------------------------------------------------------
        finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(SuperFileText.class.getName()).log(Level.SEVERE, "ERROR in getText(...close):{0}", ex.getLocalizedMessage());
            }
        }
        // Removing BOM!
        if (!contents.isEmpty() && contents.charAt(0) == 65279) {
            contents.deleteCharAt(0);
        }
        return contents.toString();
    }

    /**
     * Static method which writes a string (also unicode) on a text file. e.g.:
     * SuperFileText.appendText("pippo.txt", "你好!\r\nciao! :)");
     *
     * @param fileName the name of the file where to write
     * @param string   The string
     */
    public static void setText(String fileName, String string) {
        FileOutputStream oFileStream;
        try {
            oFileStream = new FileOutputStream(new File(fileName));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SuperFileText.class.getName()).log(Level.SEVERE, "ERROR in setText(...):{0}", ex.getLocalizedMessage());
            return;
        }
        OutputStreamWriter oStream = new OutputStreamWriter(oFileStream, StandardCharsets.UTF_8);
        BufferedWriter bw = new BufferedWriter(oStream);
        try {
            bw.write(string);
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(SuperFileText.class.getName()).log(Level.SEVERE, "ERROR in setText(...):{0}", ex.getLocalizedMessage());
        }
    }

    /**
     * Static method which writes a string (also unicode) on a text file. e.g.:
     * SuperFileText.appendText("pippo.txt", "你好!\r\nciao! :)");
     *
     * @param filePath the name of the file where to write
     * @param text     The text. You need to insert \n for a newline!
     */
    public static void append(String filePath, String text) {
        FileOutputStream oFileStream;
        try {
            oFileStream = new FileOutputStream(new File(filePath), true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SuperFileText.class.getName()).log(Level.SEVERE, "ERROR in appendText(...):{0}", ex.getLocalizedMessage());
            return;
        }
        OutputStreamWriter oStream = new OutputStreamWriter(oFileStream, StandardCharsets.UTF_8);
        BufferedWriter bw = new BufferedWriter(oStream);
        try {
            bw.append(text);
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(SuperFileText.class.getName()).log(Level.SEVERE, "ERROR in appendText(...):{0}", ex.getLocalizedMessage());
        }

    }

    /**
     * Non ancora utilizzata ma pronta all'uso
     */
    private static void append_new(String filePath, String text) {
        try {
            final Path path = Paths.get(filePath);
            Files.write(path, Collections.singletonList(text), StandardCharsets.UTF_8,
                    Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        } catch (final IOException ioe) {
            // Add your own exception handling...
        }
    }

    public static String getTail(String fileName, int lines) {
        if (!FileManager.fileExists(fileName)) {
            return "";
        }
        java.io.RandomAccessFile fileHandler = null;
        try {
            fileHandler = new java.io.RandomAccessFile(new File(fileName), "r");
            long fileLength = fileHandler.length() - 1;
            StringBuilder sb = new StringBuilder();
            int line = 0;

            for (long filePointer = fileLength; filePointer != -1; filePointer--) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();

                if (readByte == 0xA) {
                    if (filePointer < fileLength) {
                        line = line + 1;
                    }
                } else if (readByte == 0xD) {
                    if (filePointer < fileLength - 1) {
                        line = line + 1;
                    }
                }
                if (line >= lines) {
                    break;
                }
                sb.append((char) readByte);
            }

            return sb.reverse().toString();
        } catch (IOException e) {
            return null;
        } finally {
            if (fileHandler != null) {
                try {
                    fileHandler.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    public static void printAvailableCharset() {
        for (String s : Charset.availableCharsets().keySet()) {
            System.out.println(s + ":" + Charset.availableCharsets().get(s));
        }
    }

}
