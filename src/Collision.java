import java.util.ArrayList;

public class Collision {
    Body bodyA, bodyB;
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
        coefficientOfRestitution = Math.min(A.coefficientOfRestitution, B.coefficientOfRestitution);
    }

    public void addContactPoint(Vector contactPoint) {
        contactPoints.add(contactPoint);
    }
}
