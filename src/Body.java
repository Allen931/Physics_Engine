import java.util.ArrayList;

import static java.lang.Math.PI;

public class Body {
    AABB aabb;
    Vector velocity = new Vector(0, 0);
    Vector acceleration = new Vector(0, 0);
    Vector position;
    private final Shape shape;
    private final Material material;
    double mass;
    double inverseMass;

    // angle in radian
    double orientation;
    double angularVelocity;
    double momentOfInertia;
    double inverseMomentOfInertia;
    Matrix rotationMatrix;

    boolean isAlive = true;

    public Body(Vector position, Shape shape, Material material) {
        this.position = position;
        this.shape = shape;
        this.material = material;
        this.mass = material.getDensity() * shape.getArea();
        inverseMass = mass == 0 ? 0 : 1 / mass;
        setOrientation(0.0);
        calculateAABB();
        angularVelocity = 0;
        momentOfInertia = shape.getMomentOfInertiaFactor() * material.getDensity();
        inverseMomentOfInertia = momentOfInertia == 0 ? 0 : 1 / momentOfInertia;
    }

    private void calculateAABB() {
        if (shape instanceof Circle) {
            aabb = new AABB(position, ((Circle) shape).getRadius());
        } else if (shape instanceof Polygon) {
            aabb = new AABB(this, ((Polygon) shape).getVertices());
        }
    }

    public void updatePosition(double dt) {
        if (mass != 0) {
            position = position.add(velocity.multiply(dt));
            setOrientation(orientation + angularVelocity * dt);
            calculateAABB();
        }
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

    public void resetAcceleration() {
        acceleration = new Vector(0, 0);
    }

    public void applyImpulse(Vector impulse, Vector contactVector) {
        velocity = velocity.add(impulse.multiply(inverseMass));
        angularVelocity += contactVector.crossProduct2D(impulse) * inverseMomentOfInertia;
        System.out.println(shape.toString() + "\n" + angularVelocity);
    }

    public Shape getShape() {
        return shape;
    }

    public Material getMaterial() {
        return material;
    }

    /**
     * rotate the body
     *
     * @param angle in radian
     */
    public void rotate(double angle) {
        orientation += angle;
        rotationMatrix = new Matrix(orientation);
    }

    public void setOrientation(double angle) {
        orientation = angle;
        rotationMatrix = new Matrix(orientation);
    }

    public Vector toWorldCoordinates(Vector vector) {
        return rotationMatrix.transform(vector).add(position);
    }

    public Side toWorldCoordinates(Side side) {
        return side.toWorldCoordinates(rotationMatrix, position);
    }

    public Vector toBodyCoordinates(Vector vector) {
        return rotationMatrix.transpose().transform(vector.subtract(position));
    }

    public Side toBodyCoordinates(Side side) {
        return side.toBodyCoordinates(rotationMatrix, position);
    }

    public Vector toUICoordinates(Vector vector) {
        // round orientation to nearest integer in degree
        Matrix uiRotationMatrix = new Matrix(Math.toRadians(Math.round(Math.toDegrees(orientation))));
        return uiRotationMatrix.transform(vector).add(position);
    }

    public static Body createCircle(double x, double y, double radius, Material material) {
        return new Body(new Vector(x, y), new Circle(radius), material);
    }

    public static Body createPolygon(double x, double y, Vector[] vertices, Material material) {
        return new Body(new Vector(x, y), new Polygon(vertices), material);
    }

}
