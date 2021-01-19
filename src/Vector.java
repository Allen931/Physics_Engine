public class Vector {
    double x;
    double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double squareOfDistance(Vector target) {
        return Math.pow(x - target.x, 2) + Math.pow(y - target.y, 2);
    }

    public Vector subtract(Vector target) {
        return new Vector(target.x - x, target.y - y);
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

    public void to_unit() {
        double ratio = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        x = x / ratio;
        y = y / ratio;
    }

}
