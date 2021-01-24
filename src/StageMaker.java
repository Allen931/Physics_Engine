import java.io.Serializable;
import java.util.ArrayList;

public class StageMaker implements Serializable {

    public static Stage makeTestStage() {
        ArrayList<Body> bodies = new ArrayList<>();
        Vector[] vertices = new Vector[]{new Vector(10, -10), new Vector(10, 10),
                new Vector(-10, 10), new Vector(-10, -10)};
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Body polygon = BodyFactory.createPolygon(60 * j + 100, 40 * i, vertices, Material.WOOD);
                polygon.setVelocity(new Vector(50, 200));
                polygon.setAcceleration(new Vector(0, 200));
                polygon.angularVelocity = 20;
                bodies.add(polygon);
            }
        }

        for (int p = 0; p < 10; p++) {
            for (int q = 0; q < 10; q++) {
                Body circle = BodyFactory.createCircle(60 * p + 120, 40 * q + 20, 20, Material.WOOD);
                circle.setVelocity(new Vector(-50, 200));
                circle.setAcceleration(new Vector(0, 200));
                circle.angularVelocity = 0;
                bodies.add(circle);
            }
        }

        Pig pig = BodyFactory.createPig(750, Game.GROUND_LEVEL - Pig.imageIcon.getIconHeight() / 2.0);

        bodies.add(pig);

        return new Stage(bodies);
    }

    public static Stage makeStage1() {
        ArrayList<Body> bodies = new ArrayList<>();
        Body centerPolygon = BodyFactory.createRectangle(400, 320, 200,  300, Material.STATIC);
        Pig pig = BodyFactory.createPig(750, Game.GROUND_LEVEL - Pig.imageIcon.getIconHeight() / 2.0);

        bodies.add(centerPolygon);
        bodies.add(pig);

        return new Stage(bodies);
    }

    public static Stage makeStage2() {
        ArrayList<Body> bodies = new ArrayList<>();

        Body polygon1 = BodyFactory.createRectangle(300, 400, 30, 200, Material.WOOD);
        Body polygon2 = BodyFactory.createRectangle(500, 400, 30, 200, Material.WOOD);
        Body polygon3 = BodyFactory.createRectangle(400, 285, 230, 30, Material.STEEL);
        Body polygon4 = BodyFactory.createRectangle(400, 170, 30, 200, Material.WOOD);
        bodies.add(polygon1);
        bodies.add(polygon2);
        bodies.add(polygon3);
        bodies.add(polygon4);

        Pig pig1 = BodyFactory.createPig(400, Game.GROUND_LEVEL - Pig.imageIcon.getIconHeight() / 2.0);
        Pig pig2 = BodyFactory.createPig(400, 270 - Pig.imageIcon.getIconHeight() / 2.0);
        Pig pig3 = BodyFactory.createPig(750, Game.GROUND_LEVEL - Pig.imageIcon.getIconHeight() / 2.0);
        bodies.add(pig1);
        bodies.add(pig2);
        bodies.add(pig3);

        return new Stage(bodies);
    }

//    public static Stage makeStage3() {
//
//    }
}
