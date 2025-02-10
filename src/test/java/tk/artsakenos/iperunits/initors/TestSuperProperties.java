package tk.artsakenos.iperunits.initors;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Testa le superproperties.
 * Utile per creare il default infodev.properties
 * che uso ovunque.
 */
@Log
@Disabled
public class TestSuperProperties {

    @Test
    public void testSuperProperties() {
        SuperProperties superProps = new SuperProperties("infodev.properties");
        log.info("GROQ KEY: " + superProps.get("APIKEY_GROQ", "GROQ KEY"));
        log.info("GOOGLE KEY: " + superProps.get("APIKEY_GOOGLE", "GOOGLE KEY"));
    }

}
