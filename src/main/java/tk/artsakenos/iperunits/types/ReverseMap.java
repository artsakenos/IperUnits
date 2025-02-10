/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * Questa mappa contiene i valori invertiti Value/Key Evita la ridondanza nella
 * lista dei value
 *
 * @param <KEY_TYPE>
 * @param <VALUE_TYPE>
 * @author Andrea
 * @author <p style="color:red; font-family:verdana;">
 * <a href="mailto:a.addis@gmail.com">Andrea Addis</a> - &copy;<a
 * href="http://infodev.wordpress.com">Information Devices </a> </p>
 * @version 2005.07.12
 */
@SuppressWarnings("unused")
public class ReverseMap<KEY_TYPE, VALUE_TYPE> {

    private final HashMap<KEY_TYPE, ArrayList<VALUE_TYPE>> mks = new HashMap<>();

    public void put(KEY_TYPE key, VALUE_TYPE value) {
        put(key, value, true);
    }

    public void put(KEY_TYPE key, VALUE_TYPE value, boolean avoidRedundance) {
        ArrayList<VALUE_TYPE> oldValue;

        if (mks.containsKey(key)) {
            oldValue = mks.get(key);
            if (avoidRedundance && oldValue.contains(value)) {
                return;
            }
        } else {
            oldValue = new ArrayList<>();
        }
        oldValue.add(value);
        mks.put(key, oldValue);
    }

    public ArrayList<VALUE_TYPE> get(KEY_TYPE key) {
        return mks.get(key);
    }

    public Set<KEY_TYPE> keySet() {
        return mks.keySet();
    }

    public Set<KEY_TYPE> keySetSorted() {
        return new TreeSet<>(mks.keySet());
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (KEY_TYPE rr : keySet()) {
            output.append(rr).append("->").append(get(rr)).append("\n");
        }
        return output.toString().trim();
    }

}
