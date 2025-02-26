/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.types;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Andrea
 */
@SuppressWarnings("unused")
public class MapUtils {

    // -------------------------------------------------------------------------
    public static Map<String, Integer> sortHMByIntegerValue(Map<String, Integer> unsortedMap, final boolean ascending, int limit) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortedMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> ascending ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<String, Integer> collect = list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));

        // Remove elements di troppo
        for (Iterator<String> iterator = collect.keySet().iterator(); iterator.hasNext(); ) {
            iterator.next();
            if (--limit < 0) {
                iterator.remove();
            }
        }

        return collect;
    }

    public static Map<String, Double> sortHMByDoubleValue(Map<String, Double> unsortedMap, final boolean ascending, int limit) {
        List<Map.Entry<String, Double>> list = new LinkedList<>(unsortedMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> ascending ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<String, Double> collect = list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));

        // Remove elements di troppo
        for (Iterator<String> iterator = collect.keySet().iterator(); iterator.hasNext(); ) {
            iterator.next();
            if (--limit < 0) {
                iterator.remove();
            }
        }

        return collect;
    }

    // -------------------------------------------------------------------------

    /**
     * Stampa una mappa
     *
     * @param map    La mappa da stampare.
     * @param labels se non null, contiene le label relative alle categories,
     *               che possono essere anche dei codici anonimi.
     */
    public static String printMap(Map<?, ?> map, HashMap<String, String> labels) {
        StringBuilder output = new StringBuilder();
        output.append("----------------- Printing Map ------------------\n");
        for (Object key : map.keySet()) {
            String label = "";
            if (labels != null) {
                label = " (" + labels.get(key.toString()) + ")";
            }
            output.append(key).append(label).append(" -> ").append(map.get(key)).append("\n");
        }
        output.append("-------------------------------------------------\n\n");
        System.out.println(output);
        return output.toString();
    }

}
