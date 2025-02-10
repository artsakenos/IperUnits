package tk.artsakenos.iperunits.llm.politburo;

import lombok.Data;
import tk.artsakenos.iperunits.llm.Assistant;

@Data
public class AiAgent {

    private final Assistant assistant;
    private final String name;
    private final String profile;
    private final String target;
    private final Role role;

    public AiAgent(Assistant assistant, String name, String profile, String target, Role role) {
        this.assistant = assistant;
        this.name = name;
        this.profile = profile;
        this.target = target;
        this.role = role;
    }

    @SuppressWarnings("unused")
    public enum Role {
        AGENT, // A normal agent
        COORDINATOR, // A coordinato, i.e., a Chairman
        TOOL // A Tool agent, execute  operations such as create files, send emails...
    }

}
