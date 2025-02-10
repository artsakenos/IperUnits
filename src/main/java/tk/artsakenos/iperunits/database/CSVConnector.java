/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.database;

import tk.artsakenos.iperunits.file.SuperFileText;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Consente di manipolare files CSV, TSV.
 * <p>
 * C'è un metodo per la manipolazione di una riga corrente!
 *
 * @author Andrea
 */
@SuppressWarnings("unused")
public class CSVConnector implements Serializable {

    private final LinkedHashSet<Object[]> table = new LinkedHashSet<Object[]>();
    private String[] header = null;
    public String lineSeparator = "\n";
    public String columnSeparator = "\t";
    private Object[] currentRow = null;

    //--------------------------------------------------------------------------

    /**
     * Aggiunge una riga composta dalle celle.
     *
     * @param cell Una lista di celle
     */
    public void add(Object... cell) {
        table.add(cell);
    }

    /**
     * Imposta un header
     *
     * @param columnName Una lista di nomi di colonna
     */
    public void setHeader(String... columnName) {
        header = columnName;
    }

    /**
     * Restituisce le righe che partono con gli elementi della key (che può
     * essere composta da diverse celle).
     *
     * @param key la chiave composta dai primi object della riga
     * @return le righe che corrispondono alle key
     */
    public LinkedHashSet<Object[]> get(Object... key) {
        final LinkedHashSet<Object[]> result = new LinkedHashSet<>();
        for (Object[] k : table) {
            if (Arrays.equals(Arrays.copyOfRange(k, 0, key.length), key)) {
                result.add(k);
            }
        }
        return result;
    }

    // -------------------------------------------------------------------------
    public String toStringRow(Object[] row) {
        StringBuilder output = new StringBuilder();
        for (Object o : row) {
            output.append(columnSeparator).append(o == null ? "" : o);
        }
        String output_string = output.toString();
        return output_string.isEmpty() ? "" : output_string.substring(1);
    }

    public String toString(LinkedHashSet<Object[]> table) {
        StringBuilder output = new StringBuilder();
        for (Object[] oo : table) {
            output.append(toStringRow(oo)).append(lineSeparator);
        }
        return output.toString();
    }

    @Override
    public String toString() {
        String header_s = (header != null) ? toStringRow(header) + lineSeparator : "";
        return header_s + toString(table);
    }

    /**
     * Salva il file CSV (sovrascrive un eventuale file esistente).
     *
     * @param filePath The file path
     */
    public void save(String filePath) {
        SuperFileText.setText(filePath, toString());
    }

    public void load(String filePath, boolean hasHeader) {
        String csv_content = SuperFileText.getText(filePath);
        String[] lines = csv_content.split(lineSeparator);
        int firstLine = 0;
        if (hasHeader) {
            setHeader(lines[0].split(columnSeparator));
            firstLine++;
        }
        for (int n = firstLine; n < lines.length; ++n) {
            add((Object[]) lines[n].split(columnSeparator));
        }
    }

    /**
     * Modifica i valori della currentrow; Un header deve essere settato in
     * questo caso.
     *
     * @param column Il nome della colonna
     * @param value  Il valore
     */
    public void set(String column, Object value) {
        if (header == null) {
            System.out.println("CSVConnector: Warning, no header during set('" + column + "', ...)");
            return;
        }
        if (currentRow == null) {
            currentRow = new Object[header.length];
        }
        int index = getIndex(column);
        if (index < 0) {
            System.out.println("CSVConnector: Warning, no column '" + column + "' during set(...)");
            return;
        }

        currentRow[index] = value;
    }

    public int getIndex(String column) {
        if (header == null) {
            return -1;
        }
        for (int i = 0; i < header.length; i++) {
            if (header[i].equals(column)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Aggiunge la current row, impostata tramite i metodi set(...).
     */
    public void add() {
        if (currentRow == null) {
            System.out.println("CSVConnector: Warning, no currentRow during add()");
            return;
        }
        table.add(currentRow);
        currentRow = null;
    }

    //--------------------------------------------------------------------------
    public static void test(String[] args) {
        CSVConnector csv = new CSVConnector();

        csv.add("gino", 2, "pippo");
        csv.add("gino", 2, "pappo");
        csv.add("gino", 3, "lello");
        csv.add("pino", 3, "lallo");

        System.out.println(csv.toString());

        LinkedHashSet<Object[]> get = csv.get("gino", 2);
        System.out.println(csv.toString(get));
    }
}
