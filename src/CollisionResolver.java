public class CollisionResolver {

    public static class Collision {
        Body body_A, body_B;
        Vector collisionNormal;
        double penetrationDepth;
        double coefficientOfRestitution;

        public Collision(Body A, Body B, Vector collisionNormal, double penetrationDepth) {
            body_A = A;
            body_B = B;
            this.collisionNormal = collisionNormal;
            this.penetrationDepth = penetrationDepth;
            coefficientOfRestitution = Math.min(A.coefficientOfRestitution, B.coefficientOfRestitution);
        }
    }

    public static Collision resolve(Body A, Body B) {
        if (A.shape instanceof Circle && B.shape instanceof Circle) {
            return resolve(A, B, (Circle) A.shape, (Circle) B.shape);
        } else if (A.shape instanceof Polygon && B.shape instanceof Polygon) {
            return resolve(A, B, (Polygon) A.shape, (Polygon) B.shape);
        } else if (A.shape instanceof Circle && B.shape instanceof Polygon) {
            return resolve(A, B, (Circle) A.shape, (Polygon) B.shape);
        } else if (A.shape instanceof Polygon && B.shape instanceof Circle) {
            return resolve(A, B, (Polygon) A.shape, (Circle) B.shape);
        } else {
            return null;
        }
    }

    private static Collision resolve(Body body_A, Body body_B, Circle circle_A, Circle circle_B) {
        double penetration_depth = circle_A.center.squareOfDistance(circle_B.center)
                - Math.pow(circle_A.radius + circle_B.radius, 2);
        if (penetration_depth <= 0) {
            Vector normal = circle_B.center.subtract(circle_A.center);
            penetration_depth = Math.sqrt(penetration_depth);
            return new Collision(body_A, body_B, normal, penetration_depth);
        }
        return null;
    }

    private static Collision resolve(Body body_A, Body body_B, Polygon polygon_A, Polygon polygon_B) {

    }

    private static Collision resolve(Body body_A, Body body_B, Circle circle, Polygon polygon) {

    }

    private static Collision resolve(Body body_A, Body body_B, Polygon polygon, Circle circle) {

    }
}
