import java.io.Serializable;

public class Body implements Serializable {
    AABB aabb;
    Vector velocity = new Vector(0, 0);
    Vector acceleration = new Vector(0, 0);
    Vector position;
    private final Shape shape;
    Material material;
    double mass;
    double inverseMass;
    double healthPoint;

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
        healthPoint = 2500 * mass;
    }

    public boolean isAlive() {
        return (healthPoint > 0 && position.getY() < 500) || mass == 0;
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

    public void setAngularVelocity(double angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public void resetAcceleration() {
        acceleration = new Vector(0, 0);
    }

    public void applyImpulse(Vector impulse, Vector contactVector) {
        velocity = velocity.add(impulse.multiply(inverseMass));
        angularVelocity += contactVector.crossProduct2D(impulse) * inverseMomentOfInertia;
        if (!(this instanceof Bird)) {
            this.loseHP(impulse);
        }
    }

    public void loseHP(Vector impulse) {
        double damage = impulse.length();
        if (damage > 100000) {
            healthPoint -= damage;
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

    public static Bird createBird(double x, double y) {
        return new Bird(new Vector(x, y));
    }

    public static Bird createBird(Vector position) {
        return new Bird(position);
    }

    public static Pig createPig(double x, double y) {
        return new Pig(new Vector(x, y));
    }

    public static RoughPig createRoughPig(double x, double y) {
        return new RoughPig(new Vector(x, y));
    }
}
