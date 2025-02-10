package tk.artsakenos.iperunits.web;

import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tk.artsakenos.iperunits.initors.SuperProperties;

import java.util.Optional;
import java.util.TreeMap;

@Log
@Disabled
public class TestSuperElasticSearch {

    private static SuperElasticsearch superElasticsearch;

    @BeforeAll
    public static void setup() {
        superElasticsearch = new SuperElasticsearch(
                SuperProperties.get("infodev.properties", "ES_SEARCHLY_INFODEV.URL", null),
                SuperProperties.get("infodev.properties", "ES_SEARCHLY_INFODEV.USER", null),
                SuperProperties.get("infodev.properties", "ES_SEARCHLY_INFODEV.PASSWORD", null));
    }

    @Test
    public void see() {
        System.out.println(superElasticsearch.see());
    }

    @Test
    public void createIndex() {
        TreeMap<String, String> preferences = new TreeMap<>();
        preferences.put("Chiave01", "Valore 01");
        preferences.put("Chiave02", "Valore 02");
        boolean index = superElasticsearch.index("main_index", "preference", "preference_01", preferences);
        log.info("Index Creato? " + index);

        @SuppressWarnings("unchecked") // Suppress warning for casting
        Optional<TreeMap<String, String>> treeMap = (Optional<TreeMap<String, String>>) (Optional<?>) superElasticsearch.get("main_index", "preference", "preference_01", TreeMap.class);
        treeMap.ifPresent(map -> map.keySet().forEach(System.out::println));

    }
}