/*
 * FileManager.java
 *
 * Created on 22 marzo 2005, 19.26
 */
package tk.artsakenos.iperunits.file;

import lombok.extern.java.Log;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;


/**
 * Fornisce i metodi per il controllo dei Files nel sistema e delle loro
 * proprieta' intrinseche
 *
 * @author <p style="color:red; font-family:verdana;">
 * <a href="mailto:a.addis@gmail.com">Andrea Addis</a> - &copy;<a
 * href="http://infodev.wordpress.com">Information Devices </a> </p>
 * @version 2019.02.27
 */
@Log
@SuppressWarnings({"unused", "SimplifyStreamApiCallChains", "Convert2MethodRef", "UnusedReturnValue"})
public class FileManager {

    private static final Logger logger = Logger.getLogger(FileManager.class.getName());

    //--------------------------------------------------------------------------
    //--------- Operazioni sui files
    //--------------------------------------------------------------------------

    /**
     * Il file esiste
     *
     * @param FileName Il nome del File in questione
     * @return true - se l'operazione ha successo
     */
    public static boolean fileExists(String FileName) {
        File ff = new File(FileName);
        return ff.exists();
    }

    /**
     * Cancella un file
     *
     * @param FileName Il nome del File in questione
     * @return true - se il file � stato cancellato con successo
     */
    public static boolean fileDelete(String FileName) {
        File ff = new File(FileName);
        return ff.delete();
    }

