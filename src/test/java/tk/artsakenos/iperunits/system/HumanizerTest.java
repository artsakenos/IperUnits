package tk.artsakenos.iperunits.system;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Log
@Disabled
public class HumanizerTest {

    @Test
    public void humanize() {
        Human.parseCommand("KEYBOARD TYPE Hello World");
    }

    @Test
    public void chiocciola() {
        Keyboard.Write("@");
    }

    @Test
    public void write() {
        Keyboard.Write("aeiouAEIOU");
    }

}
