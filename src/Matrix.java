import org.junit.experimental.max.MaxCore;

import java.io.Serializable;

public class Matrix implements Serializable {
    private final Vector columnVector0, columnVector1;

    public Matrix(double m00, double m01, double m10, double m11) {
        columnVector0 = new Vector(m00, m10);
        columnVector1 = new Vector(m01, m11);
    }

    public Matrix(Vector columnVector0, Vector columnVector1) {
        this.columnVector0 = columnVector0;
        this.columnVector1 = columnVector1;
    }

    // rotation matrix
    public Matrix(double angle) {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        columnVector0 = new Vector(cos, sin);
        columnVector1 = new Vector(-sin, cos);
    }

    public Matrix transpose() {
        Vector rowVector0 = new Vector(columnVector0.getX(), columnVector1.getX());
        Vector rowVector1 = new Vector(columnVector0.getY(), columnVector1.getY());
        return new Matrix(rowVector0, rowVector1);
    }

    public Vector transform(Vector vector) {
        Vector v0 = columnVector0.multiply(vector.getX());
        Vector v1 = columnVector1.multiply(vector.getY());

        return new Vector(v0.getX() + v1.getX(), v0.getY() + v1.getY());
    }

    public Matrix matrixMultiplication(Matrix matrix) {
        Vector v0 = transform(matrix.columnVector0);
        Vector v1 = transform(matrix.columnVector1);
        return new Matrix(v0, v1);
    }

}
