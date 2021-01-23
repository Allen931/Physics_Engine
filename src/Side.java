import java.io.Serializable;

public class Side implements Serializable {
    private final Vector A;
    private final Vector B;
    private Vector normal = null;

    // Side AB
    public Side(Vector A, Vector B) {
        this.A = A;
        this.B = B;
    }

    public Vector toVectorForm() {
        return B.subtract(A);
    }

    public Vector getDirection() {
        return B.subtract(A).toUnitVector();
    }

    public Vector getNormal() {
        if (normal == null) {
            normal = B.subtract(A).unitNormalVector();
        }
        return normal;
    }

    public double length() {
        return B.subtract(A).length();
    }

    public Vector getA() {
        return A;
    }

    public Vector getB() {
        return B;
    }

    /**
     * @param target a point
     * @return the distance from the point to the side in the direction of the normal
     */
    public double distanceToSide(Vector target) {
        Vector vertexToTarget = target.subtract(A);
        return vertexToTarget.dotProduct(getNormal());
    }

    public Side changeA(Vector changedA) {
        return new Side(changedA, B);
    }

    public Side changeB(Vector changedB) {
        return new Side(A, changedB);
    }

    public Side toWorldCoordinates(Matrix rotationMatrix, Vector position) {
        return new Side(rotationMatrix.transform(A).add(position), rotationMatrix.transform(B).add(position));
    }

    public Side toBodyCoordinates(Matrix rotationMatrix, Vector position) {
        return new Side(rotationMatrix.transpose().transform(A.subtract(position)),
                rotationMatrix.transpose().transform(B.subtract(position)));
    }

    public Side copy() {
        return new Side(A, B);
    }
}
