public class Matrix {
    private final Vector columnVector0, columnVector1;

    public Matrix(double m00, double m01, double m10, double m11) {
        columnVector0 = new Vector(m00, m10);
        columnVector1 = new Vector(m01, m11);
    }

    public Matrix(Vector columnVector0, Vector columnVector1) {
        this.columnVector0 = columnVector0;
        this.columnVector1 = columnVector1;
    }

    public Matrix transpose() {
        Vector rowVector0 = new Vector(columnVector0.getX(), columnVector1.getX());
        Vector rowVector1 = new Vector(columnVector0.getY(), columnVector1.getY());
        return new Matrix(rowVector0, rowVector1);
    }

    public Vector multiplyWithVector(Vector vector) {
        Vector v0 = columnVector0.multiply(vector.getX());
        Vector v1 = columnVector1.multiply(vector.getY());

        return new Vector(v0.getX() + v1.getX(), v0.getY() + v1.getY());
    }

    public Matrix multiplyWithMatrix(Matrix matrix) {
        Vector v0 = multiplyWithVector(matrix.columnVector0);
        Vector v1 = multiplyWithVector(matrix.columnVector1);
        return new Matrix(v0, v1);
    }

}