    /**
     * Rinomina/Sposta un file.
     *
     * @param oldFileName Il vecchio nome del File in questione
     * @param newFileName Il nuovo nome del File in questione
     * @return true - se l'operazione ha successo
     */
    public static boolean fileMove(String oldFileName, String newFileName) {
        try {
            File ff = new File(oldFileName);
            return ff.renameTo(new File(newFileName));
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Esegue la copia di oldFileName su newFileName
     *
     * @param oldFileName Il file sorgente
     * @param newFileName Il file destinazione
     * @return true se l'operazione ha avuto successo, false altrimenti
     */
    public static boolean fileCopy(String oldFileName, String newFileName) {
        return fileCopy(new File(oldFileName), new File(newFileName));
    }

    /**
     * Esegue la copia di oldFile su newFile
     *
     * @param oldFile Il file sorgente
     * @param newFile Il file destinazione
     * @return true se l'operazione ha avuto successo, false altrimenti
     */
    public static boolean fileCopy(File oldFile, File newFile) {
        FileInputStream fis;
        FileOutputStream fos;
        try {
            fis = new FileInputStream(oldFile);
            fos = new FileOutputStream(newFile);
            byte[] buf = new byte[1024];
            int i;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
            fis.close();
            fos.close();
        } catch (IOException e) {
            log.severe("fileCopy(" + oldFile + " -> " + newFile + ") - " + e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    //--------------------------------------------------------------------------
    //--------- Operazioni sulle directories
    //--------------------------------------------------------------------------

    /**
     * Crea TUTTO l'albero delle directory passato come parametro, in maniera
     * ricorsiva.
     *
     * @param DirName Deve essere il nome di una Directory. Se venisse passato
     *                c:\miadir\pippo.txt verrebbe creata una cartella pippo.txt in c:\miadir.
     * @return true se l'operazione ha successo, false altrimenti
     */
    public static boolean mkDir(String DirName) {
        File ff = new File(DirName);
        return ff.mkdirs();
    }

    /**
     * Crea TUTTO l'albero delle directory passato come parametro, in maniera
     * ricorsiva. Escludendo però un nome di file passato nel parametro
     *
     * @param FileName Il nome del file da cui verrà estratta la cartella da
     *                 creare: es: c:\miadir\pippo.txt farà creare la cartella c:\miadir
     * @return true se l'operazione ha successo, false altrimenti
     */
    public static boolean mkDirFromFileName(String FileName) {
        // int lastSeparatorIndex = FileName.lastIndexOf(File.pathSeparator);
        int lastSeparatorIndex = FileName.lastIndexOf("\\");
        if (lastSeparatorIndex == -1) {
            lastSeparatorIndex = FileName.lastIndexOf("/");
        }
        FileName = FileName.substring(0, lastSeparatorIndex);
        File ff = new File(FileName);
        return ff.mkdirs();
    }

    /**
     * Questo metodo cancella tutti i file e le directory e le sottocose di una
     * directory passata come parametro.
     * <h2>Attenzione: <b>USARE CON MOLTA CAUTELA!</b></h2>
     *
     * @param directoryName Il nome della directory da cancellare insieme al suo
     *                      contenuto
     * @return true se l'operazione ha successo, false altrimenti
     */
    public static boolean deleteDirectoryRecursive(String directoryName) {
        File dir = new File(directoryName);

        // to see if this directory is actually a symbolic link to a directory,
        // we want to get its canonical path - that is, we follow the link to
        // the file it's actually linked to
        File candir;
        try {
            candir = dir.getCanonicalFile();
        } catch (IOException e) {
            return false;
        }

        // a symbolic link has a different canonical path than its actual path,
        // unless it's a link to itself
        if (!candir.equals(dir.getAbsoluteFile())) // this file is a symbolic link, and there's no reason for us to
        // follow it, because then we might be deleting something outside of
        // the directory we were told to delete
        {
            return false;
        }

        // now we go through all of the files and subdirectories in the
        // directory and delete them one by one
        File[] files = candir.listFiles();
        if (files != null) {
            for (File file : files) {
                // in case this directory is actually a symbolic link, or it's
                // empty, we want to try to delete the link before we try
                // anything
                boolean deleted = file.delete();
                if (!deleted) // deleting the file failed, so maybe it's a non-empty
                // directory
                {
                    if (file.isDirectory()) {
                        deleteDirectoryRecursive(file.getPath());
                    }
                }
            }
        }

        // now that we tried to clear the directory out, we can try to delete it again
        return dir.delete();
    }
    //--------------------------------------------------------------------------
    //--------- Informazioni sui files
    //--------------------------------------------------------------------------

    /**
     * Restituisce il nome del file o della directory presente in filePath
     *
     * @param filePath Il path del file o della directory
     * @return il nome del file o della directory
     */
    public static String getFileName(String filePath) {
        File ff = new File(filePath);
        return ff.getName();
    }

    /**
     * Restituisce la dimensione del file.
     *
     * @param FileName Il nome del File in questione
     * @return Un long che indica la dimensione del File.
     */
    public static long getFileSize(String FileName) {
        if (!fileExists(FileName)) {
            return -1;
        }
        return new File(FileName).length();
    }

    /**
     * Restituisce il path assoluto associato al file o alla directory
     *
     * @param fileName Il nome del file o della directory
     * @return il path assoluto associato al file o alla directory
     */
    public static String getAbsolutePath(String fileName) {
        File ff = new File(fileName);
        return ff.getAbsolutePath();
    }

    /**
     * Restituisce il path relativo associato al file o alla directory
     *
     * @param FileName Il nome del file o della directory
     * @return il path relativo associato al file o alla directory
     */
    public static String getPath(String FileName) {
        File ff = new File(FileName);
        return ff.getPath();
    }

    /**
     * Restituisce il path corrente senza il separatore finale
     *
     * @return Il percorso assoluto del path corrente
     */
    public static String getCurrentPath() {
        return System.getProperty("user.dir");
    }

    /**
     * Gets a {@link File} from a String that represents file path which is
     * relative to current Class Path
     *
     * @param path        Path to File
     * @param classLoader {@link ClassLoader} that should be able to see the
     *                    file
     * @return {@link File} object or null if nothing is found
     */
    public static File getClassPathFile(final String path, final ClassLoader classLoader) {
        final URL url = classLoader.getResource(path);
        if (url == null) {
            return null;
        }
        return new File(url.getFile());
    }

    /**
     * Gets a {@link File} form a String that represents file path which is
     * relative to current Class Path. Uses system's default ClassLoader.
     *
     * @param path Path to File
     * @return {@link File} object or null if nothing is found
     * @see #getClassPathFile(String, ClassLoader)
     */
    public static File getClassPathFile(final String path) {
        return getClassPathFile(path, ClassLoader.getSystemClassLoader());
    }

    /**
     * Se fileName � una directory, restituisce la directory assoluta altrimenti
     * restituisce la directory assoluta associata al percorso del file
     *
     * @param fileName The File Name
     * @return Il nome della Directory (senza l'ultimo '/' )
     */
    public static String getAbsoluteDirectory(String fileName) {
        File ff = new File(fileName);
        String absDir = ff.getAbsolutePath();
        if (ff.isDirectory()) {
            return absDir;
        }
        ///-{ Altrimenti è un file, dunque:
        absDir = absDir.substring(0, absDir.indexOf(ff.getName()) - 1);
        return absDir;
    }

    //--------------------------------------------------------------------------
    //--------- Recupero di Files e Directories
    //--------------------------------------------------------------------------

    /**
     * Restituisce tutti i files di quella directory partendo dal percorso
     * relativo ad esempio se chiedi: FileManager.getFilesRecursive("build");
     * <pre>
     * build\classes\Samples\Graphics\frmTpcContainer$2.class
     * build\classes\Samples\Graphics\frmTpcContainer.class
     * build\classes\Samples\Music
     * build\classes\Samples\Music\Music.txt
     * [...]
     * </pre>
     * <p>
     * se chiedi: FileManager.getFilesRecursive("c:/build");
     * <pre>
     * c:\build\classes\Samples\Music
     * c:\build\classes\Samples\Music\Music.txt
     * [...]
     * </pre>
     *
     * @param directory Il nome della directory da cui estrarre la lista di
     *                  files
     * @return la lista dei files contenuti nella directory e sottodirectory
     */
    public static String[] getFilesRecursive(String directory) {
        ArrayList<File> files = getFilesRecursive(new File(directory));
        String[] sFiles = new String[files.size()];

        for (int i = 0; i < files.size(); i++) {
            sFiles[i] = files.get(i).getPath();
        }
        return sFiles;
    }

    /**
     * Restituisce tutti i files di quella directory partendo dal percorso
     * relativo
     *
     * @param aStartingDir Il nome della directory da cui estrarre la lista di
     *                     files
     * @return la lista dei files contenuti nella directory e sottodirectory
     * @see FileManager#getFilesRecursive(String directory) getFilesRecursive
     */
    public static ArrayList<File> getFilesRecursive(File aStartingDir) {
        ArrayList<File> result = new ArrayList<>();

        if (aStartingDir == null) {
            log.severe("getFileRecursive(" + aStartingDir + "): Directory should not be null.");
            return result;
        }
        if (!aStartingDir.exists()) {
            log.severe("getFileRecursive(" + aStartingDir + "): IperUnits FileManager: Directory does not exist.");
            return result;
        }
        if (!aStartingDir.isDirectory()) {
            log.severe("getFileRecursive(" + aStartingDir + "): Is not a directory.");
            return result;
        }
        if (!aStartingDir.canRead()) {
            log.severe("getFileRecursive(" + aStartingDir + "): Directory cannot be read.");
            return result;
        }

        File[] filesAndDirs = aStartingDir.listFiles();
        if (filesAndDirs == null) return result;
        ArrayList<File> filesDirs = new ArrayList<>(Arrays.asList(filesAndDirs));

        filesDirs.stream().map(file -> {
            result.add(file); //always add, even if directory
            return file;
        }).filter(file -> (!file.isFile())).map(FileManager::getFilesRecursive).forEachOrdered(deeperList -> {
            //must be a directory, recursive call!
            result.addAll(deeperList);
        });
        Collections.sort(result);
        return result;
    }

    /**
     * Restituisce tutti i file presenti nella directory data Se la directory
     * passata non è una directory resituisce null
     *
     * @param directory la directory dove cercare i file con o senza il
     *                  separatore finale
     * @param absolute  Indica se si vuole restituito il percorso assoluto
     * @return uno string[] contenente la lista completa dei files presenti con
     * il loro percorso assoluto o non.
     */
    public static String[] getFiles(String directory, boolean absolute) {
        File dirRoot = new File(directory);
        String dirPath = dirRoot.getAbsolutePath();
        if (!dirPath.endsWith(File.separator)) {
            dirPath += File.separator;
        }
        String[] sFiles = dirRoot.list();

        ///-{ Significa che il path non � un path corretto
        if (sFiles == null) {
            return null;
        }

        if (absolute) {
            for (int i = 0; i < sFiles.length; i++) {
                sFiles[i] = dirPath + sFiles[i];
            }
        }
        return sFiles;
    }

    /**
     * Restituisce i nomi dei files che all'interno del nome contengono quelle
     * sequenza di caratteri.
     *
     * @param directory la directory dove cercare i file, con o senza separatore
     * @param absolute  Absolute Path
     * @param filters   I filtri utilizzati (usa "... ... ...".split(" "));
     * @return uno string[] contenente la lista dei nomi dei files selezionati
     */
    public static String[] getFiles(String directory, boolean absolute, String[] filters) {
        String[] myFiles = FileManager.getFiles(directory, absolute);
        if (myFiles == null) return new String[]{};
        ArrayList<String> myFilteredFiles = new ArrayList<>();

        for (int i = 0; i < myFiles.length; i++) {
            myFiles[i] = FileManager.getFileName(myFiles[i]);

            if (containsPattern(myFiles[i], filters)) {
                myFilteredFiles.add(myFiles[i]);
            }
        }

        /// String[] output = (String[])myFilteredFiles.toArray(); // Non valido :(
        String[] filteredFiles = new String[myFilteredFiles.size()];
        for (int i = 0; i < myFilteredFiles.size(); i++) {
            filteredFiles[i] = myFilteredFiles.get(i);
        }

        return filteredFiles;
    }

    /**
     * Recupera la lista dei file (non ricorsiva) su quel resourcePath di
     * risorse
     *
     * @param parent       La classe che fa la richiesta e che ha le risorse nel jar
     * @param resourcePath Il resourcePath delle risorse, e.g.,
     *                     /app_pcillo/plugins/PP_Testi (no final slash)
     * @return la liste dei file disponibili
     */
    public static ArrayList<String> getFilesResources(Object parent, String resourcePath) {
        ArrayList<String> filenames = new ArrayList<>();
        try {

            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
            if (in == null) {
                in = parent.getClass().getResourceAsStream(resourcePath);
                if (in == null) return filenames;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String resource;
            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return filenames;
    }

    /**
     * Recupera tutte le risorse in un dato path (non ricorsivo), trattandoli
     * come file di testo, li carica in un dizionario che ha key = nome file, e
     * value = il contenuto.
     *
     * @param parent       The Parent
     * @param resourcePath Il resourcePath delle risorse, e.g.,
     *                     /app_pcillo/plugins/PP_Testi (no final slash)
     * @return Una mappa con i contenuti
     */
    public static HashMap<String, String> getFilesResourcesContent(Object parent, String resourcePath) {
        ArrayList<String> filesResources = getFilesResources(parent, resourcePath);
        HashMap<String, String> contents = new HashMap<>();
        filesResources.forEach(fileResourcePath -> {
            String content = FileManager.getAssetString(parent, resourcePath + "/" + fileResourcePath);
            contents.put(fileResourcePath, content);
        });
        return contents;
    }

    /**
     * Restituisce tutti i file presenti nella directory data Se la directory
     * passata non è una directory resituisce null
     *
     * @param directory la directory dove cercare i file con o senza il
     *                  separatore finale
     * @param absolute  true if absolute
     * @param ext       l'estensione comprensiva di '.'
     * @return uno string[] contenente la lista completa dei files presenti con
     * il loro percorso assoluto o non!
     */
    public static String[] getFilesWithExt(String directory, boolean absolute, String ext) {
        String[] sFiles = getFiles(directory, absolute);
        if (sFiles == null) {
            return null;
        }

        ArrayList<String> myFiles = new ArrayList<>();
        for (String s : sFiles) {
            if (s.endsWith(ext)) {
                myFiles.add(s);
            }
        }

        sFiles = new String[myFiles.size()];
        for (int i = 0; i < myFiles.size(); i++) {
            sFiles[i] = myFiles.get(i);
        }

        return sFiles;
    }

    /**
     * @param directory La directory di partenza
     * @param absolute  Indica se si vuole restituito il percorso assoluto
     * @return Le directory (non ricorsive)
     */
    public static ArrayList<String> getDirs(String directory, boolean absolute) {
        File dirRoot = new File(directory);
        File[] files = dirRoot.listFiles();

        ///-{ Significa che il path non � un path corretto
        if (files == null) {
            return null;
        }

        ArrayList<String> myDirs = new ArrayList<>();

        for (File f : files) {
            if (f.isDirectory()) {
                myDirs.add(absolute ? f.getAbsolutePath() : f.getPath());
            }
        }

        return myDirs;
    }

    //--------------------------------------------------------------------------
    //--------- Utilities
    //--------------------------------------------------------------------------

    /**
     * Estrae il File dalla risorsa di nome resource name (Questa può essere
     * incorporata all'interno delle cartelle di classi o incastonata durante la
     * compilazione del file .jar)
     * <p>
     * es: "/UltraUnits/Utilities/Verbs.txt"
     * <p>
     * Nota: Questo metodo a volte da problemi di URI NOT Hierarchical, in quel
     * caso utilizzare l'altro getAssetString!
     *
     * @param parent       Il parent (di solito 'this')
     * @param resourcePath il nome della risorsa (ovvero dei packages separati
     *                     da / con percorso relativo)
     * @return il File corrispondente alla risorsa
     */
    public static File getAsset(Object parent, String resourcePath) {
        // ClassLoader cl = parent.getClass().getClassLoader();
        Class<?> cl = parent.getClass();

        File f = null;
        try {
            f = new File(Objects.requireNonNull(cl.getResource(resourcePath)).toURI());
        } catch (URISyntaxException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return f;
    }

    /**
     * Questo metodo restituisce un InputStream a partire dal resource.
     * Restituire un InputStream serve ad evitare il problema del URI Not
     * Hierarchical Exception. Questo cavolo di problema forse si può risolvere
     * adattando l'URI al percorso nel jar (o qualcosa del genere). Che schifo,
     * per ora mi accontento di questo. Attenzione con Maven, inserire le
     * risorse nella apposita cartella delle risorse (resources).
     *
     * @param parent       Il parent che fa la chiamara, purtroppo è necessario (altro
     *                     schifo)
     * @param resourcePath Il nome della risorsa e.g. "/org/images/cippa.gif"
     * @return Restituisce l'InputStream associato alla risorsa
     */
    public static InputStream getAssetInputStream(Object parent, String resourcePath) {
        return parent.getClass().getResourceAsStream(resourcePath);
    }

    /**
     * Restituisce una risorsa (dalle resources) in formato Stringa. Vedi
     * getInputStreamFromResource(...). Supporta l'UTF-8! Attenzione.
     *
     * @param parent       NON SONO SICURO, ma dovrebbe essere un oggetto che si trova
     *                     nel jar con la risorsa in questione
     * @param resourcePath Il nome della risorsa, e.g.,
     *                     "/UltraUnits/Utilities/Verbs.txt"
     * @return Il contenuto della risorsa in formato testo, null se la risorsa
     * non esiste
     */
    public static String getAssetString(Object parent, String resourcePath) {
        // Object parent = null; // Prima veniva passato.
        if (parent == null) {
            parent = new Object();
        }
        InputStream is = parent.getClass().getResourceAsStream(resourcePath);
        // InputStreamReader ir = new InputStreamReader(is); // Se non si vuol gestire l'UTF-8
        if (is == null) {
            return null;
        }
        InputStreamReader ir = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader in = new BufferedReader(ir);
        StringBuilder str = new StringBuilder();
        char[] buffer = new char[2048];
        int r;
        try {
            while ((r = in.read(buffer)) > 0) {
                str.append(buffer, 0, r);
            }
            in.close();
        } catch (IOException ex) {
            log.severe(resourcePath + " not found in resources - " + ex.getLocalizedMessage());
        }
        return str.toString();
    }

    /**
     * Restituisce una risorse in formato Binario.
     *
     * @param parent       The parent resource
     * @param resourcePath The resourcePath
     * @return bytes.
     */
    public static byte[] getAssetBytes(Object parent, String resourcePath) {
        // Attenzione parent.getClass().getClassLoader() invece vuole il path relativo!
        try {
            return parent.getClass().getResourceAsStream(resourcePath).readAllBytes();
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Indica se gli elementi del pattern sono contenuti nella stringa
     *
     * @param string  La String in ingresso
     * @param pattern Gli elementi del pattern (in AND)
     * @return true se i pattern sono contenuti, false altrimenti
     */
    public static boolean containsPattern(String string, String[] pattern) {
        for (String fil : pattern) {
            if (!string.contains(fil)) {
                return false;
            }
        }
        return true;
    }

    public static String getCRC32(String file_path) {
        InputStream inputStream = null;
        CRC32 crc = new CRC32();
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file_path));

            int cnt;
            while ((cnt = inputStream.read()) != -1) {
                crc.update(cnt);
            }
            inputStream.close();
            return Long.toHexString(crc.getValue());
        } catch (IOException ex) {
            // Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ex) {
                // Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "";
    }

}
