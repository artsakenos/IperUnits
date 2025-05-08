package tk.artsakenos.iperunits.llm;

import lombok.extern.java.Log;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
@Disabled
@Log
public class TestLLM {

    private final static LlmService llmService = new LlmService();
    private static Assistant assistant;
    private final static Conversation conversation = new Conversation();


    @BeforeAll
    public static void setup() {
        Assistant groq = HelperModelsPool.getAssistant("groq_llama3_8b");
        Assistant google = HelperModelsPool.getAssistant("google_gemini_20_flash");

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
        assistant = HelperModelsPool.getAssistant("groq_llama3_8b");
    }

    @Test
    void testCerebras() {
        assistant = HelperModelsPool.getAssistant("cerebras_llama31_8b");
    }

    @Test
    void testGoogle() {
        assistant = HelperModelsPool.getAssistant("google_gemini_20_flash");
    }

    @Test
    void testAnthropic() {
        assistant = HelperModelsPool.getAssistant("claude");
    }

    @Test
    void testCohere() {
        assistant = HelperModelsPool.getAssistant("cohere");
    }

    @Test
    void testDeepseek() {
        assistant = HelperModelsPool.getAssistant("deepseek");
    }

    @AfterAll
    public static void results() {
        conversation.add(assistant, Message.Role.user, "Ora ho maldipancia, ma solo la sera, hai qualche consiglio?");
        log.info("Answer: " + llmService.query(conversation, assistant));
        log.info("Conversation: " + conversation);

    }

    @Test
    void testLlmRouter() {
        LlmRouter llmRouter = new LlmRouter(List.of(
                HelperModelsPool.getAssistant("Groq LLama3 8B"),
                HelperModelsPool.getAssistant("Cerebras LLama3 8B")
        ));
        String question = "Ciao sono il tuo nuovo amico francese, per favore parlami in francese";
        String answer = llmRouter.query("user01", question);
        System.out.println(question + "\n    > " + answer);
        question = "mi dici come si fa la pizza?";
        answer = llmRouter.query("user01", question);
        System.out.println(question + "\n    > " + answer);
    }

}
