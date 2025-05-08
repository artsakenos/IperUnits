package tk.artsakenos.iperunits.llm.models;

import tk.artsakenos.iperunits.llm.Assistant;
import tk.artsakenos.iperunits.llm.Conversation;

public class OpenAI extends Assistant {

    public static final String MODEL_41_MINI = "gpt-4.1-mini-2025-04-14"; // $0.40 $1.60 // 5k x min    // 32768
    public static final String MODEL_4O_SEARCH = "gpt-4o-search-preview-2025-03-11"; // $2.50 $10.00 // 6k x min    // 8192
    public static final String ENDPOINT_COMPLETION = "https://api.openai.com/v1/chat/completions";

    public OpenAI(String apiKey, String model) {
        setProvider("OpenAI");
        setModel(model);
        setApikey(apiKey);
        setDescription("OpenAI ChatGPT: " + model);
    }

    @Override
    public String getEndpoint(Conversation conversation) {
        return ENDPOINT_COMPLETION;
    }


}
