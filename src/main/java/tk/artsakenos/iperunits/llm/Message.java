package tk.artsakenos.iperunits.llm;

import lombok.Data;

import java.beans.Transient;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


@Data
@SuppressWarnings("unused")
public class Message {

    public enum Role {
        system,
        user,
        assistant,
        context,
        error
    }

    public enum Type {
        text,
        image,
        audio
    }

    private long timestamp;
    private Role role;
    private Assistant assistant;
    private String submodel = null;

    private Map<Type, String> parts = new HashMap<>();

    @Transient
    public String getText() {
        return getParts().get(Type.text);
    }

    public boolean hasImage() {
        return getParts().containsKey(Type.image);
    }

    public Message(Assistant assistant, Role role, Map<Type, String> parts) {
        this.timestamp = Calendar.getInstance(Locale.ITALY).getTimeInMillis();
        this.role = role;
        this.assistant = assistant;
        if (parts != null) this.parts = parts;
    }

    public String toString() {
        return String.format("%s# %s> %s", assistant.getId(), getRole(), getText());
    }

}