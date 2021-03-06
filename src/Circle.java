import java.io.Serializable;

public class Circle implements Shape, Serializable {
    private final double radius;

    public Circle(double r) {
        radius = r;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }

    @Override
    public double getMomentOfInertiaFactor() {
        return Math.pow(radius, 4) * Math.PI / 4;
    }
}
