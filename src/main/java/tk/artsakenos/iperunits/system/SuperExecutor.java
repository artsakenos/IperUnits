/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.system;

import lombok.extern.java.Log;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Permette di eseguire codice su shell e maneggiare l'output. Con quello
 * statico l'output viene sputato su console e restituito a fine corsa, con
 * l'altro è possibile fare dell'output ciò che si vuole tramite il suo pasaggio
 * su onConsole(...), in più il tutto parte in un thread non bloccante.
 * <p>
 * Attenzione, i programmi in console, tipo le uci engines degli scacchi hanno
 * necessità di altre metodologie, perché si entra in un'ulteriore shell.
 *
 * @author Andrea
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
@Log
public abstract class SuperExecutor {

    public static final String SHELL_LINUX_BASH = "bash";
    public static final String SHELL_WINDOWS_CMD = "cmd";
    public static final String SHELL_WINDOWS_START = "start";

    public boolean verbose_log = true;

    private static ArrayList<String> initCommand(String shell, String command) {

        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        if (shell == null || shell.isEmpty()) {
            if (isWindows) {
                shell = SHELL_WINDOWS_CMD;
            } else {
                shell = SHELL_LINUX_BASH;
            }
        }

        final ArrayList<String> myCommand = new ArrayList<>();
        switch (shell) {
            case SHELL_LINUX_BASH:
                myCommand.add("bash");
                myCommand.add("-c");
                break;
            case SHELL_WINDOWS_CMD:
                myCommand.add("cmd.exe");
                myCommand.add("/c");
                break;
            case SHELL_WINDOWS_START:
                myCommand.add("cmd.exe");
                myCommand.add("/c");
                myCommand.add("start");
                break;
            default:
                log.info("Please specify a correct Shell Type");
                return null;
        }

        myCommand.add(command);
        return myCommand;
    }

    /**
     * Esegue un comando, mostra l'output a console e lo restituisce.
     *
     * @param id      l'id dell'executor, nel caso se ne vogliano fare diversi
     * @param shell   null o vuoto se automatico, altrimenti
     *                <ol>
     *                <li>bash - linux: bash -c ...</li>
     *                <li>cmd - windows: cmd /c ...</li>
     *                <li>start - windows start: cmd /c start ...</li>
     *                </ol>
     * @param command il comando, e.g., ping -3 google.com, dir, ...
     */
    public void execute(String id, String shell, String command) {
        final ArrayList<String> myCommand = initCommand(shell, command);
        final ProcessBuilder processBuilder = new ProcessBuilder();
        assert myCommand != null;
        processBuilder.command(myCommand);

        new Thread(() -> {
            try {
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    onConsole(id, line);
                }

                int exitCode = process.waitFor();
                if (verbose_log) {
                    log.info("Exited with error code : " + exitCode);
                }
            } catch (IOException | InterruptedException e) {
                log.info("Something went wrong: " + e.getLocalizedMessage());
            }
        }).start();

    }

    /**
     * Esegue un comando, mostra l'output a console e lo restituisce.
     *
     * @param shell   null o vuoto se automatico, altrimenti
     *                <ol>
     *                <li>bash - linux: bash -c ...</li>
     *                <li>cmd - windows: cmd /c ...</li>
     *                <li>start - windows start: cmd /c start ...</li>
     *                </ol>
     * @param command il comando, e.g., ping -3 google.com, dir, ...
     * @return L'output del processo.
     */
    public static String execute(String shell, String command) {
        ArrayList<String> myCommand = initCommand(shell, command);

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectOutput(Redirect.INHERIT);
        processBuilder.redirectError(Redirect.INHERIT);
        assert myCommand != null;
        processBuilder.command(myCommand);
        StringBuilder output = new StringBuilder();

        try {
            String line;
            Process process = processBuilder.start();

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.out.println(line);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                log.info(line);
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            log.info("Exited with error code : " + exitCode);

        } catch (IOException | InterruptedException e) {
            log.info("Something went wrong: " + e.getLocalizedMessage());
        }
        return output.toString().trim();
    }

    /**
     * Apre una cartella nel sistema, in particolare la apre su windows
     * utilizzando explorer.exe, su linux con konqueror
     *
     * @param folderName The Folder Name
     */
    public static void openFolder(String folderName) {
        String userHome = System.getProperty("user.home");
        String userDir = System.getProperty("user.dir");
        String userName = System.getProperty("user.name");
        String osName = System.getProperty("os.name");

        if (osName.startsWith("Windows")) {
            execute("cmd", "explorer.exe " + folderName.replaceAll("/", "\\\\"));
        }
        if (osName.contains("Linux")) {
            // executeCommand("konqueror file://" + folderName);
            executeUrl("file://" + folderName);
        }
    }

    /**
     * Esegue un dato URL, simulando il comportamento di un browser
     *
     * @param action l'url da eseguire
     */
    public static void executeUrl(String action) {
        Desktop desk = Desktop.getDesktop();
        try {

            desk.browse(new URI(action));
        } catch (IOException ex) {
            log.severe("executeUrl(" + action + "):" + ex.getLocalizedMessage());
        } catch (URISyntaxException ex) {

            ///-{ Proviamo allora ad aprire il file
            File fb = new File(action);
            try {
                System.err.println("URI con caratteri illegali: " + action + ". Usare URLEncoder.encode(action,\"UTF-8\"))");
                if (fb.exists()) {
                    desk.open(fb);
                } else {
                    System.err.println("File " + action + " non esistente.");
                }
            } catch (IOException ex1) {
                Logger.getLogger(SuperExecutor.class.getName()).log(Level.SEVERE, null, ex1);
            }

        }

    }

    public abstract void onConsole(String id, String line);
}
