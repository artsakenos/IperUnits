package tk.artsakenos.iperunits.llm.models;

import tk.artsakenos.iperunits.llm.Assistant;
import tk.artsakenos.iperunits.llm.Conversation;
import tk.artsakenos.iperunits.llm.Message;
import tk.artsakenos.iperunits.serial.Jsonable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://inference-docs.cerebras.ai/">Cerebras</a>
 */
@SuppressWarnings("unused")
public class Cerebras extends Assistant {

    public static final String MODEL_LLAMA_8B = "llama3.1-8b"; // Context Length: 8192
    public static final String MODEL_LLAMA_70B = "llama3.1-70b"; // Context Length: 8192

    public Cerebras(String apiKey, String model) {
        setProvider("cerebras");
        setModel(model);
        setApikey(apiKey);
        setDescription("Cerebras, fast inference on a TSP Architecture: " + model);
    }

    @Override
    public String getEndpoint(Conversation conversation) {
        return "https://api.cerebras.ai/v1/chat/completions";
    }

    @Override
    public Map<String, String> getHeaders() {
        return Map.of("Authorization", "Bearer " + getApikey());
    }

    @Override
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
                "temperature", 0.5,
                "max_completion_tokens", -1,
                "seed", 0,
                "top_p", 1,
                "presence_penalty", 0

        );

        return Jsonable.toJson(requestObject, true);
    }

}
