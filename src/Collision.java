import java.util.ArrayList;

public class Collision {
    Body bodyA, bodyB;
    // in world coordinates
    // body A points to body B
    Vector collisionNormal;
    double penetrationDepth;
    double coefficientOfRestitution;
    ArrayList<Vector> contactPoints = new ArrayList<>();

//        Side referenceSide, incidentSide;

    public Collision(Body A, Body B, Vector collisionNormal, double penetrationDepth) {
        bodyA = A;
        bodyB = B;
        this.collisionNormal = collisionNormal.toUnitVector();
        this.penetrationDepth = penetrationDepth;
        coefficientOfRestitution = Math.min(A.getMaterial().getCoefficientOfRestitution(),
                B.getMaterial().getCoefficientOfRestitution());
    }

    public void addContactPoint(Vector contactPoint) {
        contactPoints.add(contactPoint);
    }
}
