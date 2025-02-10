/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.system.SuperEvent;

import tk.artsakenos.iperunits.string.SuperDate;
import tk.artsakenos.iperunits.string.SuperString;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * This class allows to define Date/Time constraints.
 *
 * An example: of use:
 * <pre>
 * dc.addConstraint("START 2009-08-13 20:09:31");
 * dc.addConstraint("HOUR 5");
 * dc.addConstraint("HOUR 8");
 * dc.addConstraint("HOUR 11");
 * dc.addConstraint("YEAR 2010");
 * dc.addConstraint("YEAR 2009");
 * System.out.println(dc.isAccepted());
 * System.out.println(dc);
 * System.out.println(getNowConstraints());
 * System.out.println("-------------------------------------------------");
 * dc.setConstraints(" HOUR     5,HOUR 11 ,YEAR  2010,  START 2009-08-13 00:00:00");
 * System.out.println(dc);
 * </pre>
 *
 * @author Andrea
 */
public class DateConstraint {

    public static final int DC_SECOND = 100;
    public static final int DC_MINUTE = 200;
    public static final int DC_HOUR = 300;
    public static final int DC_HOUR_OF_DAY = 400;
    public static final int DC_DAY_OF_WEEK = 500;
    public static final int DC_DAY_OF_MONTH = 600;
    // public static final int DC_DAY_OF_YEAR = 700; No! Pu� sfuorare.
    public static final int DC_MONTH = 800;
    public static final int DC_YEAR = 0;
    private Date start = null;
    private Date end = null;
    private final HashMap<String, Integer> DC_StringToInt = new HashMap<String, Integer>();
    private final ArrayList<Integer> constraints = new ArrayList<Integer>();

    public DateConstraint() {
        DC_StringToInt.put("SECOND", DC_SECOND);
        DC_StringToInt.put("MINUTE", DC_MINUTE);
        DC_StringToInt.put("HOUR", DC_HOUR);
        DC_StringToInt.put("HOUR_OF_DAY", DC_HOUR_OF_DAY);
        DC_StringToInt.put("DAY_OF_WEEK", DC_DAY_OF_WEEK);
        DC_StringToInt.put("DAY_OF_MONTH", DC_DAY_OF_MONTH);
        // DC_StringToInt.put("DAY_OF_YEAR", DC_DAY_OF_YEAR);
        DC_StringToInt.put("MONTH", DC_MONTH);
        DC_StringToInt.put("YEAR", DC_YEAR);
    }

