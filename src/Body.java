public class Body {
    Vector velocity = new Vector(0, 0);
    Vector center;
    Shape shape;
    double mass;
    double inverseMass;
    double coefficientOfRestitution;


    public Body(Vector center, Shape shape, double mass, double coefficientOfRestitution) {
        this.center = center;
        this.shape = shape;
        this.mass = mass;
        inverseMass = 1 / mass;
        this.coefficientOfRestitution = coefficientOfRestitution;
    }

    public static Body createCircle(double x, double y, double radius, double mass, double coefficientOfRestitution) {
        return new Body(new Vector(x, y), new Circle(radius), mass, coefficientOfRestitution);
    }

}
