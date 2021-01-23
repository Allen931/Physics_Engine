import java.io.Serializable;
import java.util.Arrays;

public class Polygon implements Shape, Serializable {
    private final Vector[] vertices;
    private final Side[] sides;
    private double area;

    public Polygon(Vector[] vertices) {
        this.vertices = vertices;
        sides = new Side[vertices.length];
        area = -1;
        sortVertices();
        initializeSides();
    }

    private void sortVertices() {
        Arrays.sort(vertices);
    }

    private void initializeSides() {
        for (int i = 0; i < vertices.length; i++) {
            sides[i] = new Side(vertices[i], vertices[(i + 1) % vertices.length]);
        }
    }

    public Vector[] getVertices() {
        return vertices;
    }

    public Side[] getSides() {
        return sides;
    }

    @Override
    public double getArea() {
        if (area > 0) {
            return area;
        } else {
            double area = 0;
            for (int i = 1; i < sides.length - 1; i++) {
                area += vertices[i].subtract(vertices[0]).crossProduct2D(vertices[i + 1].subtract(vertices[0]));
            }
            area /= 2;
            this.area = area;
            return area;
        }
    }

    /**
     * @reference  https://stackoverflow.com/questions/31106438/calculate-moment-of-inertia-given-an-arbitrary-convex-2d-polygon
     * @return factor of moment of inertia
     */
    @Override
    public double getMomentOfInertiaFactor() {
        double area = 0;
        Vector center = new Vector(0, 0);
        double factor = 0;

        for (int i = 0; i < vertices.length; i++) {
            Vector a = vertices[i];
            Vector b = vertices[(i + 1) % vertices.length];

            double areaStep = a.crossProduct2D(b) / 2;
            Vector centerStep = a.add(b).divideBy(3);
            double factorStep = areaStep * (a.dotProduct(a) + b.dotProduct(b) + a.dotProduct(b)) / 6;

            center = (center.multiply(area).add(centerStep.multiply(areaStep))).divideBy(area + areaStep);
            area += areaStep;
            factor += factorStep;
        }

        factor -= area * center.dotProduct(center);
        return factor;
    }
}
