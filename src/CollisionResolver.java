public class CollisionResolver {

    public static Collision resolve(Body A, Body B) {
        if (A.getShape() instanceof Circle && B.getShape() instanceof Circle) {
            return resolve(A, B, (Circle) A.getShape(), (Circle) B.getShape());
        } else if (A.getShape() instanceof Polygon && B.getShape() instanceof Polygon) {
            return resolve(A, B, (Polygon) A.getShape(), (Polygon) B.getShape());
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

    private static Collision resolve(Body bodyA, Body bodyB, Polygon polygonA, Polygon polygonB) {
        LeastPenetration axisLeastPenetrationReferenceA = findLeastPenetrationPolygon(bodyA, bodyB);
        if (axisLeastPenetrationReferenceA.separation > 0) {
            return null;
        }
        LeastPenetration axisLeastPenetrationReferenceB = findLeastPenetrationPolygon(bodyB, bodyA);
        if (axisLeastPenetrationReferenceB.separation > 0) {
            return null;
        }

        Side referenceSide;
        Body referenceBody, incidentBody;
        Polygon referencePolygon, incidentPolygon;
        // separation is a negative value
        if (axisLeastPenetrationReferenceA.separation > axisLeastPenetrationReferenceB.separation) {
            referenceBody = bodyA;
            incidentBody = bodyB;
            referenceSide = axisLeastPenetrationReferenceA.referenceSide;
        } else {
            referenceBody = bodyB;
            incidentBody = bodyA;
            referenceSide = axisLeastPenetrationReferenceB.referenceSide;
        }
        referencePolygon = (Polygon) referenceBody.getShape();
        incidentPolygon = (Polygon) incidentBody.getShape();






    }

    private static Collision resolve(Body bodyCircle, Body bodyPolygon, Circle circle, Polygon polygon) {
        LeastPenetration axisLeastPenetration = findLeastPenetrationCircle(bodyPolygon, bodyCircle);
        double separation = axisLeastPenetration.separation;
        if (separation > circle.getRadius()) {
            return null;
        }

        Side referenceSide = axisLeastPenetration.referenceSide;
        Vector collisionNormal, contactPoint;

        if (separation < 0) {
            collisionNormal = bodyPolygon.toWorldCoordinates(referenceSide).getNormal().reverse();
            contactPoint = collisionNormal.multiply(circle.getRadius()).add(bodyCircle.position);
        }


    }

//    private static Collision resolve(Body bodyA, Body bodyB, Polygon polygon, Circle circle) {
//
//    }

    /**
     * Get the farthest point from the side
     * @param incidentBody incident polygon
     * @param direction should be in world coordinates
     * @return the farthest point
     */
    public static Vector getSupportPoint(Body incidentBody, Vector direction) {
        Polygon incidentPolygon = (Polygon) incidentBody.getShape();
        double largestProjection = -Double.MAX_VALUE;
        Vector supportPoint = null;
        for (Vector vertex : incidentPolygon.getVertices()) {
            vertex = incidentBody.toWorldCoordinates(vertex);
            double projection = vertex.dotProduct(direction);

            if (projection > largestProjection) {
                largestProjection = projection;
                supportPoint = vertex;
            }
        }
        return supportPoint;
    }

    /**
     * Find the Least Penetration
     * @param referencePolygon reference Polygon
     * @param incidentPolygon collides with referencePolygon
     * @return AxisLeastPenetration with the side and the distance
     */
    public static LeastPenetration findLeastPenetrationPolygon(Body referencePolygon, Body incidentPolygon) {
        Side[] sides = ((Polygon) referencePolygon.getShape()).getSides();

        // if penetration, bestSeparation should be a negative value
        // the maximum of negative values should be a value with minimum absolute value
        double bestSeparation = -Double.MAX_VALUE;
        Side referenceSide = null;
        for (Side side : sides) {
            Vector supportPoint = getSupportPoint(incidentPolygon,
                    referencePolygon.toWorldCoordinates(side).getNormal().reverse());
            double separation = referencePolygon.toWorldCoordinates(side).distanceToSide(supportPoint);

            if (separation > bestSeparation) {
                bestSeparation = separation;
                referenceSide = side;
            }
        }
        return new LeastPenetration(referenceSide, bestSeparation);
    }

    public static LeastPenetration findLeastPenetrationCircle(Body referencePolygon, Body incidentCircle) {
        Side[] sides = ((Polygon) referencePolygon.getShape()).getSides();
        Vector center = referencePolygon.toBodyCoordinates(incidentCircle.position);

        double bestSeparation = -Double.MAX_VALUE;
        Side referenceSide = null;

        for (Side side : sides) {
            double separation = side.distanceToSide(center);

            if (separation > bestSeparation) {
                bestSeparation = separation;
                referenceSide = side;
            }
        }
        return new LeastPenetration(referenceSide, bestSeparation);
    }

    public static class LeastPenetration {
        // in body coordinates
        Side referenceSide;
        // penetration = -separation
        double separation;

        public LeastPenetration(Side referenceSide, double separation) {
            this.referenceSide = referenceSide;
            this.separation = separation;
        }

        public double getPenetration() {
            return -separation;
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
    private static Side findIncidentSide(Side referenceSide, Body referenceBody, Body incidentBody) {
        Polygon incidentPolygon = (Polygon) incidentBody.getShape();
        referenceSide = referenceBody.toWorldCoordinates(referenceSide);
        Vector referenceSideNormal = referenceSide.getNormal();

        double smallestDotProductOfNormals = Double.MAX_VALUE;
        Side incidentSide = null;

        for (Side side : incidentPolygon.getSides()) {
            double dotProductOfNormals =
                    incidentBody.toWorldCoordinates(side.getNormal()).dotProduct(referenceSideNormal);
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
    private static Side clip(Side incidentSide, Vector vertexOnReferenceSide, Vector directionPointToVertex) {
        double distanceFromVertexToA =
                directionPointToVertex.dotProduct(incidentSide.getA().subtract(vertexOnReferenceSide));
        double distanceFromVertexToB =
                directionPointToVertex.dotProduct(incidentSide.getB().subtract(vertexOnReferenceSide));

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
