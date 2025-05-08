package tk.artsakenos.iperunits.llm;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class ModelsPool extends LinkedHashMap<String, Assistant> {

    /**
     * A list of the available models with their description.
     *
     * @return The list.
     */
    public TreeMap<String, String> list() {
        TreeMap<String, String> descriptions = new TreeMap<>();
        for (String key : this.keySet()) {
            Assistant assistant = this.get(key);
            descriptions.put(key, assistant.getDescription());
        }
        return descriptions;
    }

    /**
     * This method checks which of the models is available
     * (0 cooloff and context windows enough)
     *
     * @return The available Assistant
     */
    public Assistant route() {
        for (String key : this.keySet()) {
            Assistant assistant = this.get(key);
        }
        return null;

    }

}
