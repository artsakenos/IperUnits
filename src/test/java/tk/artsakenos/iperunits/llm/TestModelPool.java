package tk.artsakenos.iperunits.llm;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tk.artsakenos.iperunits.initors.SuperProperties;
import tk.artsakenos.iperunits.llm.models.*;

@Disabled
@Log
public class TestModelPool extends ModelsPool {

    public TestModelPool() {
        put("groq_llama3_8b", new Groq(
                SuperProperties.get("infodev.properties", "APIKEY_GROQ", null),
                Groq.MODEL_LLAMA3_8B));

        put("groq_infora_llama3_70b", new Groq(
                SuperProperties.get("infodev.properties", "APIKEY_GROQ_INFORA", null),
                Groq.MODEL_LLAMA3_70B));

        put("cerebras_llama31_8b", new Cerebras(
                SuperProperties.get("infodev.properties", "APIKEY_CEREBRAS", null),
                Cerebras.MODEL_LLAMA_31_8B));

        put("google_gemini_20_flash", new Google(
                SuperProperties.get("infodev.properties", "APIKEY_GOOGLE", null),
                Google.MODEL_GEMINI_20_FLASH));

        put("claude", new Anthropic(
                SuperProperties.get("infodev.properties", "APIKEY_ANTHROPIC", null),
                Anthropic.MODEL_CLAUDE3_OPUS));

        put("cohere", new Cohere(
                SuperProperties.get("infodev.properties", "APIKEY_COHERE", null),
                Cohere.MODEL_AYA_8));

        put("deepseek", new Deepseek(
                SuperProperties.get("infodev.properties", "APIKEY_DEEPSEEK", null),
                Deepseek.MODEL_CHAT));

        put("openai", new OpenAI(
                SuperProperties.get("infodev.properties", "APIKEY_OPENAI", null),
                OpenAI.MODEL_41_MINI));
    }

    @Test
    public void testRouting() {
        ModelsPool modelsPool = new ModelsPool();
        modelsPool.put("deepseek", new Deepseek(
                SuperProperties.get("infodev.properties", "APIKEY_DEEPSEEK", null),
                Deepseek.MODEL_CHAT));
        modelsPool.put("groq_llama3_8b", new Groq(
                SuperProperties.get("infodev.properties", "APIKEY_GROQ", null),
                Groq.MODEL_LLAMA3_8B));

        System.out.println("Models available: " + modelsPool.descriptions());

        Conversation conversation = new Conversation();
        conversation.add(modelsPool.get("groq_llama3_8b"), Message.Role.user, "Hello, how are you?");
        Message answer = modelsPool.query(conversation);
        log.info(answer.toString());
    }

}
