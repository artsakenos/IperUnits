package tk.artsakenos.iperunits.llm;

import lombok.Getter;
import tk.artsakenos.iperunits.web.SuperHttpClient;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Wrapper per avviare e gestire istanze del server llama.cpp.
 * Comunica tramite API HTTP, garantendo compatibilità su diversi sistemi operativi.
 */
public class LlamaJava {
    private Process process;
    /**
     * -- GETTER --
     *  Restituisce la porta su cui è in esecuzione il server.
     *
     * @return numero della porta
     */
    @Getter
    private final int port;
    private final String llamaExePath;
    private final String modelPath;
    private final List<String> extraArgs;

    public LlamaJava(String llamaExePath, String modelPath, int port, String... extraArgs) {
        this.llamaExePath = llamaExePath;
        this.modelPath = modelPath;
        this.port = port;
        this.extraArgs = Arrays.asList(extraArgs);
    }

    /**
     * Inizializza e avvia il server llama.cpp.
     *
     * @throws IOException se il server non può essere avviato
     */
    public void init() throws IOException {
        File llamaExe = new File(llamaExePath);
        if (!llamaExe.exists() || !llamaExe.canExecute()) {
            throw new FileNotFoundException("Llama.cpp server non disponibile: " + llamaExePath);
        }

        if (isRunning()) {
            throw new IllegalStateException("Server già in esecuzione sulla porta " + port);
        }

        List<String> command = new ArrayList<>(Arrays.asList(
                llamaExe.getAbsolutePath(),
                "-m", modelPath,
                "--host", "0.0.0.0",
                "--port", String.valueOf(port)
        ));
        command.addAll(extraArgs);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        process = pb.start();

        // Thread per logging dell'output del processo
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[llama-" + port + "] " + line);
                }
            } catch (IOException e) {
                System.err.println("Errore nel leggere l'output del server: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Ferma il server in esecuzione.
     */
    public void stop() {
        if (isRunning()) {
            process.destroy();
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Verifica se il server è in esecuzione.
     *
     * @return true se il server è attivo, false altrimenti
     */
    public boolean isRunning() {
        return process != null && process.isAlive();
    }

    /**
     * Invia una richiesta sincrona al server llama.cpp.
     *
     * @param jsonBody corpo della richiesta in formato JSON
     * @return risposta del server
     * @throws RuntimeException se la richiesta fallisce
     */
    public String query(String jsonBody) {
        SuperHttpClient client = SuperHttpClient.builder()
                .baseurl("http://localhost:" + port)
                .build();

        SuperHttpClient.SuperResponse response = client.postJson("/completion", jsonBody, null, null);

        if (!response.isSuccessful()) {
            throw new RuntimeException("Errore nella query: " + response);
        }

        return response.getBody();
    }

    /**
     * Invia una richiesta asincrona al server llama.cpp.
     * Da riscrivere, per implementare lo streaming.
     *
     * @param jsonBody corpo della richiesta in formato JSON
     * @return CompletableFuture con la risposta del server
     */
    public CompletableFuture<String> queryAsync(String jsonBody) {
        return CompletableFuture.supplyAsync(() -> query(jsonBody));
    }

}