package tk.artsakenos.iperunits.web.social;

import tk.artsakenos.iperunits.web.SuperHttpClient;

import java.util.Map;

/**
 * Nota che verranno postati come guest, a meno che non si faccia uno user login e si inserisca lo user key nel form.
 * <a href="https://pastebin.com/doc_api">PasteBin API</a>
 */
public class Pastebin {

    private final SuperHttpClient client;
    private static final String PASTEBIN_URL = "https://pastebin.com";
    private static final String API_POST_ENDPOINT = "/api/api_post.php";
    private static final String API_RAW_URL = "/raw";
    private final String pastebinKey;

    public Pastebin(String pastebin_key) {
        this.pastebinKey = pastebin_key;
        this.client = new SuperHttpClient(PASTEBIN_URL, null, null);
    }

    public String getPastebin(String pastebinId) {
        SuperHttpClient.SuperResponse response = client.getJson(API_RAW_URL + "/" + pastebinId, null, null);
        if (response.isSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to fetch paste: " + response);
        }
    }

    /**
     * @param pasteContent    A String of content!
     * @param pasteName       Name / Title of your paste
     * @param pasteFormat     php, text, ...
     * @param pastePrivacy    0=public 1=unlisted 2=private
     * @param pasteExpireDate 10M = 10 Minutes, 1H = 1 Hour, 1D = 1 Day, 1W = 1 Week, 1M = 1 Month, 1Y = 1 Year
     * @return the url of the new pastebin
     */
    public String postPastebin(String pasteContent, String pasteName, String pasteFormat,
                               String pastePrivacy, String pasteExpireDate) {
        Map<String, String> params = Map.of(
                "api_dev_key", pastebinKey,
                "api_option", "paste",
                "api_paste_code", pasteContent,
                "api_paste_name", pasteName,
                "api_paste_format", pasteFormat,
                "api_paste_private", pastePrivacy,
                "api_paste_expire_date", pasteExpireDate
        );

        SuperHttpClient.SuperResponse response = client.postXForm(
                API_POST_ENDPOINT,
                params, null, null, "POST");
        if (response.isSuccessful()) {
            return response.getBody(); // URL of the created paste
        } else {
            throw new RuntimeException("Failed to create paste: " + response);
        }
    }

}
