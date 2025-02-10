package tk.artsakenos.iperunits.string;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
@Log
public class TestSuperGrammar {

    @Test
    public void produce() {
        SuperGrammar gram = new SuperGrammar();
        gram.addGrammarResource(gram, "/grammars/Grammatica_Template_Help.txt");
        // Oppure addGrammarValue(...) etc...

        gram.addGrammarResource(gram, "/grammars/SoftwareEngineering.txt");
        log.info(gram.produce("Nuova Libreria IperUnits: {iperlibreria}"));

        gram.addGrammarResource(gram, "/grammars/{nome_proprio_femminile}.txt");
        gram.addGrammarResource(gram, "/grammars/{nome_proprio_maschile}.txt");
        gram.addGrammarResource(gram, "/grammars/{cognome}.txt");
        gram.addGrammar("{nome cognome}:={nome_proprio_femminile} {cognome} {cognome}|{nome_proprio_maschile} {cognome}");
        log.info(gram.produce("{nome cognome}"));
    }

}
