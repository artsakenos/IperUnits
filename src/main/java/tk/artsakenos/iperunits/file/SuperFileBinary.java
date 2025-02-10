/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.file;

import lombok.extern.java.Log;

import java.io.*;


/**
 * To execute a random access on a File, the Random Access File exists.
 * SuperFile allows binary access to a file, either in input or output, with
 * some useful string operation like readLine();
 *
 * @author Andrea
 */
@Log
@SuppressWarnings({"ConvertToTryWithResources", "unused"})
public class SuperFileBinary {

    public static void loadFile(String slimBlobobj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private BufferedInputStream br = null;
    private BufferedOutputStream bw = null;

    /**
     * @return the BufferedReader
     */
    public BufferedInputStream getBufferedReader() {
        return br;
    }

    /**
     * @return the BufferedWriter
     */
    public BufferedOutputStream getBufferedWriter() {
        return bw;
    }

    //--------------------------------------------------------------------------
    public BufferedInputStream openInput(File iFile) throws FileNotFoundException {
        FileInputStream iFileStream = new FileInputStream(iFile);
        BufferedInputStream iStream = new BufferedInputStream(iFileStream);
        return br = iStream;
    }

    public BufferedInputStream openInput(String fileName) throws FileNotFoundException {
        return openInput(new File(fileName));
    }

    public static byte[] loadFile(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            throw new IOException("The file is too big");
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("The file was not completely read: " + file.getName());
        }
        is.close();
        return bytes;
    }
    //--------------------------------------------------------------------------

    public BufferedOutputStream openOutput(File file) throws FileNotFoundException {
        // StringBuilder oStringBuffer = new StringBuilder();
        FileOutputStream oFileStream = new FileOutputStream(file);
        BufferedOutputStream oStream = new BufferedOutputStream(oFileStream);
        return bw = oStream;
    }

    public BufferedOutputStream openOutput(String fileName) throws FileNotFoundException {
        return openOutput(new File(fileName));
    }

    //--------------------------------------------------------------------------
    public static Object getObject(String fileName) {
        FileInputStream fis;
        ObjectInputStream in;
        Object output = null;
        try {
            fis = new FileInputStream(fileName);
            in = new ObjectInputStream(fis);
            output = in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException ex) {
            log.severe("getObject(" + fileName + ") - " + ex.getLocalizedMessage());
        }
        return output;
    }

    public static void setObject(String fileName, Object object) {
        FileOutputStream fos;
        ObjectOutputStream out;
        try {
            fos = new FileOutputStream(fileName);
            out = new ObjectOutputStream(fos);
            out.writeObject(object);
            out.close();
        } catch (IOException ex) {
            log.severe("setObject(" + fileName + ") - " + ex.getLocalizedMessage());
        }
    }
    //--------------------------------------------------------------------------

}
