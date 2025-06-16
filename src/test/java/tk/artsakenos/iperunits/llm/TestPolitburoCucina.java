package tk.artsakenos.iperunits.llm;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tk.artsakenos.iperunits.llm.politburo.AiAgent;
import tk.artsakenos.iperunits.llm.politburo.Politburo;

import java.util.List;
import java.util.Scanner;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
@Log
@Disabled
public class TestPolitburoCucina {

    private Politburo politburo;
    private final static TestModelPool modelPool = new TestModelPool();


    @Test
    public void initPolitburo() {
        Assistant groq_infora = modelPool.get("groq_infora_llama3_70b");
        Assistant cerebras = modelPool.get("cerebras_llama31_8b");
        Assistant gemini = modelPool.get("google_gemini_20_flash");

        AiAgent aiChairman = new AiAgent(cerebras, "classificatore",
                """
                        - Agente di sistema per la gestione dei turni di parola
                        - Analizza il contesto della conversazione per determinare chi deve parlare
                        - Non partecipa attivamente alla conversazione
                        - Filtra tutte le comunicazioni non rilevanti e non pertinenti con l' obbiettivo degli agenti
                        """,
                """
                        Tu sei il coordinatore degli agenti.
                        Rispondi esclusivamente con il nome del prossimo attore che deve parlare o con FILTER se il meggaggio non e' rilevante.
                        Vincoli da rispettare:
                          - Rispondere SOLO con il nome dell'agente che deve parlare
                          - Seguire l'ordine procedurale che tiene conto degli interventi precedenti e di eventuali ruoli di coordinazione
                        """,
                AiAgent.Role.COORDINATOR);
        AiAgent aiScarlet = new AiAgent(gemini, "scarlet",
                "Una esperta di cucina, riesce a creare i piatti piu' prelibati partendo dagli ingredienti a disposizione",
                "Preparare il piatto migliore con gli ingredienti a disposizione",
                AiAgent.Role.AGENT);
        AiAgent aiDroghiere = new AiAgent(groq_infora, "droghiere",
                "Il proprietario di un grocery shop. Ha un ottima conoscenza degli ingredienti e della loro qualita'.",
                "Restituisce una lista degli ingredienti a disposizione sulla base delle richieste",
                AiAgent.Role.TOOL);

        politburo = new Politburo(List.of(aiChairman, aiScarlet, aiDroghiere));
        politburo.setWorkflow(Politburo.Workflow.HIERARCHICAL);
    }

    public void start() {
        initPolitburo();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ciao sono Scarlet, come ti chiami? ");
        String nome = scanner.nextLine();

        AiAgent aiUser = new AiAgent(null, nome,
                "L'acquirente, molto simpatico e accomodante",
                "Esprime le sue preferenze e chiede che venga eseguita la sua ricetta",
                AiAgent.Role.USER);
        politburo.addAgent(aiUser);

        System.out.println("Ciao " + nome + "! Dimmi che ricetta vuoi preparare?");
        String query = "Richiesta dello user e obbiettivo da perseguire: " + scanner.nextLine();
        politburo.execute(query);

        do {
            System.out.print("Continue> ");
            query = scanner.nextLine();
        } while (!query.equals("stop"));
        scanner.close();
    }

    /**
     * Politburo Interactive Test
     */
    public static void main(String[] args) {
        TestPolitburoCucina testPolitburoCucina = new TestPolitburoCucina();
        testPolitburoCucina.start();

    }

}
