import java.util.Arrays;

public class Polygon implements Shape{
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
        for (int i = 0; i < vertices.length - 1; i++) {
            sides[i] = new Side(vertices[i], vertices[i + 1]);
        }
        sides[vertices.length - 1] = new Side(vertices[vertices.length - 1], vertices[0]);
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
}
