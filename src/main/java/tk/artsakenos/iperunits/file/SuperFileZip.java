/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Andrea
 */
@SuppressWarnings("unused")
public class SuperFileZip {

    /**
     * Esempio:
     * <pre>
     * String zipfilename = "extra/datasets/dictionaries/ParoleItaliane.zip";
     * String internalFilePath = "ParoleItaliane/paroleitaliane/280000_parole_italiane.txt";
     * String read_file_in_zip = SuperFileZip.read_file_in_zip(zipfilename, internalFilePath);
     *
     * @param zipFilePath Il percorso dello zipFile
     * @param internalFilePath Il percorso del file all'interno dello zip
     * @return il contenuto del file in formato stringa
     */
    public static String fromZip(String zipFilePath, String internalFilePath) {

        final int BUFFER_SIZE = 4096;
        ZipInputStream zipIn;
        StringBuilder output = new StringBuilder();
        try {
            zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                // System.out.println("Zip Entry:"+entry.getName());
                if (!entry.getName().equals(internalFilePath)) {
                    entry = zipIn.getNextEntry();
                    continue;
                }
                byte[] bytesIn = new byte[BUFFER_SIZE];
                int read = 0;
                while ((read = zipIn.read(bytesIn)) != -1) {
                    output.append(new String(bytesIn));
                    // System.out.println(new String(bytesIn));
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            zipIn.close();
        } catch (IOException ex) {
            Logger.getLogger(SuperFileZip.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output.toString();
    }

    /**
     * Restituisce una lista dei file all'interno dello zip
     *
     * @param zipFilePath Il Path dello Zip
     */
    @SuppressWarnings("ConvertToTryWithResources")
    public static void fileList(String zipFilePath) {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                long compressedSize = entry.getCompressedSize();
                long normalSize = entry.getSize();
                String type = entry.isDirectory() ? "DIR" : "FILE";

                System.out.println(name);
                System.out.format("\t %s - %d - %d\n", type, compressedSize, normalSize);
            }

            zipFile.close();
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    /**
     * Comprime un file in uno zip
     *
     * @param zipFilePath      Il path dello zip
     * @param externalFilePath Il path del file esterno
     */
    @SuppressWarnings("ConvertToTryWithResources")
    public static void toZip(String zipFilePath, String externalFilePath) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(zipFilePath);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(externalFilePath);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            zipOut.close();
            fis.close();
            fos.close();
        } catch (IOException ex) {
            Logger.getLogger(SuperFileZip.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("ConvertToTryWithResources")
    public static void toZip(String zipFilePath, List<String> srcFiles) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(zipFilePath);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            for (String srcFile : srcFiles) {
                File fileToZip = new File(srcFile);
                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                fis.close();
            }
            zipOut.close();
            fos.close();
        } catch (IOException ex) {
            Logger.getLogger(SuperFileZip.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
