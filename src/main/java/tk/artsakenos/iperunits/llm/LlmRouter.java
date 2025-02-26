package tk.artsakenos.iperunits.llm;

import lombok.Data;
import lombok.extern.java.Log;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import static tk.artsakenos.iperunits.string.SuperDate.now;

@Data
@Log
public class LlmRouter {

    private final LinkedHashMap<String, Assistant> assistants = new LinkedHashMap<>();
    private final Map<String, Conversation> userConversations = new ConcurrentHashMap<>();
    private final Map<String, Long> lastQueryTimestamps = new ConcurrentHashMap<>();
    private final Map<String, Integer> userQueryCounts = new ConcurrentHashMap<>();

    // Configurazioni
    private long minQueryIntervalSeconds = 5;  // Tempo minimo tra una query e l'altra
    private int maxQueriesPerUser = 100;       // Numero massimo di query per utente
    private boolean persistConversations = true; // Se mantenere le conversazioni per utente

    public LlmRouter() {
    }

    /**
     * Costruttore con lista di assistenti
     *
     * @param assistantList Lista di assistenti da aggiungere al router
     */
    public LlmRouter(List<Assistant> assistantList) {
        for (Assistant assistant : assistantList) {
            addAssistant(assistant);
        }
    }

    /**
     * Aggiunge un assistente al router
     *
     * @param assistant L'assistente da aggiungere
     */
    public void addAssistant(Assistant assistant) {
        String assistantId = assistant.getId();
        assistants.put(assistantId, assistant);
    }

    /**
     * Esegue una query a un LLM per un determinato utente
     *
     * @param userId   ID dell'utente che effettua la query
     * @param question Domanda dell'utente
     * @return Risposta dell'LLM o messaggio di errore
     */
    public String query(String userId, String question) {
        // Verifica limiti di utilizzo
        if (maxQueriesPerUser > 0 && userQueryCounts.getOrDefault(userId, 0) >= maxQueriesPerUser) {
            return "Hai raggiunto il limite massimo di query consentite.";
        }

        // Verifica intervallo minimo tra query
        long lastQueryTime = lastQueryTimestamps.getOrDefault(userId, 0L);
        long currentTime = now();
        if (minQueryIntervalSeconds > 0 && (currentTime - lastQueryTime) < (minQueryIntervalSeconds * 1000)) {
            return "Devi attendere prima di effettuare una nuova query.";
        }

        // Ottieni o crea la conversazione dell'utente (inline)
        Conversation conversation = persistConversations
                ? userConversations.computeIfAbsent(userId, k -> new Conversation())
                : new Conversation();

        // Aggiungi la domanda dell'utente alla conversazione
        if (!assistants.isEmpty()) {
            Assistant primaryAssistant = assistants.values().iterator().next();
            conversation.add(new Message(primaryAssistant, Message.Role.user, Map.of(Message.Type.text, question)));
        } else {
            log.severe("Nessun assistente disponibile nel router");
            return "Errore: Nessun assistente LLM configurato";
        }

        // Aggiorna contatori e timestamp
        userQueryCounts.put(userId, userQueryCounts.getOrDefault(userId, 0) + 1);
        lastQueryTimestamps.put(userId, now());

        // Tenta di ottenere una risposta dagli assistenti in ordine
        for (Assistant assistant : assistants.values()) {
            try {
                LlmService service = new LlmService();

                // Trasferisci la conversazione dell'utente al servizio LLM
                for (Message message : conversation) {
                    service.getConversation().add(message);
                }

                // Esegui la query
                Message response = service.query();

                if (response != null) {
                    // Aggiungi la risposta alla conversazione persistente dell'utente
                    if (persistConversations) {
                        conversation.add(response);
                    }
                    return response.getText();
                }
            } catch (Exception e) {
                String errorMessage = "Errore con assistente " + assistant.getId() + ": " + e.getMessage();
                log.log(Level.SEVERE, errorMessage, e);

                // Se l'errore è dovuto a un rate limit, prova con il prossimo assistente
                String message = e.getMessage().toLowerCase();
                boolean isRateLimit = message.contains("rate limit") ||
                                      message.contains("ratelimit") ||
                                      message.contains("too many requests") ||
                                      message.contains("429");

                if (isRateLimit) {
                    log.info("Rate limit raggiunto per " + assistant.getId() + ", tentativo con il prossimo assistente...");
                    continue;
                }

                // Se l'errore non è di rate limit e siamo all'ultimo assistente, ritorna un messaggio di errore
                if (assistant.equals(assistants.values().toArray()[assistants.size() - 1])) {
                    return "Si è verificato un errore durante l'elaborazione della richiesta: " + e.getMessage();
                }
            }
        }

        return "Tutti gli assistenti LLM sono attualmente non disponibili. Riprova più tardi.";
    }

    /**
     * Resetta i contatori per un utente specifico
     *
     * @param userId ID dell'utente da resettare
     */
    public void resetUserCounters(String userId) {
        userQueryCounts.remove(userId);
        lastQueryTimestamps.remove(userId);
        if (persistConversations) {
            userConversations.remove(userId);
        }
    }

    /**
     * Resetta i contatori per tutti gli utenti
     */
    public void resetAllCounters() {
        userQueryCounts.clear();
        lastQueryTimestamps.clear();
        if (persistConversations) {
            userConversations.clear();
        }
    }

    /**
     * Svuota la conversazione di un utente
     *
     * @param userId ID dell'utente
     */
    public void clearUserConversation(String userId) {
        if (userConversations.containsKey(userId)) {
            userConversations.get(userId).clear();
        }
    }
}