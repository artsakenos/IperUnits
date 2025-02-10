package tk.artsakenos.iperunits.llm.models;

import tk.artsakenos.iperunits.llm.Assistant;
import tk.artsakenos.iperunits.llm.Conversation;

/**
 * - <a href="https://console.groq.com/docs/quickstart">Groq API</a>
 * - <a href="https://console.groq.com/settings/limits">Models and Rate Limits</a>
 */
@SuppressWarnings("unused")
public class Groq extends Assistant {

    public static final String MODEL_MIXTRAL = "mixtral-8x7b-32768"; // 5k x min    // 32768
    public static final String MODEL_LLAMA3_70B = "llama3-70b-8192"; // 6k x min    // 8192
    public static final String MODEL_LLAMA3_8B = "llama3-8b-8192"; // 30k x min     // 8192
    public static final String MODEL_GEMMA7B = "gemma-7b-it"; // 15k x min          // 8192
    public static final String ENDPOINT_TEXT = "https://api.groq.com/openai/v1/chat/completions";

    public Groq(String apiKey, String model) {
        setProvider("groq");
        setModel(model);
        setApikey(apiKey);
        setDescription("Groq, fast inference on a TSP Architecture: " + model);
    }

    @Override
    public String getEndpoint(Conversation conversation) {
        return ENDPOINT_TEXT;
    }

}
