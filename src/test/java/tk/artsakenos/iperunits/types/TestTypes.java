package tk.artsakenos.iperunits.types;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class TestTypes {

    @Test
    void points() {
        PointND pointND = new PointND();
        pointND.addCoordinate("x", 10);
        pointND.addCoordinate("y", 10);

        Point2D point2D = new Point2D(10, 10);
        double euclideanDistanceFrom = pointND.getEuclideanDistanceFrom(
                new PointND()
                        .addCoordinate("x", point2D.getX())
                        .addCoordinate("y", point2D.getY()));

        assertEquals(0, euclideanDistanceFrom, "The Euclidean distance should be 0");
    }

}
