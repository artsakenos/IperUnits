package tk.artsakenos.iperunits.llm;

import lombok.Getter;
import lombok.extern.java.Log;
import tk.artsakenos.iperunits.llm.Message.Role;
import tk.artsakenos.iperunits.web.SuperHttpClient;

import java.util.Map;

import static tk.artsakenos.iperunits.llm.Message.Type;

/**
 * LLM Service utilizza la conversazione per fare una nuova query al LLM.
 * Dunque per utilizzarlo bisogna aggiornare la conversazione con
 * prompt di sistema e richieste dell'utente e poi chiamare query();
 * <p>
 * La risposta dell'assistente viene sempre aggiunta automaticamente alla conversazione.
 */
@Getter
@Log
public class LlmService {

    private final Conversation conversation = new Conversation();

    public void addMessage(Assistant assistant, Role role, String message_text) {
        conversation.add(new Message(assistant, role, Map.of(Type.text, message_text)));
    }

    public Message query() {
        Assistant assistant = conversation.getLast().getAssistant();
        SuperHttpClient client = SuperHttpClient.builder()
                .baseurl(assistant.getEndpoint(conversation))
                .build();
        String jsonRequest = assistant.getJsonRequest(conversation);
        SuperHttpClient.SuperResponse superResponse = client.postJson("", jsonRequest, assistant.getHeaders(), null);
        try {
            Message answer = assistant.getAnswer(superResponse);
            conversation.add(answer);
            return answer;
        } catch (Exception e) {
            log.severe("Qualcosa é andato storto nella deserializzazione: "
                       + e.getLocalizedMessage() + "\nResponse:\n" + superResponse + "\n");
            return null;
        }
    }

    /**
     * Fornisce una stima grossolana del numero di token.
     * Vedi ad esempio
     * <a href="https://help.openai.com/en/articles/4936856-what-are-tokens-and-how-to-count-them">questo articolo</a>.
     *
     * @return Stima del numero di token.
     */
    public static int getTokenEstimate(String text) {
        return Math.max(text.length() / 3, text.split(" ").length * 4 / 3);
    }

}
