package tk.artsakenos.iperunits.web;

import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tk.artsakenos.iperunits.initors.SuperProperties;

@Log
@Disabled
public class TestSuperHttpESClient {

    private static SuperHttpClient superHttpClient;

    @BeforeAll
    public static void setup() {
        superHttpClient = new SuperHttpClient(
                SuperProperties.get("infodev.properties", "ES_SEARCHLY_INFODEV.URL", null),
                SuperProperties.get("infodev.properties", "ES_SEARCHLY_INFODEV.USER", null),
                SuperProperties.get("infodev.properties", "ES_SEARCHLY_INFODEV.PASSWORD", null));
    }

    @Test
    public void see() {
        SuperHttpClient.SuperResponse jsonResponse = superHttpClient.getJson("", null, null);
        log.info(jsonResponse.toString());
    }

}