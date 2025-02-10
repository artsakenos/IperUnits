package tk.artsakenos.iperunits.web;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tk.artsakenos.iperunits.initors.SuperProperties;
import tk.artsakenos.iperunits.web.pebblechain.Pebble;
import tk.artsakenos.iperunits.web.social.Pastebin;

@Disabled
@Log
public class TestPeebleChain {

    @Test
    void testPebble() throws Pebble.PebbleException {
        Pebble pebble01 = new Pebble("My New Pebble 01");
        pebble01.mineBlock();
        log.info("Pebble 01: " + pebble01.toJson());

        Pebble pebble02 = new Pebble(pebble01, "My New Pebble 02");
        pebble02.mineBlock();
        log.info("Pebble 02: " + pebble02.toJson());
    }

    @Test
    void testPebbleChain() throws Pebble.PebbleException {
        String api_key = SuperProperties.get("infodev.properties", "APIKEY_PASTEBIN", null);
        Pastebin pastebin = new Pastebin(api_key);

        Pebble pebble01 = new Pebble("My New Pebble 01");
        pebble01.mineBlock();
        log.info("Pebble 01: " + pebble01.toJson());
        String pebble01Url = pastebin.postPastebin(pebble01.toJson(), pebble01.getHash(), "json", "1", "1W");

        Pebble pebble02 = new Pebble(pebble01, "My New Pebble 02");
        pebble02.setPreviousLinks(new String[]{pebble01Url});
        pebble02.mineBlock();
        log.info("Pebble 02: " + pebble02.toJson());
        String pebble02Url = pastebin.postPastebin(pebble02.toJson(), pebble02.getHash(), "json", "1", "1W");

        // Testiamo se la catena è integra.
        pastebin.getPastebin(pebble02Url);
        // ...
    }

    @Test
    void testFromJson() {
        String json = "{\n" +
                "  \"previousHash\" : \"bc00d64d8b38a8de0855e2111d4779711191cdeae55963ada29b85016b268dd5\",\n" +
                "  \"previousLinks\" : [ \"https://pastebin.com/hyr9CJMx\" ],\n" +
                "  \"target\" : \"bc00\",\n" +
                "  \"depth\" : 1,\n" +
                "  \"verbose\" : true,\n" +
                "  \"owner\" : \"bc00\",\n" +
                "  \"data\" : \"My New Pebble 02\",\n" +
                "  \"created\" : 1731240500307,\n" +
                "  \"hash\" : \"bc00856014efe038d2cfcc8838a5e816e2175fd0960108f69767950fbebc78f8\",\n" +
                "  \"merkle\" : \"4f47cd8759bc9efe7948f10ba3b9f3fdb745d355bd0e4c2b99945e61185669fa\",\n" +
                "  \"nonce\" : 104236,\n" +
                "  \"createdISO8601\" : \"2024-11-10T12:08:20.307Z\",\n" +
                "  \"value\" : 8.651588E-10,\n" +
                "  \"id\" : \"\uD83D\uDC8Ebc00#bc00856014efe038d2cfcc8838a5e816e2175fd0960108f69767950fbebc78f8\",\n" +
                "  \"valid\" : true\n" +
                "}";
        Pebble pebble = Pebble.fromJson(json);
        assert pebble != null;
        log.info(pebble.toJson());
    }

}
