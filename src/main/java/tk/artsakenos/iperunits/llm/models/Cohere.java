package tk.artsakenos.iperunits.llm.models;

import com.fasterxml.jackson.databind.JsonNode;
import tk.artsakenos.iperunits.llm.Assistant;
import tk.artsakenos.iperunits.llm.Conversation;
import tk.artsakenos.iperunits.llm.Message;
import tk.artsakenos.iperunits.serial.Jsonable;
import tk.artsakenos.iperunits.web.SuperHttpClient;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://docs.cohere.com/reference/about">Cohere</a>
 * <a href="https://docs.cohere.com/v2/docs/models">Cohere Models</a>
 * <a href="https://dashboard.cohere.com/playground/generate">Cohere Playground</a>
 */
@SuppressWarnings("unused")
public class Cohere extends Assistant {

    public final String URL_GENERATE = "https://api.cohere.com/v1/generate"; // UN Legacy.
    public final String URL_CHAT_V1 = "https://api.cohere.com/v1/chat";
    public final String URL_CHAT_V2 = "https://api.cohere.com/v2/chat";

    public static final String MODEL_COMMAND_LIGHT = "command-light"; // CLenght 4K, Output 4K
    public static final String MODEL_COMMAND_R_PLUS = "command-r-plus"; // CLenght 128K, Output 4K
    public static final String MODEL_AYA_32 = "c4ai-aya-expanse-32b"; // CLenght 128K, Output 4K
    public static final String MODEL_AYA_8 = "c4ai-aya-expanse-8b"; // CLenght 8K, Output 4K

    public boolean websearch = true; // Da implementare, il v2 non lo prevede.

    public Cohere(String apiKey, String model) {
        setProvider("cohere");
        setModel(model);
        setApikey(apiKey);
        setDescription("Cohere: " + model);
    }


    @Override
    public String getEndpoint(Conversation conversation) {
        if (websearch) return URL_CHAT_V1;
        return URL_CHAT_V2;
    }

    @Override
    public String getJsonRequest(Conversation conversation) {
        if (websearch) {
            List<Map<String, Object>> chatHistory = new LinkedList<>();
            for (Message message : conversation) {
                Message.Role role = message.getRole();
                if (role == Message.Role.error || role == Message.Role.context) continue;
                // Role must be one of the following: User, Chatbot, System, Tool"
                String cohereRole = "User";
                if (role == Message.Role.system) cohereRole = "System";
                if (role == Message.Role.assistant) cohereRole = "Chatbot";
                if (message == conversation.getQuery()) continue; // Questo lo si mette in un altro slot.
                chatHistory.add(Map.of("role", cohereRole, "message", message.getText()));
                // lastQueriesTokens += aiMessage.getText().length() / 4;
            }
            Map<String, Object> requestObject = Map.of(
                    "model", "command-r", // getModel(),
                    // Altri non sono supportati per il RAG!
                    "chat_history", chatHistory,
                    "message", conversation.getQuery().getText(),
                    "temperature", 0.3,
                    "prompt_truncation", "AUTO",
                    "stream", false,
                    "connectors", List.of(
                            Map.of("id", "web-search"))
            );

            return Jsonable.toJson(requestObject, true);
        }

        return super.getJsonRequest(conversation);
    }

    public Message getAnswer(SuperHttpClient.SuperResponse superResponse) {
        String body = superResponse.getBody();
        JsonNode rootNode = Jsonable.toJsonNode(body);
        String id = rootNode.get("id").asText();
        String answer = "";
        if (websearch) {
            answer = rootNode.get("text").asText();
        } else answer = rootNode.at("/message/content/0/text").asText();
        return new Message(this, Message.Role.assistant, Map.of(Message.Type.text, answer));
    }

}