    /**
     * Istanzia DateConstraints e imposta subito i costraint partendo dal
     * parametro
     *
     * @param date_constraints I constraints impostati come stringa
     */
    public DateConstraint(String date_constraints) {
        this();
        setConstraints(date_constraints);
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    public boolean isAccepted() {
        return isAccepted(Calendar.getInstance().getTime());
    }

    public boolean isAccepted(Date now) {
        if (start != null) {
            if (now.before(start)) {
                return false;
            }
        }
        if (end != null) {
            if (now.after(end)) {
                return false;
            }
        }

        Calendar cNow = Calendar.getInstance();
        cNow.setTime(now);
        ArrayList<Integer> nowConstraints = getDateConstraint(cNow);

        ///-{ Facciamo una lista delle categorie che passano
        ArrayList<Integer> categoryConstraints = new ArrayList<>();
        for (int constraint : constraints) {
            if (nowConstraints.contains(constraint)) {
                categoryConstraints.add(constraint / 100 * 100);
            }
        }

        for (int constraint : constraints) {
            if (!nowConstraints.contains(constraint) && !categoryConstraints.contains(constraint / 100 * 100)) {
                return false;
            }
        }

        return true;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    private ArrayList<Integer> getDateConstraint(Calendar c) {
        ArrayList<Integer> dc = new ArrayList<>();
        dc.add(c.get(Calendar.SECOND) + DC_SECOND);
        dc.add(c.get(Calendar.MINUTE) + DC_MINUTE);
        dc.add(c.get(Calendar.HOUR) + DC_HOUR);
        dc.add(c.get(Calendar.HOUR_OF_DAY) + DC_HOUR_OF_DAY);
        dc.add(c.get(Calendar.DAY_OF_WEEK) + DC_DAY_OF_WEEK);
        dc.add(c.get(Calendar.DAY_OF_MONTH) + DC_DAY_OF_MONTH);
        // dc.add(c.get(Calendar.DAY_OF_YEAR) + DC_DAY_OF_YEAR);
        dc.add(c.get(Calendar.MONTH) + DC_MONTH);
        dc.add(c.get(Calendar.YEAR) + DC_YEAR);
        return dc;
    }

    private String DateConstraintToString(int constraint) {
        if (constraint > 1000) {
            return "YEAR " + constraint;
        }
        int units = constraint % 100;
        for (String s : DC_StringToInt.keySet()) {
            int i = DC_StringToInt.get(s);
            if ((i + units) == constraint) {
                return s + " " + units;
            }
        }
        return "";
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    /**
     * @param start the start to set
     */
    public void setStart(Date start) {
        this.start = start;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(Date end) {
        this.end = end;
    }

    /**
     * Aggiunge un time constraint.
     *
     * @param constraintType Il tipo del constraint preso da DC_
     *
     * @param constraint Il valore del costraint (in ore, minuti, etc...)
     */
    public void addConstraint(int constraintType, int constraint) {
        constraints.add(constraintType + constraint);
    }

    /**
     * Consente di inserire un constraint in formato stringa. La stringa deve
     * essere del tipo: [{constraintType} constraintNumber] dove <br>
     * {constraintType} == MINUTE, HOUR, HOUR_OF_DAY, DAY_OF_WEEK, DAY_OF_MONTH,
     * DAY_OF_YEAR, MONTH, YEAR. <br> anche [START|END] yyyy-MM-dd HH:mm:ss
     * <br>
     * <pre>
     * Esempio: addConstraint("HOUR_OF_DAY 22");
     *          addConstraint("START 1980-12-30 22:12:00");
     * </pre>
     *
     * @param constraint Il constraint espresso come sopra
     */
    public void addConstraint(String constraint) {
        String sKey = SuperString.getSubWords(constraint, 0, 0);
        String sValue = SuperString.getSubWords(constraint, 1, -1);

        if (sKey.equals("START")) {
            setStart(SuperDate.StringDatabaseToDate(sValue));
            return;
        }
        if (sKey.equals("END")) {
            setEnd(SuperDate.StringDatabaseToDate(sValue));
            return;
        }

        int value = DC_StringToInt.get(sKey);
        value += Integer.parseInt(sValue);
        constraints.add(value);
    }

    /**
     * Consente di impostare i constraint da una stringa che contiene i
     * constraint in formato stringa separati da una ",".
     *
     * @param constraints I constraint in formato stringa separati da una ",".
     */
    public final void setConstraints(String constraints) {
        this.constraints.clear();
        if (constraints == null) {
            return;
        }
        if (constraints.trim().isEmpty()) {
            return;
        }
        start = null;
        end = null;
        for (String s : constraints.split(",")) {
            addConstraint(s.trim());
        }
    }

    public static String getNowConstraints() {
        DateConstraint dc = new DateConstraint();
        Calendar cal = Calendar.getInstance();

        dc.addConstraint(DC_SECOND, cal.get(Calendar.SECOND));
        dc.addConstraint(DC_MINUTE, cal.get(Calendar.MINUTE));
        dc.addConstraint(DC_HOUR, cal.get(Calendar.HOUR));
        dc.addConstraint(DC_HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
        dc.addConstraint(DC_DAY_OF_WEEK, cal.get(Calendar.DAY_OF_WEEK));
        dc.addConstraint(DC_DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
        dc.addConstraint(DC_MONTH, cal.get(Calendar.MONTH));
        dc.addConstraint(DC_YEAR, cal.get(Calendar.YEAR));
        return dc.toString();
    }

    @Override
    public String toString() {
        if (constraints.isEmpty()) {
            return "";
        }

        String output = "";
        for (Integer i : constraints) {
            output += "," + DateConstraintToString(i);
        }

        if (start != null) {
            output += ",START " + SuperDate.DateToStringDatabase(start);
        }
        if (end != null) {
            output += ",END " + SuperDate.DateToStringDatabase(end);
        }

        output = output.substring(1);
        return output;
    }

}
