import java.util.ArrayList;

public class CollisionDetector {

    public static Collision detect(Body A, Body B) {
        if (A.getShape() instanceof Circle && B.getShape() instanceof Circle) {
            return detect(A, B, (Circle) A.getShape(), (Circle) B.getShape());
        } else if (A.getShape() instanceof Polygon && B.getShape() instanceof Polygon) {
            return detect(A, B, (Polygon) A.getShape(), (Polygon) B.getShape());
        } else if (A.getShape() instanceof Circle && B.getShape() instanceof Polygon) {
            return detect(A, B, (Circle) A.getShape(), (Polygon) B.getShape());
        } else if (A.getShape() instanceof Polygon && B.getShape() instanceof Circle) {
            return detect(A, B, (Polygon) A.getShape(), (Circle) B.getShape());
        }
        return null;
    }

    private static Collision detect(Body bodyA, Body bodyB, Circle circleA, Circle circleB) {
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

    private static Collision detect(Body bodyA, Body bodyB, Polygon polygonA, Polygon polygonB) {
        LeastPenetration leastPenetrationReferenceA = findLeastPenetrationPolygon(bodyA, bodyB);
        if (leastPenetrationReferenceA.separation > 0) {
            return null;
        }
        LeastPenetration leastPenetrationReferenceB = findLeastPenetrationPolygon(bodyB, bodyA);
        if (leastPenetrationReferenceB.separation > 0) {
            return null;
        }

        Side referenceSide;
        Body referenceBody, incidentBody;
        // separation is a negative value
        if (leastPenetrationReferenceA.separation > leastPenetrationReferenceB.separation) {
            referenceBody = bodyA;
            incidentBody = bodyB;
            referenceSide = bodyA.toWorldCoordinates(leastPenetrationReferenceA.referenceSide);
        } else {
            referenceBody = bodyB;
            incidentBody = bodyA;
            referenceSide = bodyB.toWorldCoordinates(leastPenetrationReferenceB.referenceSide);
        }

        Vector collisionNormal = referenceSide.getNormal();

        Side incidentSide = incidentBody.toWorldCoordinates(findIncidentSide(referenceSide, incidentBody));

        // clip the incident side to the range of reference side
        incidentSide = clip(incidentSide, referenceSide.getA(), referenceSide.getDirection().reverse());
        incidentSide = clip(incidentSide, referenceSide.getB(), referenceSide.getDirection());

        ArrayList<Vector> contactPoints = new ArrayList<>();
        double penetration = 0;
        double separation = collisionNormal.dotProduct(incidentSide.getA().subtract(referenceSide.getA()));
        if (separation < 0) {
            contactPoints.add(incidentSide.getA());
            penetration -= separation;
        }

        separation = collisionNormal.dotProduct(incidentSide.getA().subtract(referenceSide.getA()));
        if (separation < 0) {
            contactPoints.add(incidentSide.getB());
            penetration -= separation;
        }

        Collision collision = new Collision(referenceBody, incidentBody,
                collisionNormal, penetration / contactPoints.size());
        collision.addContactPoints(contactPoints);

        return collision;
    }

    private static Collision detect(Body bodyCircle, Body bodyPolygon, Circle circle, Polygon polygon) {
        LeastPenetration leastPenetration = findLeastPenetrationCircle(bodyPolygon, bodyCircle);
        double separation = leastPenetration.separation;
        double radius = circle.getRadius();
        if (separation > radius) {
            return null;
        }

        Side referenceSide = bodyPolygon.toWorldCoordinates(leastPenetration.referenceSide);
        Vector center = bodyCircle.position;
        Vector collisionNormal, contactPoint;

        if (separation < 0) {
            // center is inside the polygon
            collisionNormal = referenceSide.getNormal().reverse();
            contactPoint = collisionNormal.multiply(radius).add(center);
        } else {
            // Check relative position of center to the reference side
            Vector referenceSideDirection = referenceSide.getDirection();
            double distanceCenterToDirectionToA =
                    referenceSideDirection.reverse().dotProduct(center.subtract(referenceSide.getA()));
            double distanceCenterToDirectionToB =
                    referenceSideDirection.dotProduct(center.subtract(referenceSide.getB()));

            if (distanceCenterToDirectionToA > 0) {
                Vector A = referenceSide.getA();
                if (center.squareOfDistance(A) > radius * radius) {
                    return null;
                } else {
                    collisionNormal = A.subtract(center).toUnitVector();
                    contactPoint = A;
                }
            } else if (distanceCenterToDirectionToB > 0) {
                Vector B = referenceSide.getB();
                if (center.squareOfDistance(B) > radius * radius) {
                    return null;
                } else {
                    collisionNormal = B.subtract(center).toUnitVector();
                    contactPoint = B;
                }
            } else {
                collisionNormal = referenceSide.getNormal().reverse();
                contactPoint = center.add(collisionNormal.multiply(radius));
            }
        }

        double penetrationDepth = radius - separation;
        Collision collision = new Collision(bodyCircle, bodyPolygon, collisionNormal, penetrationDepth);
        collision.addContactPoint(contactPoint);
        return collision;
    }

    private static Collision detect(Body bodyPolygon, Body bodyCircle, Polygon polygon, Circle circle) {
        return detect(bodyCircle, bodyPolygon, circle, polygon);
    }

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
     * @param referenceSide reference side in world coordinates
     * @return incident side which is in incident body coordinates
     */
    private static Side findIncidentSide(Side referenceSide, Body incidentBody) {
        Polygon incidentPolygon = (Polygon) incidentBody.getShape();
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
    private static Side clip(Side incidentSide, Vector vertexOnReferenceSide, Vector directionToVertex) {
        double distanceVertexToA =
                directionToVertex.dotProduct(incidentSide.getA().subtract(vertexOnReferenceSide));
        double distanceVertexToB =
                directionToVertex.dotProduct(incidentSide.getB().subtract(vertexOnReferenceSide));

        // if A is negative, B is positive, A - B is negative, and ratio is positive
        // if A is positive, B is negative, A - B is positive, and ratio is positive
        double ratio = distanceVertexToA / (distanceVertexToA - distanceVertexToB);
        Vector clippedVertex =
                incidentSide.getB().subtract(incidentSide.getA()).multiply(ratio).add(incidentSide.getA());

        Side clippedSide = incidentSide.copy();
        if (distanceVertexToA > 0) {
            clippedSide = clippedSide.changeA(clippedVertex);
        }
        if (distanceVertexToB > 0) {
            clippedSide = clippedSide.changeB(clippedVertex);
        }
        return clippedSide;
    }

}
