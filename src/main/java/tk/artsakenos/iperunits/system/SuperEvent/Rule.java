/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.system.SuperEvent;

import lombok.Getter;

import java.util.HashMap;

/**
 * @author Andrea
 */
public class Rule {

    @Getter
    private final String name;
    private final Condition[] condition;

    @Getter
    private final String action;
    private final DateConstraint dateConstraint;
    private int times;
    public static final String CONDITION_SEPARATOR_AND = "<&& />";

    /**
     * Un trigger quando si presenta l'occasione giusta, ovvero quando le rules
     * vengono tutte verificate, quando il dateConstraint viene verificato
     * quando il numero di esecuzioni (times) è >0 o =-1. In questo caso si
     * restituisce un comando rappresentato da una stringa di testo
     *
     * @param name           Il nome del trigger
     * @param condition      Le condizioni da soddisfare
     * @param action         L'azione
     * @param dateConstraint Il dateConstraint
     * @param times          Il numero di volte che il comando può ancora essere eseguito
     *                       (-1 se per indefinitamente)
     */
    public Rule(String name, Condition[] condition, String action, DateConstraint dateConstraint, int times) {
        this.name = name;
        this.condition = condition;
        this.action = action;
        this.dateConstraint = dateConstraint;
        this.times = times;
    }

    /**
     * Controlla che le regole vengano verificate
     *
     * @return true se le regole vengono verificate positivamente
     */
    public boolean check(HashMap<String, String> variables, String changedVariable) {
        // Controlliamo che il trigger sia pertinente
        if (condition == null) {
            return true;
        }

        if (dateConstraint != null) {
            if (!dateConstraint.isAccepted()) {
                return false;
            }
        }

        boolean pertinent = false;
        for (Condition r : condition) {
            if (r.getVariable().equals(changedVariable)) {
                pertinent = true;
            }
            if (r.getVariable().equals(Condition.WILDCARD)) {
                pertinent = true;
            }
        }

        if (!pertinent) {
            return false;
        }

        // Analizziamo il trigger
        for (Condition r : condition) {
            if (!check(r, variables)) {
                return false;
            }
        }
        if (times != -1) {
            if (times == 0) {
                return false;
            } else {
                --times;
            }
        }
        return true;
    }

    /**
     * Controlla che la regola venga verificata
     *
     * @param rule La regola
     * @return true se la regola viene verificata positivamente
     */
    private boolean check(Condition rule, HashMap<String, String> variables) {

        for (String variable : variables.keySet()) {
            // if (!variable.equals(changedVariable)) continue;
            String value = variables.get(variable);
            if (rule.check(variable, value)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        if (condition != null) {
            for (Condition r : condition) {
                output.append(r.toString()).append(" ");
            }
        }
        output = new StringBuilder("Rule:[" + name + "]; Conditions:[" + output.toString().trim() + "]; Action:[" + action + "]; "
                                   + "DateConstraint:[" + dateConstraint + "]; Times:[" + times + "];");

        return output.toString();
    }
}
