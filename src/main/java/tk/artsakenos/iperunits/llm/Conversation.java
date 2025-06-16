package tk.artsakenos.iperunits.llm;

import java.util.LinkedList;
import java.util.Map;

public class Conversation extends LinkedList<Message> {

    public void add(Assistant assistant, Message.Role role, String message_text) {
        add(new Message(assistant, role, Map.of(Message.Type.text, message_text)));
    }

    public Message getLast(Message.Role role) {
        for (int i = size() - 1; i >= 0; i--) {
            if (get(i).getRole() == role) return get(i);
        }
        return null;
    }

    public Message getQuery() {
        return getLast(Message.Role.user);
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (Message message : this) {
            output.append(message.toString()).append("\n");
        }
        return output.toString();
    }
}
