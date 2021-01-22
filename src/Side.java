public class Side {
    private final Vector A;
    private final Vector B;
    private final Vector normal;
    private final Vector direction;

    // Side AB
    public Side(Vector A, Vector B) {
        this.A = A;
        this.B = B;
        this.normal = B.subtract(A).unitNormalVector();
        this.direction = B.subtract(A).to_unit();
    }

    public Vector getDirection() {
        return direction;
    }

    public Vector getNormal() {
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
        return vertexToTarget.dot_product(normal);
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
