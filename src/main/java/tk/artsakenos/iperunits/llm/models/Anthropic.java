package tk.artsakenos.iperunits.llm.models;

import com.fasterxml.jackson.databind.JsonNode;
import tk.artsakenos.iperunits.llm.Assistant;
import tk.artsakenos.iperunits.llm.Conversation;
import tk.artsakenos.iperunits.llm.Message;
import tk.artsakenos.iperunits.serial.Jsonable;
import tk.artsakenos.iperunits.web.SuperHttpClient;

import java.util.*;

import static tk.artsakenos.iperunits.llm.models.Google.toImageTypeContent;

/**
 * <a href="https://docs.anthropic.com/claude/reference/messages_post">Messages</a>
 * <a href="https://docs.anthropic.com/en/docs/models-overview">Models</a>
 * <a href="https://docs.anthropic.com/en/docs/embeddings">Embeddings</a>
 */
public class Anthropic extends Assistant {

    public static final String ENPOINT_COMPLETION = "https://api.anthropic.com/v1/messages";
    public static final String MODEL_CLAUDE3_OPUS = "claude-3-opus-20240229";
    public static final String ANTHROPIC_VERSION = "2023-06-01";

    public Anthropic(String apiKey, String model) {
        setProvider("anthropic");
        setModel(model);
        setApikey(apiKey);
        setDescription("Anthropic Claude: " + model);
    }

    @Override
    public String getEndpoint(Conversation conversation) {
        return ENPOINT_COMPLETION;
    }

    @Override
    public String getJsonRequest(Conversation conversation) {
        LinkedList<Map<String, Object>> messages = new LinkedList<>();
        Assistant assistant = conversation.getQuery().getAssistant();
        String system = null; // Il system prompt è a sé stante in Claude.
        for (Message message : conversation) {
            if (message.getRole() == Message.Role.error) continue;
            if (message.getRole() == Message.Role.system) {
                system = message.getText();
                continue;
            }

            if (message.hasImage()) {
                List<Map<String, Object>> contentImage = new ArrayList<>();

                // Creazione del primo oggetto
                Map<String, Object> imageObject = new HashMap<>();
                Map<String, Object> sourceData = new HashMap<>();
                String[] imageParts = toImageTypeContent(message.getParts().get(Message.Type.image));
                sourceData.put("media_type", imageParts[0]);
                sourceData.put("data", imageParts[1]);
                sourceData.put("type", "base64");
                imageObject.put("source", sourceData);
                imageObject.put("type", "image");
                contentImage.add(imageObject);

                // Creazione del secondo oggetto
                Map<String, Object> textObject = new HashMap<>();
                textObject.put("text", message.getText());
                textObject.put("type", "text");
                contentImage.add(textObject);

                messages.add(Map.of("role", message.getRole(), "content", contentImage));
            } else {
                messages.add(Map.of("role", message.getRole(), "content", message.getText()));
            }
        }

        Map<String, Object> requestObject = new HashMap<>(Map.of(
                "model", getModel(),
                "messages", messages,
                "max_tokens", 1024
        ));
        if (system != null) requestObject.put("system", system);
        return Jsonable.toJson(requestObject, true);
    }

    @Override
    public Message getAnswer(SuperHttpClient.SuperResponse superResponse) {
        JsonNode rootNode = Jsonable.toJsonNode(superResponse.getBody());
        String answer = rootNode.get("content").get(0).get("text").asText();
        return new Message(this, Message.Role.assistant, Map.of(Message.Type.text, answer));
    }

    @Override
    public Map<String, String> getHeaders() {
        return Map.of(
                "x-api-key", getApikey(),
                "anthropic-version", ANTHROPIC_VERSION);
    }

}
