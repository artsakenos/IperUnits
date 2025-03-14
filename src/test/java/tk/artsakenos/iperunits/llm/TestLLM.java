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
    private final static HelperAvailableModels availableModels = new HelperAvailableModels();
    private static Assistant assistant;


    @BeforeAll
    public static void setup() {
        Assistant groq = availableModels.get("groq_llama3_8b");
        Assistant google = availableModels.get("google_gemini_20_flash");

        assistant = groq;
        llmService.addMessage(google, Message.Role.system, "Sei Vincenzo, un assistente medico con una preparazione incredibile, sempre sicuro di te, sei madrelingua spagnolo e non conosci altre lingue, rispondi sempre in spagnolo.");
        llmService.addMessage(google, Message.Role.assistant, "Hola que tal hoy?");
        llmService.addMessage(google, Message.Role.user, "Sto molto bene grazie! Ho un po' di maldipancia");
        llmService.addMessage(google, Message.Role.assistant, "Has probado a comer tu medicina?");
        llmService.addMessage(google, Message.Role.user, "Si ma ha auto alcuni effetti collaterali, vorrei provare qualcos' altro!.");
        llmService.addMessage(google, Message.Role.error, "QUI MOCK ERRORE GENERICO DI SISTEMA!");
        llmService.addMessage(google, Message.Role.assistant, "Vale, pregunta lo que quieres!?");
    }

    @Test
    void testGroq() {
        assistant = availableModels.get("groq_llama3_8b");
    }

    @Test
    void testCerebras() {
        assistant = availableModels.get("cerebras_llama31_8b");
    }

    @Test
    void testGoogle() {
        assistant = availableModels.get("google_gemini_20_flash");
    }

    @Test
    void testAnthropic() {
        assistant = availableModels.get("claude");
    }

    @Test
    void testCohere() {
        assistant = availableModels.get("cohere");
    }

    @Test
    void testDeepseek() {
        assistant = availableModels.get("deepseek");
    }

    @AfterAll
    public static void results() {
        llmService.addMessage(assistant, Message.Role.user, "Ora ho maldipancia, ma solo la sera, hai qualche consiglio?");
        log.info("Answer: " + llmService.query());
        log.info("Conversation: " + llmService.getConversation());

    }

    @Test
    void testLlmRouter() {
        LlmRouter llmRouter = new LlmRouter(List.of(
                availableModels.get("Groq LLama3 8B"),
                availableModels.get("Cerebras LLama3 8B")
        ));
        String question = "Ciao sono il tuo nuovo amico francese, per favore parlami in francese";
        String answer = llmRouter.query("user01", question);
        System.out.println(question + "\n    > " + answer);
        question = "mi dici come si fa la pizza?";
        answer = llmRouter.query("user01", question);
        System.out.println(question + "\n    > " + answer);
    }

}
