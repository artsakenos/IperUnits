package tk.artsakenos.iperunits.llm.politburo;

import lombok.Data;
import lombok.extern.java.Log;
import tk.artsakenos.iperunits.file.FileManager;
import tk.artsakenos.iperunits.llm.LlmService;
import tk.artsakenos.iperunits.llm.Message;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings({"unused"})
@Data
@Log
public class Politburo {

    @SuppressWarnings("unused")
    public enum Workflow {
        SEQUENTIAL,
        HIERARCHICAL,
        FREE
    }

    private final LinkedList<AiAgent> members;
    private Workflow workflow = Workflow.SEQUENTIAL;
    private int interaction_limit = 10;
    private AiAgent currentSpeaker;
    private LinkedList<String> conversation = new LinkedList<>();
    private final String promptTemplate;

    // Prompt Variabiles
    private String promptPolitburoTeam() {
        StringBuilder team = new StringBuilder();
        for (AiAgent member : members) {
            team.append("    ").append(member.getName()).append(": ")
                    .append(member.getProfile().replaceAll("\n", " ")).append("\n");
        }
        return team.toString().trim();
    }

    private String promptPolitburoConversation() {
        StringBuilder messages = new StringBuilder();
        for (String message : conversation) {
            messages.append("    ").append(message).append("\n");
        }
        return messages.toString().trim();
    }


    // -----------------------------------------------------------------------------------------------------------------

    public Politburo(List<AiAgent> members) {
        this.members = new LinkedList<>(members);
        this.promptTemplate = FileManager.getAssetString(this, "/politburo/politburo_main.prompt");
    }

    public void addAgent(AiAgent agent) {
        this.members.add(agent);
    }

    public void execute(String current_target) {
        next();
        String currentSystem = promptTemplate
                .replace("{politburo.team}", promptPolitburoTeam())
                .replace("{politburo.target}", current_target)
                .replace("{politburo.conversation}", promptPolitburoConversation())
                .replace("{agent.name}", currentSpeaker.getName())
                .replace("{agent.profile}", currentSpeaker.getProfile().replaceAll("\n", "    "))
                .replace("{agent.target}", currentSpeaker.getTarget());
        LlmService llmService = new LlmService();
        llmService.addMessage(currentSpeaker.getAssistant(), Message.Role.user, currentSystem);
        Message answer = llmService.query();

        System.out.println("   -------------------------------------------------------------");
        System.out.println("   --- Current System Prompt for the Next Agent ----------------");
        System.out.println("   -------------------------------------------------------------");
        System.out.println(currentSpeaker.getName() + "> " + currentSystem + "\n");

        System.out.println("   -------------------------------------------------------------");
        System.out.println("   --- Current Agent Answer ------------------------------------");
        System.out.println("   -------------------------------------------------------------");
        System.out.printf("%s> %s", currentSpeaker.getName(), answer.getText() + "\n\n");

        // log.info("{}> {}", currentSpeaker.getName(), currentSystem);
        // log.info("{}> {}", currentSpeaker.getName(), answer.getText());

        conversation.add(currentSpeaker.getName() + ": " + answer.getText().replace("\n", " "));
    }

    private void next() {
        if (currentSpeaker == null) {
            currentSpeaker = members.getFirst();
        } else {
            int index = members.indexOf(currentSpeaker);
            if (index >= members.size()) index = 0;
            currentSpeaker = members.get(++index);
        }
    }


}
