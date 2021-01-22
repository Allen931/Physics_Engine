import java.util.ArrayList;
import java.util.List;

public class Collision {
    Body bodyA, bodyB;
    // in world coordinates
    // body A points to body B
    Vector collisionNormal;
    double penetrationDepth;
    double coefficientOfRestitution;
    ArrayList<Vector> contactPoints = new ArrayList<>();

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

    public void addContactPoints(List<Vector> contactPoints) {
        this.contactPoints.addAll(contactPoints);
    }
}
