/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.system;

import tk.artsakenos.iperunits.types.Fifo;
import java.awt.Point;

/**
 *
 * @author Andrea
 */
public abstract class MouseAirGestures {

    public static final int E_STOP = 0;
    public static final int E_CORNER_00 = 1;
    public static final int E_CORNER_01 = 2;
    public static final int E_CORNER_10 = 4;
    public static final int E_CORNER_11 = 8;
    public static final int E_ALWAYS_LEFT = 1;
    public static final int E_ALWAYS_RIGHT = 2;
    public static final int E_ALWAYS_UP = 4;
    public static final int E_ALWAYS_DOWN = 8;

    public static String nameCorner(int event) {
        switch (event) {
            case 1:
                return "Corner Up Left";
            case 2:
                return "Corner Down Left";
            case 4:
                return "Corner Up Right";
            case 8:
                return "Corner Down Right";
            default:
                return "Stop";
        }
    }

    public static String nameMove(int event) {
        String output = "";
        if ((event & E_ALWAYS_LEFT) == E_ALWAYS_LEFT) {
            output += "To Left; ";
        }
        if ((event & E_ALWAYS_RIGHT) == E_ALWAYS_RIGHT) {
            output += "To Right; ";
        }
        if ((event & E_ALWAYS_UP) == E_ALWAYS_UP) {
            output += "To Up; ";
        }
        if ((event & E_ALWAYS_DOWN) == E_ALWAYS_DOWN) {
            output += "To Down; ";
        }
        return output.trim();
    }
    //--------------------------------------------------------------------------
    private int lastCorner = 0;
    private int lastAlways = 0;
    private final int numPoints = 10;
    private final int numMillisecs = 100;
    private final boolean continuous = true;

    public final int numSeconds() {
        return numPoints * numMillisecs;
    }
    //--------------------------------------------------------------------------
    private Fifo<Point> points = new Fifo<>(numPoints);

    final SuperTimer st = new SuperTimer(this) {
        @Override
        public void doTimer() {
            points.push(Mouse.getPoint());

            ///-{ 1) Check corner position.
            int currentCorner = E_STOP;
            if (Mouse.isMouse00()) {
                currentCorner |= E_CORNER_00;
            }

            if (Mouse.getPoint().equals(new Point(Screen.getWidth() - 1, 0))) {
                currentCorner |= E_CORNER_10;
            }
            if (Mouse.getPoint().equals(new Point(0, Screen.getHeight() - 1))) {
                currentCorner |= E_CORNER_01;
            }
            if (Mouse.getPoint().equals(new Point(Screen.getWidth() - 1, Screen.getHeight() - 1))) {
                currentCorner |= E_CORNER_11;
            }
            if (currentCorner != lastCorner) {
                lastCorner = currentCorner;
                onCorner(lastCorner);
            }

            ///-{ 2) Check up movement
            boolean alwaysLeft = true;
            boolean alwaysRight = true;
            boolean alwaysUp = true;
            boolean alwaysDown = true;

            int currentAlways = 0;

            int wMax = Integer.MAX_VALUE;
            int wMin = Integer.MIN_VALUE;
            int hMax = Integer.MAX_VALUE;
            int hMin = Integer.MIN_VALUE;
            for (Point p : points) {
                if (p.x >= wMax) {
                    alwaysLeft = false;
                } else {
                    wMax = p.x;
                }
                if (p.x <= wMin) {
                    alwaysRight = false;
                } else {
                    wMin = p.x;
                }
                if (p.y >= hMax) {
                    alwaysUp = false;
                } else {
                    hMax = p.y;
                }
                if (p.y <= hMin) {
                    alwaysDown = false;
                } else {
                    hMin = p.y;
                }
            }

            if (alwaysLeft) {
                currentAlways |= E_ALWAYS_LEFT;
            }
            if (alwaysRight) {
                currentAlways |= E_ALWAYS_RIGHT;
            }
            if (alwaysUp) {
                currentAlways |= E_ALWAYS_UP;
            }
            if (alwaysDown) {
                currentAlways |= E_ALWAYS_DOWN;
            }
            if (currentAlways != lastAlways) {
                lastAlways = currentAlways;
                onAlways(lastAlways);
            }

            ///-{ Sout Debug
            // System.out.println(currentCorner + "-" + points);
        }
    };

    //--------------------------------------------------------------------------
    public MouseAirGestures() {
        points.push(Mouse.getPoint());
        points.push(Mouse.getPoint());
    }

    public void start() {
        st.start(numMillisecs);
    }

    public void stop() {
        st.stop();
    }
    //--------------------------------------------------------------------------

    /**
     * Il corner, usare le costanti E_CORNER_XY.
     *
     * @param corner il corner.
     */
    public abstract void onCorner(int corner);

    /**
     * In che direzione sta andando il mouse? Usare le costanti E_ALWAYS_NN.
     *
     * @param always Direzione del mouse.
     */
    public abstract void onAlways(int always);
    //--------------------------------------------------------------------------

}
