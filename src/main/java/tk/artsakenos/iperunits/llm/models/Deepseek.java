package tk.artsakenos.iperunits.llm.models;

import tk.artsakenos.iperunits.llm.Assistant;
import tk.artsakenos.iperunits.llm.Conversation;

/**
 * - <a href="https://platform.deepseek.com/">DeepSeek Platform</a>
 * - <a href="https://api-docs.deepseek.com/">API Docs</a>
 */
@SuppressWarnings("unused")
public class Deepseek extends Assistant {

    public static final String DEEPSEEK_CHAT_COMPLETION = "https://api.deepseek.com/chat/completions";

    public static final String MODEL_CHAT = "deepseek-chat"; // 64K iput - no time limit
    public static final String MODEL_REASONER = "deepseek-reasoner"; // 64k input - no time limit

    public Deepseek(String apiKey, String model) {
        setProvider("deepseek");
        setModel(model);
        setApikey(apiKey);
        setDescription("DeepSeek-V3 achieves a significant breakthrough in inference speed over previous models");
    }

    @Override
    public String getEndpoint(Conversation conversation) {
        return DEEPSEEK_CHAT_COMPLETION;
    }

}
