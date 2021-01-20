import java.util.Arrays;

public class Polygon implements Shape{
    Vector[] vertices;
    Vector[] normals;

    public Polygon(Vector[] vertices) {
        this.vertices = vertices;
        sortVertices();
        initializeNormals();
    }

    private void sortVertices() {
        Arrays.sort(vertices);
    }

    private void initializeNormals() {
        for (int i = 0; i < vertices.length - 1; i++) {
            normals[i] = vertices[i + 1].subtract(vertices[i]).unitNormalVector();
        }
        normals[vertices.length - 1] = vertices[vertices.length - 1].subtract(vertices[0]).unitNormalVector();
    }

}
