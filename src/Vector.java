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

}
