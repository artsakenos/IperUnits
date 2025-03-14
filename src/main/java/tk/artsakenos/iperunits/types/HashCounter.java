/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.types;

import lombok.Getter;

import java.util.*;

/**
 * Questa classe consente di contare degli oggetti
 *
 * @param <E> Il tipo da conteggiare.
 * @author Andrea 2015.01.01
 * @version 2019.01.01
 */
@SuppressWarnings({"unused", "WeakerAccess", "unchecked", "rawtypes"})
public class HashCounter<E> {

    @Getter
    private long totalItemCount = 0;
    private final HashMap<E, Long> itemList = new HashMap<>();

    //--------------------------------------------------------------------------

    /**
     * Aggiunge l'elemento item o ne aumenta il conteggio (quindi parte da 1)
     *
     * @param item l'elemento
     */
    public void add(E item) {
        add(item, 1);
    }

    /**
     * Aggiunge l'elemento item o ne aumenta il conteggio (quindi parte da 1)
     *
     * @param item  l'elemento
     * @param delta Il delta eventuale
     */
    public void add(E item, long delta) {
        Long i = itemList.get(item);
        if (i == null) {
            i = 0L;
        }
        i += delta;
        totalItemCount += delta;
        itemList.put(item, i);
    }

    /**
     * Set the number of eleent for the item
     *
     * @param item  the Item
     * @param value the Value
     */
    public void set(E item, long value) {
        if (itemList.containsKey(item)) {
            totalItemCount -= itemList.get(item);
        }
        totalItemCount += value;
        itemList.put(item, value);
    }

    /**
     * Rimuove l'elemento item
     *
     * @param item l'elemento da rimuovere
     */
    public void remove(E item) {
        long t = itemList.remove(item);
        totalItemCount -= t;
    }

    /**
     * Resetta il conteggio degli item, eliminandoli
     */
    public void clear() {
        itemList.clear();
        totalItemCount = 0;
    }
    //--------------------------------------------------------------------------

    public Set<E> keySet() {
        return itemList.keySet();
    }

    public ArrayList<E> keySetSorted(boolean desc) {
        ArrayList list = new ArrayList(itemList.keySet());
        Collections.sort(list);
        if (desc) {
            Collections.reverse(list);
        }
        return list;
    }

    /**
     * Restituisce il conteggio associato ad un dato item, null se l'item non
     * esiste.
     *
     * @param item l'elemento da contare
     * @return il conteggio
     */
    public long getItemCount(E item) {
        return itemList.get(item);
    }

    public double getItemPercentage(E item) {
        return itemList.get(item) * 100 / (double) totalItemCount;
    }

    //--------------------------------------------------------------------------

    /**
     * Rappresenta gli item nel formato [item=conteggio, ...]
     *
     * @return una stringa nel formato [item=conteggio, ...]
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (E item : itemList.keySet()) {
            output.append(",").append(item).append("=").append(itemList.get(item));
        }
        if (!output.isEmpty()) {
            output = new StringBuilder("[" + output.substring(1) + "]");
        }
        return output.toString();
    }

    /**
     * Rappresenta gli item nel formato [item=conteggio, ...] Ma restituisce la
     * percentuale stavolta, in base al totalNumber passato
     *
     * @return una stringa nel formato [item=conteggio, ...]
     */
    public String toStringPercentage() {
        StringBuilder output = new StringBuilder();
        for (E item : itemList.keySet()) {
            output.append(",").append(item).append("=").append(fixDouble(".3", getItemPercentage(item)));
        }
        output = new StringBuilder("[" + output.substring(1) + "]");
        return output.toString();
    }

    public String toCsv() {
        StringBuilder output = new StringBuilder();
        for (E key : itemList.keySet()) {
            String line = key + "\t" + itemList.get(key) + "\t" + getItemPercentage(key) + "\n";
            output.append(line);
        }
        return output.toString();
    }

    public String toCsvSortedKey() {
        StringBuilder output = new StringBuilder();
        ArrayList<E> sortedKeys = new ArrayList<>(itemList.keySet());
        sortedKeys.stream().sorted().forEach(key -> {
            String line = key + "\t" + itemList.get(key) + "\t" + getItemPercentage(key) + "\n";
            output.append(line);
        });
        return output.toString();
    }

    /**
     * Mostra gli item ordinati DESC che abbiano un conteggio superiore a limit.
     *
     * @param limit limit
     * @return a string
     */
    public String toStringOrderedDescLimit(long limit) {
        StringBuilder output = new StringBuilder();
        for (E item : keySetSortedByValue()) {
            long val = itemList.get(item);
            if (val <= limit) {
                continue;
            }
            output.append(",").append(item).append("=").append(itemList.get(item));
        }
        output = new StringBuilder("[" + output.substring(1) + "]");
        return output.toString();
    }

    //--------------------------------------------------------------------------
    private class ValueComparator implements Comparator {

        private final HashMap<E, Long> base;

        public ValueComparator(HashMap<E, Long> base) {
            this.base = base;
        }

        @SuppressWarnings({"SuspiciousMethodCalls", "element-type-mismatch"})
        @Override
        public int compare(Object a, Object b) {
            if (base.get(a) < base.get(b)) {
                return 2;
            } else if (Objects.equals(base.get(a), base.get(b))) {
                return 0;
            } else {
                return -2;
            }
        }
    }

    public Set<E> keySetSortedByValue() {
        ValueComparator bvc = new ValueComparator(itemList);
        TreeMap<E, Long> sorted_map = new TreeMap<>(bvc);
        sorted_map.putAll(itemList);
        return sorted_map.keySet();
    }

    public static String fixDouble(String format, double d) {
        return String.format(Locale.ENGLISH, "%" + format + "f", d);
    }

}
