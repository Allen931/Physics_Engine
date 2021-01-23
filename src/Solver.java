import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @reference https://www.scss.tcd.ie/~manzkem/CS7057/cs7057-1516-09-CollisionResponse-mm.pdf
 */
public class Solver {
    private final ArrayList<Body> bodies;
    private final double dt = 0.001;

    public Solver() {
        bodies = new ArrayList<>();
    }

    public void update() {
        handleCollisions();
        updatePositions();
        updateVelocities();
    }

    private void updatePositions() {
        for (Body body : bodies) {
            if (body.isAlive) {
                body.updatePosition(dt);
            }
        }
    }

    private void updateVelocities() {
        for (Body body : bodies) {
            if (body.isAlive) {
                body.updateVelocity(dt);
            }
        }
    }

    public void handleCollisions() {
        LinkedList<Collision> collisions = new LinkedList<>();
        for (int i = 0; i < bodies.size(); i++) {
            Body bodyA = bodies.get(i);
            if (bodyA.isAlive) {
                for (int j = i + 1; j < bodies.size(); j++) {
                    Body bodyB = bodies.get(j);
                    if (bodyB.isAlive) {
                        Collision collision = CollisionDetector.detect(bodyA, bodyB);
                        if (collision != null) {
                            collisions.add(collision);
                        }
                    }
                }
            }
        }

        for (Collision collision : collisions) {
            handleCollision(collision);
        }
    }

    private void handleCollision(Collision collision) {
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

            // TODO
            double sumOfInverseMass = A.inverseMass + B.inverseMass
                    + Math.pow(contactVectorA.crossProduct2D(collisionNormal), 2) * A.inverseMomentOfInertia
                    + Math.pow(contactVectorB.crossProduct2D(collisionNormal), 2) * B.inverseMomentOfInertia;

            // TODO
            // Calculate impulse magnitude
            double impulseMagnitude = -(1 + collision.coefficientOfRestitution)
                    * velocityAlongNormal / sumOfInverseMass / collision.contactPoints.size();

            // Apply impulse
            Vector impulse = collisionNormal.multiply(impulseMagnitude);
            System.out.println("Impulse: " + impulse);
            A.applyImpulse(impulse.reverse(), contactVectorA);
            B.applyImpulse(impulse, contactVectorB);

            double staticFrictionCoefficient = (A.getMaterial().getStaticFrictionCoefficient()
                    + B.getMaterial().getStaticFrictionCoefficient()) / 2;
            double dynamicFrictionCoefficient = (A.getMaterial().getDynamicFrictionCoefficient()
                    + B.getMaterial().getDynamicFrictionCoefficient()) / 2;

            Vector directionOfFriction =
                    relativeVelocity.subtract(collisionNormal.multiply(velocityAlongNormal)).reverse();
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

            positionalCorrection(A, B, collision);

            System.out.println("Relative velocity: " + relativeVelocity);
            System.out.println("Normal: " + collisionNormal);
            System.out.println("A : Position: " + A.position + " " + "Velocity: " + A.velocity);
            System.out.println("B : Position: " + B.position + " " + "Velocity: " + B.velocity);
        }
    }

    // prevent one body sinking into another
    private void positionalCorrection(Body A, Body B, Collision collision) {
        double percent = 0.2;
        double slop = 0.01;
        Vector correction = collision.collisionNormal.multiply(percent)
                .multiply(Math.max(collision.penetrationDepth - slop, 0))
                .multiply((1 / (A.inverseMass + B.inverseMass)));
        A.setPosition(A.position.subtract(correction.multiply(A.inverseMass)));
        B.setPosition(B.position.add(correction.multiply(B.inverseMass)));
    }

    //Todo
    private boolean isResting(Body body) {
        return false;
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
