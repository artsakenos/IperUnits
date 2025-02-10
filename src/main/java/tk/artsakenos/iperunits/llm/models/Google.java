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
 * <a href="https://ai.google.dev/docs/gemini_api_overview">...</a>
 * <a href="https://ai.google.dev/tutorials/rest_quickstart">...</a>
 * Il multipart del context, a google lo chiamano multiturn
 */
public class Google extends Assistant {

    public final String URL_GENERATE = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    public final String URL_VISION = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro-vision:generateContent";

    public static final String MODEL_GEMINIPRO = "gemini-pro";
    public static final String MODEL_GEMINIPRO_VISION = "gemini-pro-vision";

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
        return URL_GENERATE + "?key=" + getApikey();
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
        // Per ora la faccio semplice, ma vedi Postman per il multipart!
        // String question = aiEnvelope.getQuery().getContent();
        LinkedList<Object> contents = new LinkedList<>();
        Map<String, Object> content_section = null;
        for (Message message : conversation) {
            String role = message.getRole().name();
            if (role.equals("system")) role = "user";
            if (role.equals("assistant")) role = "model";
            if (content_section != null
                    && content_section.get("role") != null
                    && content_section.get("role").equals(role)) {
                contents.removeLast();
            }

            List<Map<String, Object>> parts = new java.util.ArrayList<>(List.of(Map.of("text", message.getText())));
            Message query = conversation.getLast(Message.Role.user);

            if (query.hasImage()) {
                String image_b64 = query.getParts().get(Message.Type.image);
                String[] image = toImageTypeContent(image_b64);
                Map<String, Object> inline_image = Map.of(
                        "mime_type", image[0],
                        "data", image[1]
                );
                parts.add(Map.of("inline_data", inline_image));
            }

            content_section = Map.of(
                    "role", role,
                    "parts", parts
            );
            contents.add(content_section);
        }

        Map<String, Object> story = Map.of("contents", contents);
        return Jsonable.toJson(story, true);
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
