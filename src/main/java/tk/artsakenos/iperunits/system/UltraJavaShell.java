/**
 * Copyright (C) InfoDev, Andrea Addis. ---------------------------------------
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */
package tk.artsakenos.iperunits.system;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Andrea
 * @version 2018-10-23
 */
@SuppressWarnings("unused")
public abstract class UltraJavaShell {

    protected static final String QUIT = "quit";
    private static final String QUIT_FORCE = "quit_force";
    private static final String HELP = "help";
    private static final String HISTORY = "hist";
    private static final String PROMPT = "UJS> ";
    private static final ArrayList<String> HISTORY_LIST = new ArrayList<>();
    private String lastCommand = "";

    public String getWelcome() {
        return "\n" + "     ------------------------------------------------------------------------\n"
               + "     -- Welcome to UltraJavaShell v.16.06. \n" + "     -- Type '" + QUIT + "' to leave the shell. \n"
               + "     -- Type '" + QUIT_FORCE + "' to force close the shell. \n" + "     -- Type '" + HELP
               + "' to see the available commands. \n" + "     -- Type '" + HISTORY + "' to see the commands used. \n"
               + "     ------------------------------------------------------------------------\n";
    }

    @SuppressWarnings("ConvertToTryWithResources")
    public void start() {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        System.out.println(getWelcome());
        System.out.println(getCommandDescription());

        String command = "";
        while (!command.equals(QUIT)) {
            System.out.print(PROMPT);
            command = scanner.nextLine();

            if (command.equals(HELP)) {
                System.out.println(getWelcome());
                System.out.println(getCommandDescription());
            }
            if (command.equals(QUIT_FORCE)) {
                System.out.println("UltraJavaShell, force closing");
                System.exit(123456789);
            }
            if (command.equals(HISTORY)) {
                for (int i = 0; i < HISTORY_LIST.size(); i++) {
                    System.out.println("[" + i + "] " + HISTORY_LIST.get(i));
                }
            }
            try {
                lastCommand = command;
                onCommand(command);
                HISTORY_LIST.add(command);
            } catch (Exception e) {
                System.err.println("[UltraJavaShell] Errore " + e.getClass().getSimpleName()
                                   + " durante l'esecuzione del comando " + "<" + command + ">: " + e.getLocalizedMessage());
                Logger.getLogger(UltraJavaShell.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        scanner.close();
    }

    /**
     * Controlla se nel lastCommand i primi arg sono quelli passati in
     * quell'ordine
     *
     * @param args Gli args da controllare
     * @return true se i primi args combaciano con il lastCommand
     */
    protected final boolean args(String... args) {
        String[] lastArgs = lastCommand.split(" ");
        if (lastArgs.length < args.length) {
            return false;
        }

        for (int i = 0; i < args.length; i++) {
            if (!lastArgs[i].equals(args[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Restituisce l'i-esimo arg
     *
     * @param i l'indice dell'arg
     * @return l'i-esimo arg, null se non esiste l'i-esimo.
     */
    protected final String arg(int i) {
        String[] args = lastCommand.split(" ");
        if (i >= args.length) {
            return null;
        }
        return args[i];
    }

    /**
     * Restituisce tutti gli args presenti dopo l'i-esimo in unìunica stringa.
     *
     * @param i l'i-esimo arg.
     * @return gli args presenti topo l'iesimo. e.g., "pippo con topolino e
     * minni", args(2) == topolino e minni
     */
    protected final String args(int i) {
        String[] args = lastCommand.split(" ");
        if (i >= args.length) {
            return null;
        }
        StringBuilder output = new StringBuilder();
        for (; i < args.length; i++) {
            output.append(args[i]).append(" ");
        }
        return output.toString().trim();
    }

    /**
     * Restituisce una descrizione dei comandi aggiuntivi. Vanno inseriti come
     * lista, stile <i>man</i>.
     *
     * @return una lista dei comandi aggiuntivi disponibili.
     */
    public abstract String getCommandDescription();

    /**
     * Operazioni da eseguire al comando chiamato.
     *
     * @param command The Command
     */
    public abstract void onCommand(String command);

}
