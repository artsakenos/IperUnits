package tk.artsakenos.iperunits.string;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
@Log
public class TestSillabator {

    @Test
    public void sillabator() {
        Sillabator sillabator = new Sillabator();
        String frase = sillabator.creaFrase();
        log.info("Frase: " + frase);
    }

}
