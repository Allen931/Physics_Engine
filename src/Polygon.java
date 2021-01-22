import java.util.Arrays;

public class Polygon implements Shape{
    private final Vector[] vertices;
    private final Side[] sides;

    public Polygon(Vector[] vertices) {
        this.vertices = vertices;
        sides = new Side[vertices.length];
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
}
