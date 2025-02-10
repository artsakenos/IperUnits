package tk.artsakenos.iperunits.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Dalla versione 7 è possibile all'interno di un indice definire diversi tipi, e.g.,:
 * <a href="https://index.com/main_index/preference/01">Esempio 01</a>
 * <p>
 * Esempio di ricerca:
 * https://index.com/main_index/preference/_search
 *
 * <pre>
 *     ElasticSearchClient client = new ElasticSearchClient(
 *     "http://localhost:9200",
 *     "user",
 *     "password"
 * );
 *
 * // Ricerca
 * List<MyDocument> docs = client.searchGet("my_index", "field:value", MyDocument.class);
 *
 * // Salvataggio
 * MyDocument doc = new MyDocument();
 * client.index("my_index", "doc_id", doc);
 *
 * // Recupero per ID
 * Optional<MyDocument> doc = client.get("my_index", "doc_id", MyDocument.class);
 * </pre>
 */
@SuppressWarnings({"unused"})
@Log
public class SuperElasticsearch {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final SuperHttpClient httpClient;
    private static final String ES_INDICES = "/_cat/indices?v";
    private static final String ES_SEARCH = "/_search";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ElasticHit {
        public String index;
        public String id;
        public Double score;
        public JsonNode source;

        public <T> T convertSource(Class<T> clazz) {
            return MAPPER.convertValue(source, clazz);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class SearchResponse {
        public JsonNode hits;

        public List<ElasticHit> getHits() {
            if (hits == null || !hits.has("hits")) {
                return Collections.emptyList();
            }

            List<ElasticHit> results = new ArrayList<>();
            hits.get("hits").forEach(hit -> {
                ElasticHit elasticHit = new ElasticHit();
                elasticHit.index = hit.get("_index").asText();
                elasticHit.id = hit.get("_id").asText();
                if (hit.has("_score") && !hit.get("_score").isNull()) {
                    elasticHit.score = hit.get("_score").asDouble();
                }
                elasticHit.source = hit.get("_source");
                results.add(elasticHit);
            });
            return results;
        }
    }

    public SuperElasticsearch(String baseUrl, String user, String password) {
        this.httpClient = SuperHttpClient.builder()
                .baseurl(baseUrl)
                .user(user)
                .password(password)
                .build();
    }

    /**
     * Esegue una ricerca su Elasticsearch usando una query in formato stringa
     *
     * @param index       L'indice su cui cercare
     * @param query       La query in formato stringa per ES
     * @param resultClass La classe in cui convertire i risultati
     * @return Lista di risultati convertiti nel tipo richiesto
     */
    public <T> List<T> searchGet(String index, String query, Class<T> resultClass) {
        try {
            String endpoint = "/" + index + ES_SEARCH;
            Map<String, String> params = Map.of("q", query);

            SuperHttpClient.SuperResponse response = httpClient.getJson(endpoint, null, null);
            if (!response.isSuccessful()) {
                log.warning("Search failed with code: " + response.getCode());
                return Collections.emptyList();
            }

            SearchResponse searchResponse = MAPPER.readValue(response.getBody(), SearchResponse.class);
            return searchResponse.getHits().stream()
                    .map(hit -> hit.convertSource(resultClass))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.severe("Error during search: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Esegue una ricerca su Elasticsearch usando una query in formato JSON
     *
     * @param index       L'indice su cui cercare
     * @param jsonQuery   La query in formato JSON
     * @param resultClass La classe in cui convertire i risultati
     * @return Lista di risultati convertiti nel tipo richiesto
     */
    public <T> List<T> searchPost(String index, String jsonQuery, Class<T> resultClass) {
        try {
            String endpoint = "/" + index + ES_SEARCH;

            SuperHttpClient.SuperResponse response = httpClient.postJson(
                    endpoint,
                    jsonQuery,
                    Map.of("Content-Type", "application/json"),
                    null
            );

            if (!response.isSuccessful()) {
                log.warning("Search failed with code: " + response.getCode());
                return Collections.emptyList();
            }

            SearchResponse searchResponse = MAPPER.readValue(response.getBody(), SearchResponse.class);
            return searchResponse.getHits().stream()
                    .map(hit -> hit.convertSource(resultClass))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.severe("Error during search: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Salva o aggiorna un documento in Elasticsearch
     *
     * @param index    L'indice dove salvare il documento
     * @param id       L'ID del documento (opzionale)
     * @param document Il documento da salvare
     * @return true se l'operazione è andata a buon fine
     */
    public boolean index(String index, String type, String id, Object document) {
        try {
            String endpoint = "/" + index + "/" + type + (id != null ? "/" + id : "");
            String jsonBody = MAPPER.writeValueAsString(document);

            SuperHttpClient.SuperResponse response = httpClient.postJson(
                    endpoint,
                    jsonBody,
                    Map.of("Content-Type", "application/json"),
                    null
            );

            return response.isSuccessful();
        } catch (Exception e) {
            log.severe("Error during indexing: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera un documento per ID
     *
     * @param index       L'indice da cui recuperare
     * @param id          L'ID del documento
     * @param resultClass La classe in cui convertire il risultato
     * @return Il documento convertito nel tipo richiesto
     */
    public <T> Optional<T> get(String index, String type, String id, Class<T> resultClass) {
        try {
            String endpoint = "/" + index + "/" + type + "/" + id;

            SuperHttpClient.SuperResponse response = httpClient.getJson(endpoint, null, null);
            if (!response.isSuccessful()) {
                return Optional.empty();
            }

            JsonNode root = MAPPER.readTree(response.getBody());
            if (!root.has("_source")) {
                return Optional.empty();
            }

            return Optional.of(MAPPER.convertValue(root.get("_source"), resultClass));
        } catch (Exception e) {
            log.severe("Error during get: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Mostra gli indici disponibili
     *
     * @return Lista degli indici come stringhe
     */
    public List<String> listIndices() {
        try {
            SuperHttpClient.SuperResponse response = httpClient.getJson(ES_INDICES, null, null);
            if (!response.isSuccessful()) {
                return Collections.emptyList();
            }

            return Arrays.asList(response.getBody().split("\n"));
        } catch (Exception e) {
            log.severe("Error listing indices: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public SuperHttpClient.SuperResponse see() {
        return httpClient.getJson("", null, null);
    }
}