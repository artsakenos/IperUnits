/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.initors;

import tk.artsakenos.iperunits.file.FileManager;
import tk.artsakenos.iperunits.file.SuperFileText;

import java.util.HashMap;

/**
 * Un file ini è un file con delle sezioni divise in paragrafi. I Paragrafi sono
 * delimitati da righe contenenti solo [NOME PARAGRAFO] e [/NOME PARAGRAFO] Sono
 * considerate linee di commento le linee vuote e le linee che iniziano con #
 * <p>
 * Cosa c'è di diverso da Preferences? Che FileIni è più facile da editare a
 * mano.
 *
 * @author Andrea
 */
@SuppressWarnings("unused")
public class FileIni extends HashMap<String, String> {

    /**
     * @param resourcePath e.g., "/Samples/ExperimentSuite/ClassifierScript.txt"
     */
    public void loadFromResource(String resourcePath) {
        ///-{ Loads Available Templates
        String res = FileManager.getAssetString(new FileIni(), resourcePath);
        loadFromString(res);
    }

    /**
     * @param fileName e.g., "/Samples/ExperimentSuite/ClassifierScript.txt"
     */
    public void loadFromFile(String fileName) {
        String iniContent = SuperFileText.getText(fileName);
        loadFromString(iniContent);
    }

    public void saveToFile(String fileName) {
        StringBuilder builder = new StringBuilder();
        for (String key : keySet()) {
            builder.append("[").append(key).append("]\n").append(get(key)).append("\n[/").append(key).append("]\n\n");
        }
        SuperFileText.setText(fileName, builder.toString());
    }

    private void loadFromString(String iniContent) {
        iniContent = iniContent.replaceAll("\r", "");
        String title = "";
        String content = "";

        for (String line : iniContent.split("\n")) {
            if (line.startsWith("[") && line.endsWith("]")
                    && iniContent.indexOf("[/" + line.substring(1), iniContent.indexOf(line)) > 0) {
                // Title Found
                title = line.substring(1, line.length() - 1);
                content = "";
                continue;
            }

            if (line.trim().contains("[/" + title + "]")) {
                put(title, content.substring(0, content.length() - 1));
                // Found enclosure
            }
            content += line + "\n";
        }
    }

    /**
     * In FileIni se la value non esiste viene restituito un empty, mai un null.
     *
     * @param key la key
     * @return la value corrispondente alla key.
     */
    @Override
    public String get(Object key) {
        if (!containsKey(key.toString())) {
            return "";
        }

        return super.get(key);
    }

}
