import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PhysicsEngine {
    private final ArrayList<Body> bodies;
    private final double dt = 0.001;

    public PhysicsEngine() {
        bodies = new ArrayList<>();
    }

    public void update() {
        handleCollisions();
        updatePositions();
        updateVelocities();
    }

    private void updatePositions() {
        for (Body body : bodies) {
            body.updatePosition(dt);
        }
    }

    private void updateVelocities() {
        for (Body body : bodies) {
            body.updateVelocity(dt);
        }
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
        A.setVelocity(A.velocity.subtract(impulse.multiply(A.inverseMass)));
        B.setVelocity(B.velocity.add(impulse.multiply(B.inverseMass)));
        System.out.println("Relative velocity: " + relativeVelocity);
        System.out.println("Normal: " + collision.collisionNormal);
        System.out.println("A : Position: " + A.position + " " + "Velocity: " + A.velocity);
        System.out.println("B : Position: " + B.position + " " + "Velocity: " + B.velocity);
    }

//    private void applyImpulse() {
//
//    }

    // prevent one body sinking into another
    private void positionalCorrection(Body A, Body B, CollisionResolver.Collision collision) {
        double percent = 0.2;
        double slop = 0.01;
        Vector correction = collision.collisionNormal.multiply(percent)
                .multiply(Math.max(collision.penetrationDepth - slop, 0.0f))
                .multiply((1 / (A.inverseMass + B.inverseMass)));
        A.setPosition(A.position.subtract(correction.multiply(A.inverseMass)));
        B.setPosition(B.position.add(correction.multiply(B.inverseMass)));
    }

    public void add(Body body) {
        bodies.add(body);
    }

    public void addAll(List<Body> bodies) {
        if (bodies == null || bodies.isEmpty()) {
            return;
        }
        this.bodies.addAll(bodies);
    }

    public ArrayList<Body> getBodies() {
        return bodies;
    }
}
