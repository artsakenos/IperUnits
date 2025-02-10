package tk.artsakenos.iperunits.web;

import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tk.artsakenos.iperunits.initors.SuperProperties;

import java.time.Duration;

@Log
@Disabled
public class TestSuperHttpClient {

    private static SuperHttpClient superHttpClient;

    @BeforeAll
    public static void setup() {
        superHttpClient = new SuperHttpClient("https://generativelanguage.googleapis.com/v1beta/models", null, null);
        superHttpClient.setTimeout(Duration.ofSeconds(120));
    }

    @Test
    public void askQuestion() {
        String Google_APIKEY = SuperProperties.get("infodev.properties", "APIKEY_GOOGLE", null);
        String jsonBody = "{\n" +
                "    \"contents\": [\n" +
                "        {\n" +
                "            \"parts\": [\n" +
                "                {\n" +
                "                    \"text\": \"Write a story about a magic backpack.\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        SuperHttpClient.SuperResponse superResponse = superHttpClient.postJson(
                "/gemini-pro:generateContent",
                jsonBody,
                null,
                java.util.Map.of("key", Google_APIKEY));
        log.info(superResponse.toString());
    }

}