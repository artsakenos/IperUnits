package tk.artsakenos.iperunits.system;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
@Log
public class JavaShellTest {

    UltraJavaShell shell = new UltraJavaShell() {
        @Override
        public String getCommandDescription() {
            return "Command description";
        }

        @Override
        public void onCommand(String command) {

        }
    };

    @Test
    public void start(){
        shell.start();
    }
}
