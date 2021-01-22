public class Vector implements Comparable<Vector> {
    private final double x;
    private final double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double squareOfDistance(Vector target) {
        return Math.pow(x - target.x, 2) + Math.pow(y - target.y, 2);
    }

    public Vector subtract(Vector target) {
        return new Vector(x - target.x, y - target.y);
    }

    public Vector add(Vector target) {
        return new Vector(target.x + x, target.y + y);
    }

    public Vector multiply(double coefficient) {
        return new Vector(x * coefficient, y * coefficient);
    }

    public Vector multiply(int coefficient) {
        return new Vector(x * coefficient, y * coefficient);
    }

    public double dot_product(Vector target) {
        return x * target.x + y * target.y;
    }

    public double cross_product_2D(Vector target) {
        return x * target.y - y * target.x;
    }

    public Vector negative() {
        return new Vector(-x, -y);
    }

    public Vector to_unit() {
        double length = length();
        return new Vector(x / length, y / length);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double length() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public double angle() {
        return Math.atan2(y, x);
    }

    @Override
    public String toString() {
        return "X :" + x + " Y :" + y;
    }

    @Override
    public int compareTo(Vector o) {
        if (angle() > o.angle()) {
            return 1;
        } else if (angle() == o.angle()) {
            return -Double.compare(length(), o.length());
        } else {
            return -1;
        }
    }

    public Vector unitNormalVector() {
        return new Vector(y, -x).to_unit();
    }
}
