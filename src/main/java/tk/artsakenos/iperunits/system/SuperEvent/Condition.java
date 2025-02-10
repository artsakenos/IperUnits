/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.system.SuperEvent;

/**
 * TODO: Il confronto viene fatto convertendo in numero, altrimenti confronto
 * tra stringhe!
 *
 * @author Andrea
 *
 */
public class Condition {

    public static final String WILDCARD = "*";
    public static final String OP_EQUALS = "==";
    public static final String OP_MAIOR = ">>";
    public static final String OP_MAIOR_EQUALS = ">=";
    public static final String OP_MINOR = "<<";
    public static final String OP_MINOR_EQUALS = "<=";
    public static final String OP_DIFFERENT = "<>";
    public static final String OP_STRING_STARTSWITH = "SW";
    public static final String OP_STRING_CONTAINS = "SC";
    //----------------------------------------------------------------------
    private String variable = "";
    private String operator = "";
    private String value = "";

    public Condition(String variable, String operator, String value) {
        this.variable = variable;
        this.operator = operator;
        this.value = value;
    }

    public boolean check(String setVariable, String setValue) {
        if (variable.equals(setVariable) || variable.equals(WILDCARD)) {

            if (getOperator().equals(OP_EQUALS)) {
                if (setValue.equals(value)) {
                    return true;
                }
            }
            if (getOperator().equals(OP_DIFFERENT)) {
                if (!setValue.equals(value)) {
                    return true;
                }
            }
            if (getOperator().equals(OP_MAIOR)) {
                double dVariable = Double.parseDouble(setValue);
                double dValue = Double.parseDouble(value);
                if (dVariable > dValue) {
                    return true;
                }
            }
            if (getOperator().equals(OP_MAIOR_EQUALS)) {
                double dVariable = Double.parseDouble(setValue);
                double dValue = Double.parseDouble(value);
                if (dVariable >= dValue) {
                    return true;
                }
            }
            if (getOperator().equals(OP_MINOR)) {
                double dVariable = Double.parseDouble(setValue);
                double dValue = Double.parseDouble(value);
                if (dVariable < dValue) {
                    return true;
                }
            }
            if (getOperator().equals(OP_MINOR_EQUALS)) {
                double dVariable = Double.parseDouble(setValue);
                double dValue = Double.parseDouble(value);
                if (dVariable <= dValue) {
                    return true;
                }
            }
            if (getOperator().equals(OP_STRING_STARTSWITH)) {
                if (setValue.startsWith(value)) {
                    return true;
                }
            }
            if (getOperator().equals(OP_STRING_CONTAINS)) {
                if (setValue.contains(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return the variable
     */
    public String getVariable() {
        return variable;
    }

    /**
     * @return the operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return variable + " " + operator + " " + value;
    }
}
