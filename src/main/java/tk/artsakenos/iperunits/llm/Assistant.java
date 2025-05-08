package tk.artsakenos.iperunits.llm;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.Setter;
import tk.artsakenos.iperunits.serial.Jsonable;
import tk.artsakenos.iperunits.web.SuperHttpClient;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static tk.artsakenos.iperunits.string.SuperDate.now;


/**
 * Mette a disposizione dei metodi di default che si basano sullo standard OpenAI che vale per molti LLM.
 * Altrimenti si possono sovrascrivere all'occorrenza.
 */
@Data
public abstract class Assistant {

    private String provider;
    private String model;
    private String description;

    private long lastCallTimestamp = now();
    private long coolDownSeconds = 60;
    private long contextWindowSize = 4096;

    @Setter
    private String apikey;

    public String getId() {
        return provider + "::" + model;
    }

    public abstract String getEndpoint(Conversation conversation);


    public Map<String, String> getHeaders() {
        return Map.of("Authorization", "Bearer " + apikey);
    }

    public String getJsonRequest(Conversation conversation) {
        List<Map<String, Object>> messagesEnvelope = new LinkedList<>();
        for (Message message : conversation) {
            Message.Role role = message.getRole();
            if (role == Message.Role.error || role == Message.Role.context) continue;
            messagesEnvelope.add(Map.of("role", role, "content", message.getText()));
        }
        Map<String, Object> requestObject = Map.of(
                "model", getModel(),
                "messages", messagesEnvelope,
                "temperature", 1,
                // "max_tokens", 1024,
                // "top_p", 1,
                "presence_penalty", 0
        );

        return Jsonable.toJson(requestObject, true);
    }

    public Message getAnswer(SuperHttpClient.SuperResponse superResponse) {
        JsonNode rootNode = Jsonable.toJsonNode(superResponse.getBody());
        String id = rootNode.get("id").asText();
        String model = rootNode.get("model").asText();
        long created = rootNode.get("created").asLong();
        if (!superResponse.isSuccessful()) {
            String error = superResponse.getBody();
            return new Message(this, Message.Role.error, Map.of(Message.Type.text, error));
        }
        // String answer = rootNode.get("choices").get(0).get("message").get("content").asText();
        String answer = rootNode.at("/choices/0/message/content").asText();
        return new Message(this, Message.Role.assistant, Map.of(Message.Type.text, answer));
    }

}
