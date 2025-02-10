/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.file;

import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

/**
 * Consente di sviluppare un parser velocemente, con quei due o tre strumenti
 * che servono.
 * <p>
 * The content used is already trimmed and de\r'ed.
 *
 * @author Andrea
 */
@Getter
@SuppressWarnings("unused")
public class FileParser {

    private final String content;

    private final ArrayList<String> lines = new ArrayList<>();
    private final ArrayList<String> lines_sorted = new ArrayList<>();

    public FileParser(String content) {
        this.content = content.replaceAll("\r", "");
        for (String line : content.split("\n")) {
            lines.add(line);
            lines_sorted.add(line);
        }
        Collections.sort(lines_sorted);
    }

    public FileParser(File file) {
        this(SuperFileText.getText(file.getPath()));
    }

    public void show() {
        System.out.println(content);
    }

    // -------------------------------------------------------------------------

    public ArrayList<Integer> getIntegerList(String split) {
        ArrayList<Integer> list = new ArrayList<>();

        for (String v : getContent().split(split)) {
            list.add(Integer.parseInt(v));
        }
        return list;
    }

    public String[][] getMatrix(String lineSeparator, String columnSeparator) {
        String[] linesUp = getContent().split(lineSeparator);
        int heigth = linesUp.length;
        int width = linesUp[0].split(columnSeparator).length;
        String[][] matrix = new String[width][heigth];

        for (int y = 0; y < heigth; y++) {
            String line = linesUp[y];
            String[] cells = line.split(columnSeparator);
            for (int x = 0; x < width; x++) {
                matrix[x][y] = cells[x].trim();
            }
        }
        return matrix;
    }

    public void showMatrix(Object[][] matrix) {
        int width = matrix[0].length;
        int heigth = matrix.length;

        for (int y = 0; y < heigth; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(matrix[x][y] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Costruisce una mappa con key valore di una colonna
     *
     * @param lineSeparator   The Line Separator
     * @param columnSeparator The Column Separator
     * @param keyIndex        The key index
     * @return a TreeMap with the keys values.
     */
    public TreeMap<String, String> getTreeMap(String lineSeparator, String columnSeparator, int keyIndex) {
        TreeMap<String, String> output = new TreeMap<>();

        for (String line : content.split(lineSeparator)) {
            String key = line.split(columnSeparator)[keyIndex];
            output.put(key, line);
        }
        return output;
    }

}
