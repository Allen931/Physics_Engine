import java.awt.*;
import java.io.Serializable;

public class Body implements Serializable {
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

    public boolean isAlive() {
        return true;
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
        if (mass != 0) {
            this.velocity = velocity;
        }
    }

    public void setAcceleration(Vector acceleration) {
        if (mass != 0) {
            this.acceleration = acceleration;
        }
    }

    public void resetAcceleration() {
        acceleration = new Vector(0, 0);
    }

    public void applyImpulse(Vector impulse, Vector contactVector) {
        velocity = velocity.add(impulse.multiply(inverseMass));
        angularVelocity += contactVector.crossProduct2D(impulse) * inverseMomentOfInertia;
        if (this instanceof Pig) {
            ((Pig) this).loseHP(impulse);
        }
    }

    public Shape getShape() {
        return shape;
    }

    public Material getMaterial() {
        return material;
    }

    /**
     * rotate the body
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



}

class BodyFactory {
    public static Body createCircle(double x, double y, double radius, Material material) {
        return new Body(new Vector(x, y), new Circle(radius), material);
    }

    public static Body createPolygon(double x, double y, Vector[] vertices, Material material) {
        return new Body(new Vector(x, y), new Polygon(vertices), material);
    }

    public static Body createRectangle(double x, double y, double width, double height, Material material) {
        Vector[] vertices = new Vector[]{new Vector(width / 2.0, -height / 2.0),
                new Vector(width / 2.0, height / 2.0),
                new Vector(-width / 2.0, height / 2.0),
                new Vector(-width / 2.0, -height / 2.0)};
        return createPolygon(x, y, vertices, material);
    }

    public static Body createSquare(double x, double y, double length, Material material) {
        return createRectangle(x, y, length, length, material);
    }

    public static Bird createBird(Vector position, Image image, double radius) {
        return new Bird(position, image, radius);
    }

    public static Pig createPig(double x, double y, Image image, double radius) {
        return new Pig(new Vector(x, y), image, radius);
    }

}
