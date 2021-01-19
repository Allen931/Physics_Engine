public class Circle implements Shape{
    Vector center;
    double radius;

    public Circle(double x, double y, double r) {
        center = new Vector(x, y);
        radius = r;
    }

}
