/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.types;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * Rappresenta una lista malleabile. Puoi aggiungere oggetti in ogni posizione,
 * la lunghezza della lista dipende dalla posizione dell'ultimo elemento (+1).
 *
 * @author Andrea
 * @version Jan 29, 2020
 */
@SuppressWarnings("unused")
public class MalleableList<E> {

    private int lastIndex = 0;
    private final TreeMap<Integer, E> map = new TreeMap<>();

    public void put(int index, E object) {
        map.put(index, object);
        if (index > lastIndex) {
            lastIndex = index;
        }
    }

    public E[] getArray() {
        E[] arr = (E[]) new Object[size()];
        for (int i = 0; i < size(); ++i) {
            arr[i] = map.get(i);
        }
        return arr;
    }

    public LinkedList<E> getList() {
        return new LinkedList<>(Arrays.asList(getArray()));
    }

    public int size() {
        return lastIndex + 1;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (E e : getArray()) {
            output.append(",").append(e);
        }
        if (output.length() > 0) {
            output = new StringBuilder(output.substring(1));
        }
        return output.toString();
    }


}
