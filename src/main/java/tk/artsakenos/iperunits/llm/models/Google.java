package tk.artsakenos.iperunits.llm.models;

import com.fasterxml.jackson.databind.JsonNode;
import tk.artsakenos.iperunits.llm.Assistant;
import tk.artsakenos.iperunits.llm.Conversation;
import tk.artsakenos.iperunits.llm.Message;
import tk.artsakenos.iperunits.serial.Jsonable;
import tk.artsakenos.iperunits.web.SuperHttpClient;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://ai.google.dev/docs/gemini_api_overview">...</a>
 * <a href="https://ai.google.dev/tutorials/rest_quickstart">...</a>
 * Il multipart del context, a Google lo chiamano multi-turn
 */
public class Google extends Assistant {

    public final String URL_VISION = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro-vision:generateContent";
    public static final String URL_GENERATE = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    public static final String MODEL_GEMINI_20_FLASH = "gemini-2.0-flash";
    // public static final String MODEL_GEMINIPRO = "gemini-pro";
    // public static final String MODEL_GEMINIPRO_VISION = "gemini-pro-vision";

    public Google(String apiKey, String model) {
        setProvider("Google");
        setModel(model);
        setApikey(apiKey);
        setDescription("The Google LLM: " + model);
    }

    /**
     * Data un immagine in Base64 restituisce un array di 2 stringhe,
     * tipo ("image/png") e contenuto ("iVBORw0KG...==")
     *
     * @param imageBase64 The image in Base64 (data:image/jpeg;base64,/9j/4AAQSkZJ...==)
     * @return Un array con due elementi, tipo e contenuto
     */
    public static String[] toImageTypeContent(String imageBase64) {
        String[] output = new String[]{"", ""};
        int pivot = imageBase64.indexOf(",") + 1;
        if (pivot < 1) return output;
        int typeBegin = imageBase64.indexOf(":") + 1;
        int typeEnd = imageBase64.indexOf(";");
        if (typeBegin < 1 || typeEnd < 1) return output;
        output[0] = imageBase64.substring(typeBegin, typeEnd);
        output[1] = imageBase64.substring(pivot);
        return output;
    }

    @Override
    public String getEndpoint(Conversation conversation) {
        Message query = conversation.getLast(Message.Role.user);
        if (query != null && query.hasImage()) {
            return URL_VISION + "?key=" + getApikey();
        }
        return URL_GENERATE.formatted(getModel(), getApikey());
    }

    /**
     * Attenzione a un paio di cose, per le quali Google da ERRORE e non warnings:
     * 1) La history deve alternare user e model.
     * 2) Nelle immagini non ci può essere history.
     *
     * @param conversation The Conversation
     * @return The Envelope with the Answer
     */
    @Override
    public String getJsonRequest(Conversation conversation) {
        // Per ora gestisco solo il text, vedi Postman per il multipart!
        // Take into account that Conversation extends LinkedList<Message>
        LinkedList<Object> contents = new LinkedList<>();

        for (Message message : conversation) {
            // Il contenuto contiene solo messaggi di utente e model, non errori o prompt di sistema
            if (message.getRole() != Message.Role.assistant && message.getRole() != Message.Role.user)
                continue;

            String role = message.getRole() == Message.Role.assistant ? "model" : "user";

            // Create parts list with the message text
            Map<String, Object> textPart = Map.of("text", message.getText());
            List<Object> parts = List.of(textPart);

            // Create a content section for this message
            Map<String, Object> content_section = Map.of(
                    "role", role,
                    "parts", parts
            );

            // Add to contents list
            contents.add(content_section);
        }

        // Default generation config values
        Map<String, Object> generationConfig = Map.of(
                "maxOutputTokens", 8192,
                "responseMimeType", "text/plain",
                "temperature", 1,
                "topK", 40,
                "topP", 0.95
        );

        // Check if there's a system prompt message
        Message systemPrompt = conversation.getLast(Message.Role.system);

        // Creare una mappa mutabile
        HashMap<String, Object> requestWrap = new HashMap<>();
        requestWrap.put("contents", contents);
        requestWrap.put("generationConfig", generationConfig);

        // Add system instruction if present
        if (systemPrompt != null) {
            Map<String, Object> systemPart = Map.of("text", systemPrompt.getText());
            Map<String, Object> systemInstructionWrap = Map.of("parts", List.of(systemPart));
            requestWrap.put("systemInstruction", systemInstructionWrap);
        }

        return Jsonable.toJson(requestWrap, true);
    }


    public Map<String, String> getHeaders() {
        return null;
    }


    public Message getAnswer(SuperHttpClient.SuperResponse superResponse) {
        JsonNode rootNode = Jsonable.toJsonNode(superResponse.getBody());
        String model = rootNode.get("modelVersion").asText();
        // long created = rootNode.get("created").asLong();
        String answer = rootNode.at("/candidates/0/content/parts/0/text").asText();
        return new Message(this, Message.Role.assistant, Map.of(Message.Type.text, answer));
    }

}
