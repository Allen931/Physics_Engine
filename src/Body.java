public class Body {
    Vector velocity = new Vector(0, 0);
    Vector acceleration = new Vector(0, 0);
    Vector position;
    Shape shape;
    double mass;
    double inverseMass;
    double coefficientOfRestitution;

    // angle in radian
    double orientation;


    public Body(Vector position, Shape shape, double mass, double coefficientOfRestitution) {
        this.position = position;
        this.shape = shape;
        this.mass = mass;
        inverseMass = mass == 0 ? 0 : 1 / mass;
        this.coefficientOfRestitution = coefficientOfRestitution;
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

    public static Body createCircle(double x, double y, double radius, double mass, double coefficientOfRestitution) {
        return new Body(new Vector(x, y), new Circle(radius), mass, coefficientOfRestitution);
    }

    public static Body createPolygon(double x, double y, Vector[] vertices, double mass, double coefficientOfRestitution) {
        return new Body(new Vector(x, y), new Polygon(vertices), mass, coefficientOfRestitution);
    }

}
