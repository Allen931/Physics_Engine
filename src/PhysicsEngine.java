import java.util.ArrayList;
import java.util.LinkedList;

public class PhysicsEngine {
    private final ArrayList<Body> bodies;

    public PhysicsEngine() {
        bodies = new ArrayList<>();
    }

    public void handleCollisions() {
        LinkedList<CollisionResolver.Collision> collisions = new LinkedList<>();
        for (int i = 0; i < bodies.size(); i++) {
            Body bodyA = bodies.get(i);
            for (int j = i + 1; j < bodies.size(); j++) {
                Body bodyB = bodies.get(j);
                CollisionResolver.Collision collision = CollisionResolver.resolve(bodyA, bodyB);
                if (collision != null) {
                    collisions.add(collision);
                }
            }
        }

        for (CollisionResolver.Collision collision : collisions) {
            handleCollision(collision);
        }
    }

    private void handleCollision(CollisionResolver.Collision collision) {
        Body A = collision.bodyA;
        Body B = collision.bodyB;

        // Calculate relative velocity
        Vector relativeVelocity = B.velocity.subtract(A.velocity);
        double velocityAlongNormal = relativeVelocity.dot_product(collision.collisionNormal);
        if (velocityAlongNormal > 0) {
            return;
        }

        // Calculate impulse scalar
        double impulseScalar = -(1 + collision.coefficientOfRestitution) * velocityAlongNormal;
        impulseScalar /= A.inverseMass + B.inverseMass;

        // Apply impulse
        Vector impulse = collision.collisionNormal.multiply(impulseScalar);
        A.velocity.subtract(impulse.multiply(A.inverseMass));
        B.velocity.add(impulse.multiply(B.inverseMass));
    }

    public void add(Body body) {
        bodies.add(body);
    }

}
