public class Plane {
    Vector A;
    Vector B;
    Vector normal;

    public Plane(Vector A, Vector B, Vector normal) {
        this.A = A;
        this.B = B;
        this.normal = normal;
    }

    public double distanceToPlane(Vector target) {
        Vector vertexToTarget = target.subtract(A);
        return vertexToTarget.dot_product(normal);
    }

}
