package tk.artsakenos.iperunits.llm.models;

import tk.artsakenos.iperunits.llm.Assistant;

import java.util.TreeMap;

public class AvailableModels extends TreeMap<String, Assistant> {

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

}
