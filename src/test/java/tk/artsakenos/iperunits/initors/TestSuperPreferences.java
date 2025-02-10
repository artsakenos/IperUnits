package tk.artsakenos.iperunits.initors;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Log
@Disabled
public class TestSuperPreferences {

    @Test
    void testSuperPreferences() {
        SuperPreferences sp = new SuperPreferences("infodev_preferences.json", false);
        sp.set("chiave", "valore 01\nvalore01 bis");
        log.info("Chiave: " + sp.get("chiave"));
    }

    @Test
    void testWebPreferences() {
        SuperPreferences sp = new SuperPreferences("infodev_preferences.json", true);
        sp.webInit(
                SuperProperties.get("infodev.properties", "ES_SEARCHLY_INFODEV.URL", null),
                SuperProperties.get("infodev.properties", "ES_SEARCHLY_INFODEV.USER", null),
                SuperProperties.get("infodev.properties", "ES_SEARCHLY_INFODEV.PASSWORD", null),
                "main_index",
                "preference",
                "preference_test");

        sp.webSet("My WebKey", "My WebValue");

        log.info(sp.webGet("My WebKey").toString());

    }
}
