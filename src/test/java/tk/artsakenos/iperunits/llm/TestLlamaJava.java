package tk.artsakenos.iperunits.llm;

import org.junit.jupiter.api.*;
import tk.artsakenos.iperunits.system.SuperTimer;

import java.io.File;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class TestLlamaJava {
    private static final String LLAMA_PATH = System.getenv().getOrDefault("LLAMA_SERVER_PATH", "C:\\llama_cpp\\llama-server.exe");
    private static final String MODEL_PATH = System.getenv().getOrDefault("LLAMA_MODEL_PATH", "C:\\llama_cpp\\models\\Qwen3-0.6B-IQ4_NL.gguf");
    private static final int TEST_PORT = 8765;
    private static final Duration SERVER_STARTUP_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration SERVER_QUERY_TIMEOUT = Duration.ofSeconds(15);

    private LlamaJava server;

    @BeforeEach
    void setUp() {
        File llamaExe = new File(LLAMA_PATH);
        File modelFile = new File(MODEL_PATH);
        Assumptions.assumeTrue(llamaExe.exists() && llamaExe.canExecute(), "llama.cpp server non trovato");
        Assumptions.assumeTrue(modelFile.exists(), "Modello .gguf non trovato");
    }

    @Test
    void testServerStartupAndQuery() throws Exception {
        // Inizializzazione del server
        server = new LlamaJava(LLAMA_PATH, MODEL_PATH, TEST_PORT);
        assertTimeout(SERVER_STARTUP_TIMEOUT, () -> {
            server.init();
            // Attendi che il server sia pronto
            int attempts = 0;
            while (!server.isRunning() && attempts < 50) {
                Thread.sleep(200);
                attempts++;
            }
        }, "Il server non si è avviato nel tempo previsto");

        // Verifica che il server sia in esecuzione
        SuperTimer.sleep(10_000);
        assertTrue(server.isRunning(), "Il server dovrebbe essere attivo dopo l'inizializzazione");

        // Query di test più generica
        String jsonRequest = """
            {
              "prompt": "Rispondi brevemente: Qual è la capitale della Francia?",
              "n_predict": 20,
              "temperature": 0.2,
              "stop": ["\\n"]
            }
            """;

        // Esegui la query con timeout e debug
        try {
            String response = assertTimeout(SERVER_QUERY_TIMEOUT, () -> {
                System.out.println("Esecuzione query al server...");
                String resp = server.query(jsonRequest);
                System.out.println("Risposta ricevuta: " + resp);
                return resp;
            }, "La query non ha ricevuto risposta in tempo");

            // Verifica della risposta
            assertNotNull(response, "La risposta non dovrebbe essere nulla");
            assertTrue(
                    response.toLowerCase().contains("parigi") ||
                            response.toLowerCase().contains("paris"),
                    "La risposta dovrebbe contenere 'Parigi'. Risposta ricevuta: " + response
            );
        } catch (RuntimeException e) {
            // Log dell'errore dettagliato
            System.err.println("Errore durante la query: " + e.getMessage());
            e.printStackTrace();
            fail("Errore durante l'esecuzione della query: " + e.getMessage());
        }
    }

    @Test
    void testServerShutdown() throws Exception {
        // Inizializzazione del server
        server = new LlamaJava(LLAMA_PATH, MODEL_PATH, TEST_PORT);
        server.init();

        // Attendi che il server sia avviato
        int attempts = 0;
        while (!server.isRunning() && attempts < 50) {
            Thread.sleep(200);
            attempts++;
        }

        // Termina il server
        server.stop();

        // Attendi la chiusura
        Thread.sleep(1000);

        // Verifica che il server sia terminato
        assertFalse(server.isRunning(), "Il server dovrebbe essere terminato dopo stop()");
    }

    @AfterEach
    void tearDown() {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }
}