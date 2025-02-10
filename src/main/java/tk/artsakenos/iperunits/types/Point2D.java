/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.types;

import lombok.Getter;

/**
 * @author Andrea
 */
@SuppressWarnings("unused")
@Getter
public class Point2D {

    private double x = 0;
    private double y = 0;

    public Point2D() {
    }

    public Point2D(double x, double y) {
        set(x, y);
    }

    public final void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distanceFrom(double x2, double y2) {
        double dist = (x - x2) * (x - x2) + (y - y2) * (y - y2);
        dist = Math.sqrt(dist);
        return dist;
    }

    public double distanceFrom(Point2D p2) {
        return distanceFrom(p2.x, p2.y);
    }

    public void move(double deltaX, double deltaY) {
        x += deltaX;
        y += deltaY;
    }

    @Override
    public String toString() {
        return x + ";" + y;
    }

    /**
     * Convert a String to Point2D from the format: X;Y
     *
     * @param point The Point2D
     */
    public void fromString(String point) {
        x = Double.parseDouble(point.split(";")[0]);
        y = Double.parseDouble(point.split(";")[1]);
    }
}
