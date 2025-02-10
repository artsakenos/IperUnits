/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.types;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Andrea
 */
@SuppressWarnings("unused")
public class PointND implements Serializable {

    @Setter
    @Getter
    private String className = "";
    public HashMap<String, Double> coordinates = new HashMap<>();

    public PointND addCoordinate(String name, double coord) {
        coordinates.put(name, coord);
        return this;
    }

    public int getDimension() {
        return coordinates.size();
    }

    public double get(String coordinateName) {
        try {
            return coordinates.get(coordinateName);
        } catch (Exception e) {
            // System.out.println("Coordinata non esistente>" + coordinateName);
            return 0;
        }
    }

    public double getEuclideanDistanceFrom(PointND point) {
        double sum = 0;

        for (String cName : coordinates.keySet()) {
            Double d1 = coordinates.get(cName);
            Double d2 = point.get(cName);
            sum += (d1 - d2) * (d1 - d2);
        }
        return Math.sqrt(sum);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        for (String cName : coordinates.keySet())
            output.append(cName).append(":").append(coordinates.get(cName)).append("\n");
        return output.toString();
    }

}
