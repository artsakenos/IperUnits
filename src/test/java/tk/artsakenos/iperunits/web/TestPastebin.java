package tk.artsakenos.iperunits.web;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tk.artsakenos.iperunits.initors.SuperProperties;
import tk.artsakenos.iperunits.web.social.Pastebin;

@Disabled
public class TestPastebin {


    @Test
    void testPastebin() {
        String api_key = SuperProperties.get("infodev.properties", "APIKEY_PASTEBIN", null);

        Pastebin pastebin = new Pastebin(api_key);
        String pasteBin = pastebin.getPastebin("agNXLHf3"); // A Random Pastebin
        System.out.println(pasteBin);

        String pasteUrl = pastebin.postPastebin("My new content", "Content Title", "text", "0", "10M");
        System.out.println(pasteUrl);
    }
}
