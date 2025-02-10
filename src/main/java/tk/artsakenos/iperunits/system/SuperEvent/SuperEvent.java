/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.system.SuperEvent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Attenzione: Se si creano due istanze di SuperEvent comunque le variabili
 * statiche vengono conservate. Quindi usare una sola istanza per ora.
 *
 * @author addis
 */
public class SuperEvent {

    private final HashMap<String, Rule> rules = new HashMap<String, Rule>();
    private final HashMap<String, String> variables = new HashMap<String, String>();

    /**
     * Appena cambia lo stato di una variabile si fa il check di quelle regole
     * che la contengono.
     *
     * @param variable La chiave da modificare
     * @param value Il nuovo valore
     * @return una lista si actions corrispondenti alle rules triggerate
     */
    public ArrayList<String> set(String variable, String value) {
        variables.put(variable, value);
        ArrayList<String> actions = new ArrayList<>();

        for (String triggerName : rules.keySet()) {
            Rule trigger = rules.get(triggerName);

            try {
                if (trigger.check(variables, variable)) {
                    actions.add(trigger.getAction());
                }
            } catch (NumberFormatException nfe) {
                System.err.println("Number Format Exception in SuperEvent, a causa di " + nfe.getMessage() + "> " + trigger);
            } catch (Exception e) {
                System.err.println("Exception in SuperEvent, a causa di " + e.getMessage() + "> " + trigger);
            }

        }
        return actions;
    }

    public void addRule(Rule rule) {
        rules.put(rule.getName(), rule);
    }

    public void removeRule(String ruleName) {
        rules.remove(ruleName);
    }

    @Override
    public String toString() {
        String output = "";
        for (String t : rules.keySet()) {
            output += rules.get(t) + "\n";
        }
        return output;
    }
}
