package tk.artsakenos.iperunits.llm;

import tk.artsakenos.iperunits.system.SuperTimer;

import java.util.LinkedHashMap;
import java.util.TreeMap;

import static tk.artsakenos.iperunits.string.SuperDate.now;

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
        for (int i = 0; i < 60; i++) {
            for (String key : this.keySet()) {
                Assistant assistant = this.get(key);
                if (assistant.isAvailable()) {
                    assistant.setLastCallTimestamp(now());
                    return assistant;
                }
            }
            SuperTimer.sleep(1000);
        }
        return null;

    }

}
