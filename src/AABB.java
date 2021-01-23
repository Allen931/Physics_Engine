import java.util.ArrayList;

public class AABB {
    Vector min;
    Vector max;

    public AABB(Body polygon, Vector[] vertices) {
        double xMin = -Double.MAX_VALUE;
        double xMax = Double.MAX_VALUE;
        double yMin = -Double.MAX_VALUE;
        double yMax = Double.MAX_VALUE;

        for (Vector vertex : vertices) {
            vertex = polygon.toWorldCoordinates(vertex);
            double x = vertex.getX();
            double y = vertex.getY();

            if (x < xMin) {
                xMin = x;
            }
            if (x > xMax) {
                xMax = x;
            }
            if (y < yMin) {
                yMin = y;
            }
            if (y > yMax) {
                yMax = y;
            }
        }

        min = new Vector(xMin, yMin);
        max = new Vector(xMax, yMax);
    }

    public AABB(Vector position, double radius) {
        min = new Vector(position.getX() - radius, position.getY() - radius);
        max = new Vector(position.getX() + radius, position.getY() + radius);
    }

    public boolean possibleToCollide(AABB aabb) {
        return max.getX() >= aabb.min.getX() || aabb.max.getX() >= min.getX();
    }

}
