/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.string;

import lombok.extern.java.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import static tk.artsakenos.iperunits.file.FileManager.getAssetString;
import static tk.artsakenos.iperunits.file.SuperFileText.getText;

/**
 * Crea una grammatica. Vedere Grammatica_Template_Help.txt.
 * <pre>
 * gram.addGrammarResource(gram, "/grammars/Grammatica_Template.txt");
 * oppure
 * addGrammarValue(...) etc...
 * gram.addGrammarResource(gram, "/grammars/SoftwareEngineering.txt");
 * System.out.println(gram.produce("Nuova Libreria IperUnits: {iperlibreria}"));
 *
 * gram.addGrammarResource(gram, "/grammars/{nome_proprio_femminile}.txt");
 * gram.addGrammarResource(gram, "/grammars/{nome_proprio_maschile}.txt");
 * gram.addGrammarResource(gram, "/grammars/{cognome}.txt");
 * gram.addGrammar("{nome}:={nome_proprio_femminile}|{nome_proprio_maschile}");
 * </pre>
 * <p>
 * Gestisce la ricorsione, la non ripetizione,...
 *
 * @author Andrea
 */
@SuppressWarnings("unused")
@Log
public class SuperGrammar {

    private final HashMap<String, ArrayList<String>> grammar = new HashMap<>();
    // Funzionamento attuale degli usages. Sono impostati a uno per ogni valore di un'espressione.
    // Quando si usa un valore, il suo usage si dimezza, perché indica un po' la probabilità che possa essere riscelto.
    // Poi in select(...) faccio in modo che siano sempre normalizzati a 1
    private final HashMap<String, double[]> usage = new HashMap<>();

    private static final Random RANDOM = new Random(SuperDate.now());

    /**
     * Permette di inserire un espressione del tipo {expression}:={expressions}
     *
     * @param term        Il termine in formato {expression}, con le parentesi.
     * @param expressions Le espressioni che verranno sostituite.
     */
    public void addGrammarValue(String term, ArrayList<String> expressions) {
        if (grammar.containsKey(term)) {
            log.info("SuperGrammar: Attenzione, esisteva già un " + term + " e verrà sovrascritto!");
        }
        grammar.put(term, expressions);
        double[] usage_array = new double[expressions.size()];
        Arrays.fill(usage_array, 1);
        usage.put(term, usage_array);
    }

    /**
     * Aggiunge un grammar_text, cioè un testo con tutte le grammatiche per ogni
     * linea. Può essere ovviamente anche una sola linea. Può essere chiamato
     * più volte per aggiungere più testi.
     *
     * @param grammar_text Il grammar text secondo la sintassi (vedi descrizione
     *                     classe).
     */
    public void addGrammar(String grammar_text) {
        for (String line : grammar_text.replaceAll("\r", "").split("\n")) {
            line = line.trim();
            if (line.startsWith("#") || line.isEmpty()) {
                continue;
            }

            if (!line.contains(":=") || line.endsWith(":=") || line.startsWith(":=")) {
                log.info("SuperGrammar: Malformed Grammar, no ':=' or key/value pairs  >> " + line);
                continue;
            }

            String key = line.split(":=")[0].trim();
            String values = line.split(":=")[1].trim();

            if (!key.startsWith("{") || !key.endsWith("}")) {
                log.info("SuperGrammar: Malformed Grammar, key format must be {key} >> " + key);
                continue;
            }

            ArrayList<String> values_array = new ArrayList<>();
            for (String value : values.split("\\|")) {
                values_array.add(value.trim());
            }

            addGrammarValue(key, values_array);
        }
    }

    public void addGrammarFile(String grammar_file_path) {
        String grammar_text = getText(grammar_file_path);
        File f = new File(grammar_file_path);
        String file_name = f.getName().substring(0, f.getName().lastIndexOf("."));
        if (file_name.startsWith("{") && file_name.endsWith("}")) {
            ArrayList<String> values_array = new ArrayList<>();
            for (String value : grammar_text.replace("\r", "").split("\n")) {
                values_array.add(value.trim());
            }
            addGrammarValue(file_name, values_array);
            return;
        }

        addGrammar(grammar_text);
    }

    /**
     * Ricorda che se un file è del tipo /.../{nome}.ext, {nome} caricherà tutte
     * le linee come valori associati a una chiave che è il nome del file.
     *
     * @param resource_parent            Un istanza dallo stesso package delle risorse
     * @param grammar_file_resource_path Il path della risorsa
     */
    public void addGrammarResource(Object resource_parent, String grammar_file_resource_path) {
        String grammar_text = getAssetString(resource_parent, grammar_file_resource_path);
        File f = new File(grammar_file_resource_path);
        String file_name = f.getName().substring(0, f.getName().lastIndexOf("."));
        if (file_name.startsWith("{") && file_name.endsWith("}")) {
            ArrayList<String> values_array = new ArrayList<>();
            if (grammar_text != null)
                for (String value : grammar_text.replace("\r", "").split("\n")) {
                    values_array.add(value.trim());
                }
            addGrammarValue(file_name, values_array);
            return;
        }

        if (grammar_text != null)
            addGrammar(grammar_text);
    }

    /**
     * Produce un testo a partire da una formula grammaticale. In pratica un
     * testo che può contenere delle chiavi in ogni posizione.
     *
     * @param expression L'espressione di partenza
     * @return Una frase prodotta tramite quella espressione
     */
    public String produce(String expression) {

        // Terminale
        if (!expression.contains("{") && !expression.contains("}")) {
            return expression;
        }

        // Solo chiave
        if (expression.startsWith("{") && (expression.indexOf("}") == expression.length() - 1)) {
            String value = select(expression);
            return produce(value);
        }

        // Altrimenti cerco la prima chiave e la sostituisco.
        // Questa strategia è migliore per tutti i casi tipo {}{}, {} frase, etc...
        int left = expression.indexOf("{");
        int right = expression.indexOf("}") + 1;
        int length = expression.length();
        // System.out.println("DEBUG>" + expression + ":=" + left + ", " + right + ", " + length + ";");
        String part_left = expression.substring(0, left);
        String part_center = expression.substring(left, right);
        String part_right = expression.substring(right, length);
        return produce(part_left) + produce(part_center) + produce(part_right);
    }

    // -------------------------------------------------------------------------

    /**
     * Seleziona un valore da una grammatica, effettuando tutte le operazioni di
     * utilizzo e/o memorizzazione del caso.
     *
     * @param term Il termine chiave da sostituire
     * @return il termine sostituito
     */
    private String select(String term) {
        ArrayList<String> values = grammar.get(term);
        if (values == null) {
            log.info("SuperGrammar: Warning, no key " + term + ".");
            return term.substring(1, term.length() - 1);
        }
        // Modo banale, prendere un valore random
        int selected_index = RANDOM.nextInt(values.size());

        // Metodo più da fighetti da old square, aggiustarsi coi pesi).
        // Nota: il metodo di selezionare il max, è problematico perché obbliga a aggiornare una lista di indici
        // Con il metodo probabilistico si fa più in fretta.
        double[] usages = usage.get(term);
        double max_probability_value = 0;
        for (int i = 0; i < usages.length; i++) {
            double probability = RANDOM.nextDouble() * usages[i];
            if (probability > max_probability_value) {
                max_probability_value = probability;
                selected_index = i;
            }
        }

        // Prendiamo le cose e sistemiamo gli usages.
        usages[selected_index] /= 2.0;

        // Rinormalizziamo, se serve (dovrebbe bastare un *2 con certe condizioni.
        return values.get(selected_index);
    }
}
