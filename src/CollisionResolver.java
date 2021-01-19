public class CollisionResolver {

    public static class Collision {
        Vector collision_normal;
        double penetration_depth;

        public Collision() {

        }
    }

    public static Collision resolve(Circle A, Circle B) {
        double penetration_depth = A.center.squareOfDistance(B.center) - Math.pow(A.radius + B.radius, 2);
        if (penetration_depth <= 0) {
            return 
        }
        return null;
    }

}
