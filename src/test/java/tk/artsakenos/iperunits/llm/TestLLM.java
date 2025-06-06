package tk.artsakenos.iperunits.llm;

import lombok.extern.java.Log;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
@Disabled
@Log
public class TestLLM {

    private static Assistant assistant;
    private final static Conversation conversation = new Conversation();
    private final static TestModelPool modelPool = new TestModelPool();


    @BeforeAll
    public static void setup() {
        Assistant groq = modelPool.get("groq_llama3_8b");
        Assistant google = modelPool.get("google_gemini_20_flash");

        assistant = groq;
        conversation.add(google, Message.Role.system, "Sei Vincenzo, un assistente medico con una preparazione incredibile, sempre sicuro di te, sei madrelingua spagnolo e non conosci altre lingue, rispondi sempre in spagnolo.");
        conversation.add(google, Message.Role.assistant, "Hola que tal hoy?");
        conversation.add(google, Message.Role.user, "Sto molto bene grazie! Ho un po' di maldipancia");
        conversation.add(google, Message.Role.assistant, "Has probado a comer tu medicina?");
        conversation.add(google, Message.Role.user, "Si ma ha auto alcuni effetti collaterali, vorrei provare qualcos' altro!.");
        conversation.add(google, Message.Role.error, "QUI MOCK ERRORE GENERICO DI SISTEMA!");
        conversation.add(google, Message.Role.assistant, "Vale, pregunta lo que quieres!?");
    }

    @Test
    void testGroq() {
        assistant = modelPool.get("groq_llama3_8b");
    }

    @Test
    void testCerebras() {
        assistant = modelPool.get("cerebras_llama31_8b");
    }

    @Test
    void testGoogle() {
        assistant = modelPool.get("google_gemini_20_flash");
    }

    @Test
    void testAnthropic() {
        assistant = modelPool.get("claude");
    }

    @Test
    void testCohere() {
        assistant = modelPool.get("cohere");
    }

    @Test
    void testDeepseek() {
        assistant = modelPool.get("deepseek");
    }

    @Test
    void testOpenai() {
        assistant = modelPool.get("openai");
    }

    @AfterAll
    public static void results() {
        conversation.add(assistant, Message.Role.user, "Ora ho maldipancia, ma solo la sera, hai qualche consiglio?");
        log.info("Answer: " + LlmService.query(conversation, assistant));
        log.info("Conversation: " + conversation);

    }

}
