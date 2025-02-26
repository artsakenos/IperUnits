package tk.artsakenos.iperunits.system;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tk.artsakenos.iperunits.file.FileManager;

@Log
@Disabled
public class TestHumanizer {

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

    @Test
    public void testHumanizerScript(){
        String script = FileManager.getAssetString(this, "/system/humanizer_script.txt");
        assert script != null;
        Human.parseScript(script, 1000);
    }
}
