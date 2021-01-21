public class CollisionResolver {

    public static class Collision {
        Body bodyA, bodyB;
        Vector collisionNormal;
        double penetrationDepth;
        double coefficientOfRestitution;

        public Collision(Body A, Body B, Vector collisionNormal, double penetrationDepth) {
            bodyA = A;
            bodyB = B;
            this.collisionNormal = collisionNormal.to_unit();
            this.penetrationDepth = penetrationDepth;
            coefficientOfRestitution = Math.min(A.coefficientOfRestitution, B.coefficientOfRestitution);
        }
    }

    public static Collision resolve(Body A, Body B) {
        if (A.shape instanceof Circle && B.shape instanceof Circle) {
            return resolve(A, B, (Circle) A.shape, (Circle) B.shape);
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
        double penetration_depth = Math.pow(circleA.radius + circleB.radius, 2)
                - bodyA.position.squareOfDistance(bodyB.position);
        if (penetration_depth > 0) {
            Vector normal = bodyB.position.subtract(bodyA.position);
            penetration_depth = Math.sqrt(penetration_depth);
            return new Collision(bodyA, bodyB, normal, penetration_depth);
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

}
