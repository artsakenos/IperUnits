package tk.artsakenos.iperunits.llm;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tk.artsakenos.iperunits.llm.politburo.AiAgent;
import tk.artsakenos.iperunits.llm.politburo.Politburo;
import tk.artsakenos.iperunits.system.SuperTimer;

import java.util.List;

@Log
@Disabled
public class TestPolitburoCouple {

    @Test
    void testPolitburo() {
        Assistant groq_infora = HelperModelsPool.getAssistant("groq_infora_llama3_70b");
        Assistant cerebras = HelperModelsPool.getAssistant("cerebras_llama31_8b");
        Assistant gemini = HelperModelsPool.getAssistant("google_gemini_20_flash");

        AiAgent aiMarito = new AiAgent(gemini, "marito",
                "accondiscende la moglie in tutto e per tutto",
                "cerca di accontentare la moglie sempre e comunque",
                AiAgent.Role.AGENT);
        AiAgent aiMoglie = new AiAgent(groq_infora, "moglie",
                "moglie viziata e pretenziosa",
                "non da tregua al marito, chiede sempre di più",
                AiAgent.Role.AGENT);
        AiAgent aiConsulente = new AiAgent(cerebras, "consulente",
                "Consulente matrimoniale professionista con anni di esperienza alle spalle. Assertivo e determinato.",
                "tenta di mettere la pace tra i coniugi",
                AiAgent.Role.AGENT);
        AiAgent aiChairman = new AiAgent(cerebras, "chairman",
                """
                        - Agente di sistema per la gestione dei turni di parola
                        - Analizza il contesto della conversazione per determinare chi deve parlare
                        - Segue rigide regole procedurali del processo penale
                        - Non partecipa attivamente alla conversazione
                        """,
                """
                        Tu sei il coordinatore del gruppo.
                        Rispondi esclusivamente con il nome del prossimo attore che deve parlare.
                        Vincoli da rispettare:
                          - Rispondere SOLO con il nome dell'agente che deve parlare
                          - Seguire l'ordine procedurale che tiene conto degli interventi precedenti e di eventuali ruoli di coordinazione
                        """,
                AiAgent.Role.COORDINATOR);
        AiAgent aiSecretary = new AiAgent(gemini, "secretary",
                "Segretario. Quando verrà dichiarata connclusa la discussione, crea un file di sintesi.",
                "",
                AiAgent.Role.TOOL);

        Politburo politburo = new Politburo(List.of(aiMarito, aiMoglie, aiConsulente, aiChairman, aiSecretary));
        politburo.setWorkflow(Politburo.Workflow.HIERARCHICAL);

        for (int i = 0; i < 10; i++) {
            politburo.execute("Si devono comprare dei nuovi vestiti, ma i coniugi litigano in continuazione.");
            SuperTimer.sleep(20_000);
        }
    }

}
