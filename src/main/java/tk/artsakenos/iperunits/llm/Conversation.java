package tk.artsakenos.iperunits.llm;

import java.util.LinkedList;

public class Conversation extends LinkedList<Message> {

    public Message getLast(Message.Role role) {
        for (Message message : this) {
            if (message.getRole() == role) return message;
        }
        return null;
    }

    public Message getQuery(){
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
