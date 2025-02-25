package tk.artsakenos.iperunits.llm;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tk.artsakenos.iperunits.initors.SuperProperties;
import tk.artsakenos.iperunits.llm.models.Cerebras;
import tk.artsakenos.iperunits.llm.models.Groq;
import tk.artsakenos.iperunits.llm.politburo.AiAgent;
import tk.artsakenos.iperunits.llm.politburo.Politburo;
import tk.artsakenos.iperunits.system.SuperTimer;

import java.util.List;

@Log
@Disabled
public class TestPolitburo {

    @Test
    void testPolitburo() {
        Assistant groq = new Groq(
                SuperProperties.get("infodev.properties", "APIKEY_GROQ", null),
                Groq.MODEL_LLAMA3_70B);
        Assistant groq_infora = new Groq(
                SuperProperties.get("infodev.properties", "APIKEY_GROQ_INFORA", null),
                Groq.MODEL_LLAMA3_70B);
        Assistant cerebras = new Cerebras(
                SuperProperties.get("infodev.properties", "APIKEY_CEREBRAS", null),
                Cerebras.MODEL_LLAMA_70B);

        AiAgent aiMarito = new AiAgent(groq, "marito",
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
        AiAgent aiSecretary = new AiAgent(groq, "secretary",
                "Segretario. Quando verrà dichiarata connclusa la discussione, crea un file di sintesi.",
                "",
                AiAgent.Role.TOOL);

        Politburo politburo = new Politburo(List.of(aiMarito, aiMoglie, aiConsulente, aiChairman, aiSecretary));
        politburo.setWorkflow(Politburo.Workflow.HIERARCHICAL);

        for(int i = 0; i < 10; i++) {
            politburo.execute("Si devono comprare dei nuovi vestiti, ma i coniugi litigano in continuazione.");
            SuperTimer.sleep(20_000);
        }
    }

}
