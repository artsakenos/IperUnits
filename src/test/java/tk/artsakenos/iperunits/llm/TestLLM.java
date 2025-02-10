package tk.artsakenos.iperunits.llm;

import lombok.extern.java.Log;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tk.artsakenos.iperunits.initors.SuperProperties;
import tk.artsakenos.iperunits.llm.models.*;

@Disabled
@Log
public class TestLLM {

    private final static LlmService llmService = new LlmService();
    private static final AvailableModels availableModels = new AvailableModels();
    private static Assistant assistant;


    @BeforeAll
    public static void setup() {
        // Make some Available Models
        Assistant groq = new Groq(
                SuperProperties.get("infodev.properties", "APIKEY_GROQ", null),
                Groq.MODEL_LLAMA3_8B);
        Assistant cerebras = new Cerebras(
                SuperProperties.get("infodev.properties", "APIKEY_CEREBRAS", null),
                Cerebras.MODEL_LLAMA_8B);
        Assistant google = new Google(
                SuperProperties.get("infodev.properties", "APIKEY_GOOGLE", null),
                Google.MODEL_GEMINIPRO);
        Assistant anthropic = new Anthropic(
                SuperProperties.get("infodev.properties", "APIKEY_ANTHROPIC", null),
                Anthropic.MODEL_CLAUDE3_OPUS);
        Assistant cohere = new Cohere(
                SuperProperties.get("infodev.properties", "APIKEY_COHERE", null),
                Cohere.MODEL_AYA_8);
        Assistant deepseek = new Deepseek(
                SuperProperties.get("infodev.properties", "APIKEY_DEEPSEEK", null),
                Deepseek.MODEL_CHAT);

        availableModels.put("Groq LLama3 8B", groq);
        availableModels.put("Cerebras LLama3 8B", cerebras);
        availableModels.put("Google Gemini", google);
        availableModels.put("Anthropic Claude", anthropic);
        availableModels.put("Cohere", cohere);
        availableModels.put("Deepseek", deepseek);

        assistant = groq;
        llmService.addMessage(assistant, Message.Role.system, "Sei un assistente fenomenale, e rispondi sempre in italiano perfetto.");
        llmService.addMessage(assistant, Message.Role.assistant, "Eccomi come stai oggi?");
        llmService.addMessage(assistant, Message.Role.user, "Sto molto bene grazie! Ho un po' di maldipancia");
        llmService.addMessage(assistant, Message.Role.assistant, "Hai provato ad andare da un medico?");
        llmService.addMessage(assistant, Message.Role.user, "Si sono dal medico, ora quando ti chiedo qualcosa rispondi a lui in spagnolo!.");
        llmService.addMessage(assistant, Message.Role.assistant, "Va bene chiedimi pure!?");
    }

    @Test
    void testGroq() {
        assistant = availableModels.get("Groq LLama3 8B");
        llmService.addMessage(assistant, Message.Role.user, "Sto visitando il paziente, hai qualche consiglio?");
    }

    @Test
    void testCerebras() {
        assistant = availableModels.get("Cerebras LLama3 8B");
        llmService.addMessage(assistant, Message.Role.user, "Sto visitando il paziente, hai qualche consiglio?");
    }

    @Test
    void testGoogle() {
        assistant = availableModels.get("Google Gemini");
        llmService.addMessage(assistant, Message.Role.user, "Sto visitando il paziente, hai qualche consiglio?");
    }

    @Test
    void testAnthropic() {
        assistant = availableModels.get("Anthropic Claude");
        llmService.addMessage(assistant, Message.Role.user, "Sto visitando il paziente, hai qualche consiglio?");
    }

    @Test
    void testCohere() {
        assistant = availableModels.get("Cohere");
        llmService.addMessage(assistant, Message.Role.user, "Sto visitando il paziente, hai qualche consiglio?");
    }

    @Test
    void testDeepseek() {
        assistant = availableModels.get("Deepseek");
        llmService.addMessage(assistant, Message.Role.user, "Sto visitando il paziente, hai qualche consiglio?");
    }

    @AfterAll
    public static void results() {
        log.info("Answer: " + llmService.query());
        log.info("Conversation: " + llmService.getConversation());

    }

}
