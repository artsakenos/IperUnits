package tk.artsakenos.iperunits.web;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <pre>
 *     SuperHttpClient client = SuperHttpClient.builder()
 *     .baseurl("https://api.example.com")
 *     .user("username")
 *     .password("password")
 *     .timeout(Duration.ofSeconds(60))
 *     .build();
 *
 * SuperResponse response = client.getJson("/endpoint", Map.of("param", "value"));
 * if (response.isSuccessful()) {
 *     System.out.println("Success: " + response.getResponse());
 * }
 * </pre>
 */
@SuppressWarnings({"unused", "JavadocLinkAsPlainText"})
@Log
public class SuperHttpClient {

    private final HttpClient httpClient;
    private final String baseurl;
    private final String authHeader;

    @Setter
    private Duration timeout = Duration.ofSeconds(30);

    @Data
    @Builder
    public static class SuperResponse {
        private final int code;
        private final String body;
        private final Map<String, String> headers;

        public boolean isSuccessful() {
            return code >= 200 && code < 300;
        }

        @Override
        public String toString() {
            return code + ": " + body;
        }
    }

    /**
     * Costruttore
     *
     * @param baseurl  The Base URL
     * @param user     can be null
     * @param password can be null
     */
    @Builder
    public SuperHttpClient(String baseurl, String user, String password) {
        this.baseurl = Optional.ofNullable(baseurl).orElseThrow(() ->
                new IllegalArgumentException("Base URL cannot be null"));

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Optional.ofNullable(timeout).orElse(timeout))
                .build();

