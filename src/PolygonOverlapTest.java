import org.junit.Test;
import org.junit.Assert;

public class PolygonOverlapTest {

    @Test
    public void overlapTest() {
        Vector[] vertices = new Vector[]{new Vector(10, -10), new Vector(10, 10),
                new Vector(-10, 10), new Vector(-10, -10)};
        Body polygonA = Body.createPolygon(30, 30, vertices, 20, 1);
        Body polygonB = Body.createPolygon(37, 30, vertices.clone(), 20, 1);

        Polygon A = (Polygon) polygonA.getShape();
        Polygon B = (Polygon) polygonB.getShape();
        System.out.println(CollisionResolver.findAxisLeastPenetration(polygonA, polygonB).separation);

        Assert.assertTrue(CollisionResolver.findAxisLeastPenetration(polygonA, polygonB).separation < 0);
    }

    @Test
    public void overlapTest2() {
        Vector[] vertices = new Vector[]{new Vector(10, -10), new Vector(10, 10),
                new Vector(-10, 10), new Vector(-10, -10)};
        Body polygonA = Body.createPolygon(30, 30, vertices, 20, 1);
        Body polygonB = Body.createPolygon(37, 30, vertices.clone(), 20, 1);

        polygonA.rotate(Math.PI);
        polygonB.rotate(Math.PI);

        Polygon A = (Polygon) polygonA.getShape();
        Polygon B = (Polygon) polygonB.getShape();
        System.out.println(CollisionResolver.findAxisLeastPenetration(polygonA, polygonB).separation);

        Assert.assertTrue(CollisionResolver.findAxisLeastPenetration(polygonA, polygonB).separation < 0);
    }

}
