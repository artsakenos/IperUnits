package tk.artsakenos.iperunits.llm.models;

import tk.artsakenos.iperunits.llm.Assistant;
import tk.artsakenos.iperunits.llm.Conversation;

/**
 * <a href="https://ollama.com/search">Ollama Models</a>
 * <a href="https://github.com/ollama/ollama/blob/main/docs/api.md">Api Documentation</a>
 */
@SuppressWarnings("unused")
public class Ollama extends Assistant {

    private final String URL_ENDPOINT;
    public static final String ENDPOINT_GENERATE = "/api/generate";
    public static final String ENDPOINT_CHAT = "/api/chat";

    public static final String MODEL_TEXT = "llama3.2:latest";
    public static final String MODEL_VISION = "llava:7b";


    public Ollama(String ollamaUrl, String model) {
        this.URL_ENDPOINT = ollamaUrl;
        setProvider("Ollama");
        setModel(model);
        setApikey(null);
        setDescription("Ollama: " + model);
    }

    @Override
    public String getEndpoint(Conversation conversation) {
        return URL_ENDPOINT;
    }
}

/**
 * package tk.artsakenos.ultraservices.model.ai_models;
 * <p>
 * import com.fasterxml.jackson.databind.JsonNode;
 * import org.springframework.core.env.Environment;
 * import tk.artsakenos.ultraservices.libraries.HelperSerialize;
 * import tk.artsakenos.ultraservices.model.AIEnvelope;
 * import tk.artsakenos.ultraservices.model.AIMessage;
 * import tk.artsakenos.ultraservices.model.AIMessage.Role;
 * <p>
 * import java.util.*;
 * <p>
 * import static tk.artsakenos.ultraservices.libraries.HelperSerialize.toJsonNode;
 * <p>
 * public class Model_OLLAMA_Multimodal implements ModelInterface {
 * <p>
 * public static final String URL_TEXT = "http://localhost:11434";
 * public static final String URL_VISION = "http://localhost:11435";
 * public static final String MODEL_TEXT = "mistral";
 * public static final String MODEL_VISION = "llava";
 *
 * @Override public String getUrl(AIEnvelope aiEnvelope, Environment environment) {
 * String url = aiEnvelope.getQuery().hasImage() ? URL_VISION : URL_TEXT;
 * <p>
 * if (environment != null) {
 * url = environment.getProperty("ultraservices.url_ollama_text");
 * if (aiEnvelope.getQuery().hasImage()) {
 * url = environment.getProperty("ultraservices.url_ollama_vision");
 * }
 * }
 * <p>
 * if (aiEnvelope.getQuery().hasImage())
 * return url + "/api/generate";
 * else
 * return url + "/api/chat";
 * }
 * @Override public String buildJsonRequest(AIEnvelope aiEnvelope) {
 * // see https://github.com/ollama/ollama/blob/main/docs/api.md
 * // Per ora la faccio semplice.
 * String system = aiEnvelope.getQuery().getAssistant().getSystem();
 * String model = aiEnvelope.getQuery().hasImage() ? MODEL_VISION : MODEL_TEXT;
 * for (AIMessage AIMessage : aiEnvelope) {
 * AIMessage.Role role = AIMessage.getRole();
 * if (role == Role.system && AIMessage.getAssistant() == aiEnvelope.getQuery().getAssistant())
 * system = AIMessage.getText();
 * }
 * <p>
 * HashMap<String, Object> requestObject = new HashMap<>(Map.of(
 * "model", model,
 * "system", system,
 * "stream", false
 * ));
 * if (aiEnvelope.getQuery().hasImage()) {
 * ArrayList<String> images = new ArrayList<>();
 * String imageBase64 = aiEnvelope.getQuery().getParts().get(AIMessage.Type.image);
 * images.add(HelperSerialize.toImageTypeContent(imageBase64)[1]);
 * requestObject.put("images", images);
 * requestObject.put("prompt", aiEnvelope.getQuery().getText());
 * } else {
 * List<Map<String, Object>> messages = new LinkedList<>();
 * for (AIMessage AIMessage : aiEnvelope) {
 * AIMessage.Role role = AIMessage.getRole();
 * if (role == Role.error || role == Role.context) continue;
 * messages.add(Map.of("role", role, "content", AIMessage.getText()));
 * }
 * requestObject.put("messages", messages);
 * }
 * <p>
 * return HelperSerialize.toJsonString(requestObject);
 * }
 * @Override public void setAnswer(String jsonResponse, AIEnvelope aiEnvelope) {
 * JsonNode rootNode = toJsonNode(jsonResponse);
 * String answer = null;
 * if (rootNode.get("response") != null) {
 * // Query /api/request
 * answer = rootNode.get("response").asText();
 * } else {
 * // Query /api/chat
 * answer = rootNode.get("message").get("content").asText();
 * }
 * aiEnvelope.addAnswer(aiEnvelope.getQuery().getAssistant(), answer);
 * }
 * @Override public Map<String, String> getPostParameters(Environment environment) {
 * return null;
 * }
 * }
 */