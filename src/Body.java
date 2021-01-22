public class Body {
    Vector velocity = new Vector(0, 0);
    Vector acceleration = new Vector(0, 0);
    Vector position;
    private final Shape shape;
    private final Material material;
    double mass;
    double inverseMass;

    // angle in radian
    double orientation;
    Matrix rotationMatrix;


    public Body(Vector position, Shape shape, Material material) {
        this.position = position;
        this.shape = shape;
        this.material = material;
        this.mass = material.getDensity() * shape.getArea();
        inverseMass = mass == 0 ? 0 : 1 / mass;
        orientation = 0;
        rotationMatrix = new Matrix(orientation);
    }

    public void updatePosition(double dt) {
        position = position.add(velocity.multiply(dt));
    }

    public void updateVelocity(double dt) {
        velocity = velocity.add(acceleration.multiply(dt));
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public void setAcceleration(Vector acceleration) {
        this.acceleration = acceleration;
    }

    public Shape getShape() {
        return shape;
    }

    /**
     * rotate the body
     * @param angle in radian
     */
    public void rotate(double angle) {
        orientation += angle;
        rotationMatrix = new Matrix(orientation);
    }

    public Vector toWorldCoordinates(Vector vector) {
        return rotationMatrix.transform(vector).add(position);
    }

    public Side toWorldCoordinates(Side side) {
        return side.toWorldCoordinates(rotationMatrix, position);
    }

    public Vector toBodyCoordinates(Vector vector) {
        return rotationMatrix.transpose().transform(vector).subtract(position);
    }

    public Side toBodyCoordinates(Side side) {
        return side.toBodyCoordinates(rotationMatrix, position);
    }

    public static Body createCircle(double x, double y, double radius, Material material) {
        return new Body(new Vector(x, y), new Circle(radius), material);
    }

    public static Body createPolygon(double x, double y, Vector[] vertices, Material material) {
        return new Body(new Vector(x, y), new Polygon(vertices), material);
    }

}
