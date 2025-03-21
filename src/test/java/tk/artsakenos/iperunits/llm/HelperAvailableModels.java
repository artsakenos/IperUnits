package tk.artsakenos.iperunits.llm;

import tk.artsakenos.iperunits.initors.SuperProperties;
import tk.artsakenos.iperunits.llm.models.*;

public class HelperAvailableModels extends AvailableModels {

    private static final HelperAvailableModels instance;

    static {
        instance = new HelperAvailableModels();

        Assistant groq = new Groq(
                SuperProperties.get("infodev.properties", "APIKEY_GROQ", null),
                Groq.MODEL_LLAMA3_8B);
        instance.put("groq_llama3_8b", groq);

        Assistant groq_infora = new Groq(
                SuperProperties.get("infodev.properties", "APIKEY_GROQ_INFORA", null),
                Groq.MODEL_LLAMA3_70B);
        instance.put("groq_infora_llama3_70b", groq);

        Assistant cerebras = new Cerebras(
                SuperProperties.get("infodev.properties", "APIKEY_CEREBRAS", null),
                Cerebras.MODEL_LLAMA_31_8B);
        instance.put("cerebras_llama31_8b", cerebras);

        Assistant google = new Google(
                SuperProperties.get("infodev.properties", "APIKEY_GOOGLE", null),
                Google.MODEL_GEMINI_20_FLASH);
        instance.put("google_gemini_20_flash", google);

        Assistant anthropic = new Anthropic(
                SuperProperties.get("infodev.properties", "APIKEY_ANTHROPIC", null),
                Anthropic.MODEL_CLAUDE3_OPUS);
        instance.put("claude", anthropic);

        Assistant cohere = new Cohere(
                SuperProperties.get("infodev.properties", "APIKEY_COHERE", null),
                Cohere.MODEL_AYA_8);
        instance.put("cohere", cohere);

        Assistant deepseek = new Deepseek(
                SuperProperties.get("infodev.properties", "APIKEY_DEEPSEEK", null),
                Deepseek.MODEL_CHAT);
        instance.put("deepseek", deepseek);

    }

    public static Assistant getAssistant(String name) {
        return instance.get(name);
    }

}
