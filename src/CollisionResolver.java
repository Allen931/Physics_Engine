import java.util.ArrayList;

public class CollisionResolver {

    public static class Collision {
        Body bodyA, bodyB;
        Vector collisionNormal;
        double penetrationDepth;
        double coefficientOfRestitution;
        ArrayList<Vector> contactPoints = new ArrayList<>();

//        Side referenceSide, incidentSide;

        public Collision(Body A, Body B, Vector collisionNormal, double penetrationDepth) {
            bodyA = A;
            bodyB = B;
            this.collisionNormal = collisionNormal.to_unit();
            this.penetrationDepth = penetrationDepth;
            coefficientOfRestitution = Math.min(A.coefficientOfRestitution, B.coefficientOfRestitution);
        }

        public void addContactPoint(Vector contactPoint) {
            contactPoints.add(contactPoint);
        }
    }

    public static Collision resolve(Body A, Body B) {
        if (A.getShape() instanceof Circle && B.getShape() instanceof Circle) {
            return resolve(A, B, (Circle) A.getShape(), (Circle) B.getShape());
//        } else if (A.shape instanceof Polygon && B.shape instanceof Polygon) {
//            return resolve(A, B, (Polygon) A.shape, (Polygon) B.shape);
//        } else if (A.shape instanceof Circle && B.shape instanceof Polygon) {
//            return resolve(A, B, (Circle) A.shape, (Polygon) B.shape);
//        } else if (A.shape instanceof Polygon && B.shape instanceof Circle) {
//            return resolve(A, B, (Polygon) A.shape, (Circle) B.shape);
        }
        return null;
    }

    private static Collision resolve(Body bodyA, Body bodyB, Circle circleA, Circle circleB) {
        double penetration_depth = Math.pow(circleA.getRadius() + circleB.getRadius(), 2)
                - bodyA.position.squareOfDistance(bodyB.position);
        if (penetration_depth > 0) {
            Vector normal = bodyB.position.subtract(bodyA.position);
            penetration_depth = Math.sqrt(penetration_depth);
            Collision collision = new Collision(bodyA, bodyB, normal, penetration_depth);
            Vector contactPoint = bodyA.toWorldCoordinates(normal.multiply(circleA.getRadius()));
            collision.addContactPoint(contactPoint);
            return collision;
        }
        return null;
    }

    private static Collision resolve(Body body_A, Body body_B, Polygon polygon_A, Polygon polygon_B) {
        return null;
    }

//    private static Collision resolve(Body body_A, Body body_B, Circle circle, Polygon polygon) {
//
//    }
//
//    private static Collision resolve(Body body_A, Body body_B, Polygon polygon, Circle circle) {
//
//    }

    /**
     * Get the farthest point from the side
     * @param target target polygon
     * @param direction should be in world coordinates
     * @return the farthest point
     */
    public static Vector getSupportPoint(Body target, Vector direction) {
        Polygon polygon = (Polygon) target.getShape();
        double largestProjection = -Double.MAX_VALUE;
        Vector supportPoint = null;
        for (Vector vertex : polygon.getVertices()) {
            vertex = target.toWorldCoordinates(vertex);
            double projection = vertex.dot_product(direction);

            if (projection > largestProjection) {
                largestProjection = projection;
                supportPoint = vertex;
            }
        }
        return supportPoint;
    }

    /**
     * Find the Axis Least Penetration
     * @param polygonA reference polygon
     * @param polygonB collides with A
     * @return AxisLeastPenetration with the side and the distance
     */
    public static AxisLeastPenetration findAxisLeastPenetration(Body polygonA, Body polygonB) {
        Side[] sides = ((Polygon) polygonA.getShape()).getSides();

        // if penetration, bestDistance should be a negative value
        // the maximum of negative values should be a value with minimum absolute value
        double bestDistance = -Double.MAX_VALUE;
        // bestSide is a side of A
        Side bestSide = null;
        for (Side side : sides) {
            Vector supportPoint = CollisionResolver.getSupportPoint(polygonB,
                    polygonA.toWorldCoordinates(side).getNormal().negative());
            double distance = polygonA.toWorldCoordinates(side).distanceToSide(supportPoint);

            if (distance > bestDistance) {
                bestDistance = distance;
                bestSide = side;
            }
        }
        return new AxisLeastPenetration(bestSide, bestDistance);
    }

    public static class AxisLeastPenetration {
        Side side;
        // penetration = -separation
        double separation;

        public AxisLeastPenetration(Side side, double separation) {
            this.side = side;
            this.separation = separation;
        }
    }

    /**
     * find the incident side in the incident body
     * which is the side whose normal has the most negative dot product
     * with the normal of the reference side
     * (smallest angle between incident side and reference side)
     * @param referenceSide reference side in reference body coordinates
     * @return incident side which is in incident body coordinates
     */
    public static Side findIncidentSide(Side referenceSide, Body referenceBody, Body incidentBody) {
        Polygon incidentPolygon = (Polygon) incidentBody.getShape();
        referenceSide = referenceBody.toWorldCoordinates(referenceSide);
        Vector referenceSideNormal = referenceSide.getNormal();

        double smallestDotProductOfNormals = Double.MAX_VALUE;
        Side incidentSide = null;

        for (Side side : incidentPolygon.getSides()) {
            double dotProductOfNormals =
                    incidentBody.toWorldCoordinates(side.getNormal()).dot_product(referenceSideNormal);
            if (dotProductOfNormals < smallestDotProductOfNormals) {
                smallestDotProductOfNormals = dotProductOfNormals;
                incidentSide = side;
            }
        }

        return incidentSide;
    }

    /**
     * All parameters should be in world coordinates
     * @return a modified incident side
     */
    public static Side clip(Side incidentSide, Vector vertexOnReferenceSide, Vector directionPointToVertex) {
        double distanceFromVertexToA =
                directionPointToVertex.dot_product(incidentSide.getA().subtract(vertexOnReferenceSide));
        double distanceFromVertexToB =
                directionPointToVertex.dot_product(incidentSide.getB().subtract(vertexOnReferenceSide));

        // if A is negative, B is positive, A - B is negative, and ratio is positive
        // if A is positive, B is negative, A - B is positive, and ratio is positive
        double ratio = distanceFromVertexToA / (distanceFromVertexToA - distanceFromVertexToB);
        Vector clippedVertex =
                incidentSide.getB().subtract(incidentSide.getA()).multiply(ratio).add(incidentSide.getA());

        Side clippedSide = incidentSide.copy();
        if (distanceFromVertexToA > 0) {
            clippedSide = clippedSide.changeA(clippedVertex);
        }
        if (distanceFromVertexToB > 0) {
            clippedSide = clippedSide.changeB(clippedVertex);
        }
        return clippedSide;
    }

}
