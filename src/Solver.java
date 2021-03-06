import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @reference https://www.scss.tcd.ie/~manzkem/CS7057/cs7057-1516-09-CollisionResponse-mm.pdf
 */
public class Solver implements Serializable {
    private final ArrayList<Body> bodies;
    private final LinkedList<Collision> collisions;
    private final HashSet<Body> collisionParallelToY;

    private static final double dt = 0.0005;
    private static final double SLEEPING_VELOCITY_THRESHOLD_SQUARE = 1000.0;
    private static final double SLEEPING_ANGULAR_VELOCITY_THRESHOLD = 0.5;
    private static final double INELASTIC_VELOCITY_THRESHOLD = 5;

    public static final Vector GRAVITY = new Vector(0, 800);
    public static final Vector ZERO = new Vector(0, 0);

    private final boolean enableSleeping = true;

    public Solver() {
        bodies = new ArrayList<>();
        collisions = new LinkedList<>();
        collisionParallelToY = new HashSet<>();
    }

    public void update() {
        solveCollisions();
        removeDeadBodies();
        updateVelocities();
        updatePositions();
    }

    private void updatePositions() {
        for (Body body : bodies) {
            if (!isSleeping(body)) {
                body.updatePosition(dt);
            } else {
                body.resetAcceleration();
                if (body instanceof Bird) {
                    ((Bird) body).loseHP(isSleeping(body));
                }
            }
        }
    }

    private void updateVelocities() {
        for (Body body : bodies) {
            body.setAcceleration(GRAVITY);
            body.updateVelocity(dt);
        }
    }

    private void removeDeadBodies() {
        bodies.removeIf(body -> !body.isAlive());
    }

    private void solveCollisions() {
        collisionParallelToY.clear();
        for (int i = 0; i < bodies.size(); i++) {
            Body bodyA = bodies.get(i);
            for (int j = i + 1; j < bodies.size(); j++) {
                Body bodyB = bodies.get(j);
                if (bodyA.mass == 0 && bodyB.mass == 0) {
                    continue;
                }

                if (!bodyA.aabb.possibleToCollide(bodyB.aabb)) {
                    continue;
                }

                Collision collision = CollisionDetector.detect(bodyA, bodyB);
                if (collision != null) {
                    collisions.add(collision);
                    if (Math.abs(Math.abs(collision.collisionNormal.getY()) - 1.0) < 0.001) {
                        collisionParallelToY.add(bodyA);
                        collisionParallelToY.add(bodyB);
                    }
                }
            }
        }

        LinkedList<Collision> toBeRemoved = new LinkedList<>();
        for (Collision collision : collisions) {
            solveCollision(collision);
            toBeRemoved.add(collision);
        }
        collisions.removeAll(toBeRemoved);
    }

    private void solveCollision(Collision collision) {
        Body A = collision.bodyA;
        Body B = collision.bodyB;
        double angularVelocityA = A.angularVelocity;
        double angularVelocityB = B.angularVelocity;
        Vector collisionNormal = collision.collisionNormal;
        for (Vector contactPoint : collision.contactPoints) {
            Vector contactVectorA = contactPoint.subtract(A.position);
            Vector contactVectorB = contactPoint.subtract(B.position);

            // Calculate relative velocity
            // @reference https://hadroncfy.com/articles/2016/04/21/axial-vectors/index.html
            Vector relativeVelocity = B.velocity.subtract(A.velocity)
                    .subtract(new Vector(-angularVelocityA * contactVectorA.getY(),
                            angularVelocityA * contactVectorA.getX()))
                    .add(new Vector(-angularVelocityB * contactVectorB.getY(),
                            angularVelocityB * contactVectorB.getX()));

            double velocityAlongNormal = relativeVelocity.dotProduct(collisionNormal);
            if (velocityAlongNormal > 0) {
                return;
            }

            double sumOfInverseMass = A.inverseMass + B.inverseMass
                    + Math.pow(contactVectorA.crossProduct2D(collisionNormal), 2) * A.inverseMomentOfInertia
                    + Math.pow(contactVectorB.crossProduct2D(collisionNormal), 2) * B.inverseMomentOfInertia;

            double coefficientOfRestitution
                    = -velocityAlongNormal < INELASTIC_VELOCITY_THRESHOLD ? 0 : collision.coefficientOfRestitution;

            // Calculate impulse magnitude
            double impulseMagnitude = -(1 + coefficientOfRestitution)
                    * velocityAlongNormal / sumOfInverseMass / collision.contactPoints.size();

            // Apply impulse
            Vector impulse = collisionNormal.multiply(impulseMagnitude);
            A.applyImpulse(impulse.reverse(), contactVectorA);
            B.applyImpulse(impulse, contactVectorB);

            double staticFrictionCoefficient = (A.getMaterial().getStaticFrictionCoefficient()
                    + B.getMaterial().getStaticFrictionCoefficient()) / 2;
            double dynamicFrictionCoefficient = (A.getMaterial().getDynamicFrictionCoefficient()
                    + B.getMaterial().getDynamicFrictionCoefficient()) / 2;

            Vector directionOfFriction =
                    relativeVelocity.subtract(collisionNormal.multiply(velocityAlongNormal)).reverse().toUnitVector();
            double frictionImpulseMagnitude = relativeVelocity.dotProduct(directionOfFriction)
                    / sumOfInverseMass / collision.contactPoints.size();

            Vector frictionImpulse;
            if (Math.abs(frictionImpulseMagnitude) < impulseMagnitude * staticFrictionCoefficient) {
                frictionImpulse = directionOfFriction.multiply(frictionImpulseMagnitude).reverse();
            } else {
                frictionImpulse = directionOfFriction.multiply(dynamicFrictionCoefficient * impulseMagnitude);
            }

            A.applyImpulse(frictionImpulse.reverse(), contactVectorA);
            B.applyImpulse(frictionImpulse, contactVectorB);

            positionCorrection(A, B, collision);

            //for debug
//            System.out.println("Impulse: " + impulse);
//            System.out.println("Relative velocity: " + relativeVelocity);
//            System.out.println("Normal: " + collisionNormal);
//            System.out.println("Friction Impulse: " + frictionImpulse);
//            System.out.println("A : Position: " + A.position + " " + "Velocity: " + A.velocity);
//            System.out.println("B : Position: " + B.position + " " + "Velocity: " + B.velocity);
//            System.out.println("A: Angular Velocity " + A.angularVelocity + " " + "B: Angular Velocity " + B.angularVelocity);
        }
    }

    // prevent one body sinking into another
    private void positionCorrection(Body A, Body B, Collision collision) {
        double factor = 0.3;
        double allowance = 0.05;
        Vector correction = collision.collisionNormal.multiply(factor)
                .multiply(Math.max(collision.penetrationDepth - allowance, 0))
                .multiply((1 / (A.inverseMass + B.inverseMass)));
        A.setPosition(A.position.subtract(correction.multiply(A.inverseMass)));
        B.setPosition(B.position.add(correction.multiply(B.inverseMass)));
    }

    private boolean isSleeping(Body body) {
        return enableSleeping && body.velocity.lengthSquare() < SLEEPING_VELOCITY_THRESHOLD_SQUARE &&
                body.angularVelocity < SLEEPING_ANGULAR_VELOCITY_THRESHOLD && collisionParallelToY.contains(body);
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

    public void resetSolver(Stage stage) {
        this.bodies.clear();
        collisions.clear();
        collisionParallelToY.clear();
        addAll(stage.bodies);
    }
}
