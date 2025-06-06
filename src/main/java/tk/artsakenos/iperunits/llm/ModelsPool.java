package tk.artsakenos.iperunits.llm;

import tk.artsakenos.iperunits.system.SuperTimer;

import java.util.LinkedHashMap;
import java.util.TreeMap;

import static tk.artsakenos.iperunits.string.SuperDate.now;

/**
 * ModelsPool is a collection of available LLM models.
 * It allows you to list the models, check their availability,
 * and route queries to the appropriate model based on availability.
 */
public class ModelsPool extends LinkedHashMap<String, Assistant> {

    /**
     * A list of the available models with their description.
     *
     * @return The list of available models with their description.
     */
    public TreeMap<String, String> descriptions() {
        TreeMap<String, String> descriptions = new TreeMap<>();
        for (String key : this.keySet()) {
            Assistant assistant = this.get(key);
            descriptions.put(key, assistant.getDescription() + " (" + assistant.getModel() + ")");
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
                    return assistant;
                }
                SuperTimer.sleep(1000);
            }
        }
        return null;
    }

    public Message query(Conversation conversation) {
        Message answer = LlmService.query(conversation, route());
        answer.getAssistant().setLastCallTimestamp(now());
        if (answer.getRole() == Message.Role.error) {
            return query(conversation);
        } else {
            return answer;
        }
    }

}
