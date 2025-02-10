/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.form;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class SuperColor {

    private static final Map<Color, String> MAP;

    static {
        MAP = new HashMap<>();
        Field[] fields = Color.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == Color.class) {
                if (Modifier.isStatic(field.getModifiers())) {
                    try {
                        Color color = (Color) field.get(null);
                        String name = field.getName();
                        MAP.put(color, name.toLowerCase());
                    } catch (IllegalArgumentException | IllegalAccessException ignore) {
                        // e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Restituisce uno dei colori principali dello spettro in base ad un
     * parametro intero.
     *
     * @param index Indice del colore da utilizzare
     * @param dark  Sceglie solo colori scuri
     * @return Restituisce il colore corrispondente all'indice
     */
    public static Color getColor(int index, boolean dark) {
        Color c = null;
        index = index % 7;
        switch (index) {
            case 0:
                c = Color.RED;
                break;
            case 1:
                c = Color.GREEN;
                break;
            case 2:
                c = Color.BLUE;
                break;
            case 3:
                c = Color.ORANGE;
                break;
            case 4:
                c = Color.PINK;
                if (dark) {
                    c = Color.MAGENTA;
                }
                break;
            case 5:
                c = Color.MAGENTA;
                break;
            case 6:
                c = Color.YELLOW;
                if (dark) {
                    c = Color.DARK_GRAY;
                }
                break;
        }
        return c;
    }

    public static Color getColor(int index) {
        return getColor(index, false);
    }

    public static String name(Color color) {
        int best = 3 * 256 + 1;
        String bestName = "unknown";
        for (Map.Entry<Color, String> entry : MAP.entrySet()) {
            Color sample = entry.getKey();
            int difference = Math.abs(color.getRed() - sample.getRed()) + Math.abs(color.getGreen() - sample.getGreen()) + Math.abs(color.getBlue() - sample.getBlue());
            if (difference < best) {
                best = difference;
                bestName = entry.getValue();
            }
        }
        return bestName;
    }

    public static String toRGB(Color color) {
        return "(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
    }

    public static Color getGradient(Color colorA, Color colorB, double range, double position) {
        double pNorm = position / range; // E' unvalore tra 0 e 1 che indica la posizione
        int car = colorA.getRed();
        int cag = colorA.getGreen();
        int cab = colorA.getBlue();
        int cbr = colorB.getRed();
        int cbg = colorB.getGreen();
        int cbb = colorB.getBlue();

        int ncr = (int) ((double) (cbr - car) * pNorm + (double) car);
        int ncg = (int) ((double) (cbg - cag) * pNorm + (double) cag);
        int ncb = (int) ((double) (cbb - cab) * pNorm + (double) cab);
        // int newBlue = (colorA.getBlue()+colorB.getBlue())*;
        return new Color(ncr, ncg, ncb);
    }

    public static void getColorNames() {
        for (int red = 0; red < 256; red += 15) {
            for (int green = 0; green < 256; green += 15) {
                for (int blue = 0; blue < 256; blue += 15) {
                    Color color = new Color(red, green, blue);
                    System.out.println(toRGB(color) + "\t" + name(color));
                }
            }
        }
    }

    public static Color getDifference(Color color01, Color color02) {
        double distanceR = Math.abs(color01.getRed() - color02.getRed());
        double distanceG = Math.abs(color01.getGreen() - color02.getGreen());
        double distanceB = Math.abs(color01.getBlue() - color02.getBlue());
        return new Color((int) distanceR, (int) distanceG, (int) distanceB);
    }

    public static Color getDifferenceGray(Color color01, Color color02) {
        double distanceR = Math.abs(color01.getRed() - color02.getRed());
        double distanceG = Math.abs(color01.getGreen() - color02.getGreen());
        double distanceB = Math.abs(color01.getBlue() - color02.getBlue());
        double distance = (distanceR + distanceG + distanceB) / 3.0;
        return new Color((int) distance, (int) distance, (int) distance);
    }

    public static Color getAverage(Color color01, Color color02) {
        double distanceR = Math.pow(color01.getRed() - color02.getRed(), 2);
        distanceR = Math.sqrt(distanceR);
        double distanceG = Math.pow(color01.getGreen() - color02.getGreen(), 2);
        distanceG = Math.sqrt(distanceG);
        double distanceB = Math.pow(color01.getBlue() - color02.getBlue(), 2);
        distanceB = Math.sqrt(distanceB);
        return new Color((int) distanceR, (int) distanceG, (int) distanceB);
    }

    /**
     * Mostra la differenza tra due tiles in scala di grigi. Si suppone siano
     * della stessa dimensione!
     *
     * @param tile00 La prima tile
     * @param tile01 La seconda tile
     * @return La differenza tra 0 e 256.
     */
    public static double tilesDiff(BufferedImage tile00, BufferedImage tile01) {
        double totalDiff = 0;

        int tileW = tile00.getWidth();
        int tileH = tile00.getHeight();

        for (int x = 0; x < tileW; x++) {
            for (int y = 0; y < tileH; y++) {
                Color differenceGray = getDifferenceGray(new Color(tile00.getRGB(x, y)), new Color(tile01.getRGB(x, y)));
                totalDiff += differenceGray.getRed();
            }
        }
        totalDiff = totalDiff / (double) tileW / (double) tileH;
        return totalDiff;
    }

    /**
     * @param colorStr e.g. "#FFFFFF"
     * @return
     */
    @SuppressWarnings("UseSpecificCatch")
    public static Color hex2Rgb(String colorStr) {
        try {
            return new Color(
                    Integer.valueOf(colorStr.substring(1, 3), 16),
                    Integer.valueOf(colorStr.substring(3, 5), 16),
                    Integer.valueOf(colorStr.substring(5, 7), 16));
        } catch (Exception ignore) {
            return null;
        }
    }
}