        this.authHeader = Optional.ofNullable(user)
                .flatMap(u -> Optional.ofNullable(password)
                        .map(p -> "Basic " + Base64.getEncoder()
                                .encodeToString((u + ":" + p).getBytes(StandardCharsets.UTF_8))))
                .orElse(null);
    }

    private String buildQueryString(Map<String, String> params) {
        return Optional.ofNullable(params)
                .filter(map -> !map.isEmpty())
                .map(map -> map.entrySet().stream()
                        .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) +
                                "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                        .collect(Collectors.joining("&")))
                .orElse("");
    }

    /**
     * @param endpoint         The Endpoint - After the base url, can be empty.
     * @param jsonBody         The Json Body
     * @param headerParameters The Header Parameters, can be null
     * @param getParameters    The Get Parameters, can be null.
     * @param METHOD           The Method (POST, PUT, DELETE, PATCH)
     * @return A {@link SuperResponse}
     */
    public SuperResponse postJson(String endpoint, String jsonBody,
                                  Map<String, String> headerParameters,
                                  Map<String, String> getParameters,
                                  String METHOD) {
        try {
            String urlWithParams = buildUrl(endpoint, getParameters);
            HttpRequest request = buildRequest(urlWithParams, METHOD, jsonBody, headerParameters);
            SuperResponse response = executeRequest(request);
            if (!response.isSuccessful()) {
                log.severe("# Error during postJson.\n" +
                        "## Url\n" + urlWithParams + "\n" +
                        "## Headers\n" + (headerParameters == null ? "No Headers" : headerParameters.keySet()) + "\n" +
                        "## Request\n" + jsonBody + "\n" +
                        "## Response\n" + response + "\n" +
                        "## Has Auth Header\n" + (authHeader != null) + "\n");
            }
            return response;
        } catch (Exception e) {
            return handleRequestError(e);
        }
    }

    public SuperResponse postJson(String endpoint, String jsonBody,
                                  Map<String, String> headerParameters,
                                  Map<String, String> getParameters) {
        return postJson(endpoint, jsonBody, headerParameters, getParameters, "POST");
    }

    public SuperResponse getJson(String endpoint,
                                 Map<String, String> headerParameters,
                                 Map<String, String> getParameters) {
        try {
            String urlWithParams = buildUrl(endpoint, getParameters);
            HttpRequest request = buildRequest(urlWithParams, "GET", null, headerParameters);
            return executeRequest(request);
        } catch (Exception e) {
            return handleRequestError(e);
        }
    }

    private String buildUrl(String endpoint, Map<String, String> parameters) {
        String queryString = buildQueryString(parameters);
        return baseurl + endpoint + (queryString.isEmpty() ? "" : "?" + queryString);
    }

    private HttpRequest buildRequest(String url, String method, String body,
                                     Map<String, String> headers) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json");

        // Imposta il metodo e il body
        if ("POST".equals(method)) {
            requestBuilder.POST(HttpRequest.BodyPublishers
                    .ofString(Optional.ofNullable(body).orElse("")));
        } else {
            requestBuilder.GET();
        }

        // Aggiunge l'header di autenticazione se presente
        Optional.ofNullable(authHeader)
                .ifPresent(auth -> requestBuilder.header("Authorization", auth));

        // Aggiunge gli header personalizzati, gestendo l'host in modo speciale
        if (headers != null) {
            headers.forEach((key, value) -> {
                if ("Host".equalsIgnoreCase(key)) {
                    // Per l'header Host, usa setHeader invece di header
                    requestBuilder.setHeader("Host", value);
                } else {
                    requestBuilder.header(key, value);
                }
            });
        }

        return requestBuilder.build();
    }

    private SuperResponse executeRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return SuperResponse.builder()
                .code(response.statusCode())
                .body(response.body())
                .headers(response.headers().map().entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> String.join(", ", e.getValue()))))
                .build();
    }

    private SuperResponse handleRequestError(Exception e) {
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        return SuperResponse.builder()
                .code(500)
                .body("Request failed: " + e.getMessage())
                .build();
    }

    // ------------------------------------------------------------------------------------------

    /**
     * @param endpoint         The Endpoint - After the base url, can be empty.
     * @param formData         Map containing form data key-value pairs
     * @param headerParameters The Header Parameters, can be null
     * @param getParameters    The Get Parameters, can be null.
     * @param METHOD           The Method (POST, PUT, DELETE, PATCH)
     * @return A {@link SuperResponse}
     */
    public SuperResponse postXForm(String endpoint, Map<String, String> formData,
                                   Map<String, String> headerParameters,
                                   Map<String, String> getParameters,
                                   String METHOD) {
        try {
            String urlWithParams = buildUrl(endpoint, getParameters);

            String encodedFormData = "";
            if (formData != null && !formData.isEmpty()) {
                encodedFormData = formData.entrySet().stream()
                        .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                                URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                        .collect(Collectors.joining("&"));
            }

            HttpRequest request = buildXFormRequest(urlWithParams, METHOD, encodedFormData, headerParameters);
            SuperResponse response = executeRequest(request);
            if (!response.isSuccessful()) {
                log.severe("# Error during postXForm.\n" +
                        "## Url\n" + urlWithParams + "\n" +
                        "## Headers\n" + headerParameters.keySet() + "\n" +
                        "## Request\n" + encodedFormData + "\n" +
                        "## Response\n" + response + "\n");
            }
            return response;
        } catch (Exception e) {
            return handleRequestError(e);
        }
    }

    /**
     * Builds an HTTP request for x-www-form-urlencoded content type
     */
    private HttpRequest buildXFormRequest(String url, String method, String formData,
                                          Map<String, String> headers) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded");

        // Set method and body
        if ("POST".equals(method)) {
            requestBuilder.POST(HttpRequest.BodyPublishers
                    .ofString(Optional.ofNullable(formData).orElse("")));
        } else if ("PUT".equals(method)) {
            requestBuilder.PUT(HttpRequest.BodyPublishers
                    .ofString(Optional.ofNullable(formData).orElse("")));
        } else if ("PATCH".equals(method)) {
            requestBuilder.method("PATCH", HttpRequest.BodyPublishers
                    .ofString(Optional.ofNullable(formData).orElse("")));
        } else if ("DELETE".equals(method)) {
            requestBuilder.method("DELETE", HttpRequest.BodyPublishers
                    .ofString(Optional.ofNullable(formData).orElse("")));
        } else {
            requestBuilder.GET();
        }

        // Add authentication header if present
        Optional.ofNullable(authHeader)
                .ifPresent(auth -> requestBuilder.header("Authorization", auth));

        // Add custom headers, handling Host header specially
        if (headers != null) {
            headers.forEach((key, value) -> {
                if ("Host".equalsIgnoreCase(key)) {
                    requestBuilder.setHeader("Host", value);
                } else {
                    requestBuilder.header(key, value);
                }
            });
        }

        return requestBuilder.build();
    }


}