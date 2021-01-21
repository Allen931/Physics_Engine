import java.util.Arrays;
import java.util.HashMap;

public class Polygon implements Shape{
    Vector[] vertices;
    Plane[] planes;

    public Polygon(Vector[] vertices) {
        this.vertices = vertices;
        planes = new Plane[vertices.length];
        sortVertices();
        initializeNormals();
    }

    private void sortVertices() {
        Arrays.sort(vertices);
    }

    private void initializeNormals() {
        for (int i = 0; i < vertices.length - 1; i++) {
            Vector normal = vertices[i + 1].subtract(vertices[i]).unitNormalVector();
            planes[i] = new Plane(vertices[i], vertices[i + 1], normal);
        }
        Vector normal = vertices[0].subtract(vertices[vertices.length - 1]).unitNormalVector();
        planes[vertices.length - 1] = new Plane(vertices[vertices.length - 1], vertices[0], normal);
    }

    public Vector getSupportPoint(Vector direction) {
        double largestProjection = -Double.MAX_VALUE;
        Vector supportPoint = null;
        for (Vector vertex : vertices) {
            double projection = vertex.dot_product(direction);

            if (projection > largestProjection) {
                largestProjection = projection;
                supportPoint = vertex;
            }
        }
        return supportPoint;
    }

    public AxisLeastPenetration FindAxisLeastPenetration(Polygon target) {
        double bestDistance = -Double.MAX_VALUE;
        Plane bestPlane = null;
        for (Plane plane : planes) {
            Vector supportPoint = target.getSupportPoint(plane.normal.negative());
            double distance = plane.distanceToPlane(supportPoint);

            if (distance > bestDistance) {
                bestDistance = distance;
                bestPlane = plane;
            }
        }
        return new AxisLeastPenetration(bestPlane, bestDistance);
    }

    public static class AxisLeastPenetration {
        Plane plane;
        double distance;

        public AxisLeastPenetration(Plane plane, double distance) {
            this.plane = plane;
            this.distance = distance;
        }
    }


}
