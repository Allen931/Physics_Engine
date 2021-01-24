import java.io.Serializable;
import java.util.ArrayList;

public class StageMaker implements Serializable {

    public static Stage makeTestStage() {
        ArrayList<Body> bodies = new ArrayList<>();
        Vector[] vertices = new Vector[]{new Vector(10, -10), new Vector(10, 10),
                new Vector(-10, 10), new Vector(-10, -10)};
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Body polygon = BodyFactory.createPolygon(60 * j + 100, 40 * i, vertices, Material.STANDARD);
                polygon.setVelocity(new Vector(30, -100));
                polygon.setAngularVelocity(10);
                bodies.add(polygon);
            }
        }

        for (int p = 0; p < 10; p++) {
            for (int q = 0; q < 10; q++) {
                Body circle = BodyFactory.createCircle(60 * p + 120, 40 * q + 20, 10, Material.STANDARD);
                circle.setVelocity(new Vector(-30, -100));
                bodies.add(circle);
            }
        }

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

    public static Stage makeStage3() {
        ArrayList<Body> bodies = new ArrayList<>();

        Body polygon1 = BodyFactory.createRectangle(560, 435, 20, 70, Material.LIGHT_WEIGHT_WOOD);
        Body polygon2 = BodyFactory.createRectangle(650, 435, 20, 70, Material.LIGHT_WEIGHT_WOOD);
        Body polygon3 = BodyFactory.createRectangle(740, 435, 20, 70, Material.LIGHT_WEIGHT_WOOD);

        Body polygon4 = BodyFactory.createRectangle(600, 390, 100, 20, Material.LIGHT_WEIGHT_WOOD);
        Body polygon5 = BodyFactory.createRectangle(700, 390, 100, 20, Material.LIGHT_WEIGHT_WOOD);

        Body polygon6 = BodyFactory.createRectangle(560, 345, 20, 70, Material.LIGHT_WEIGHT_WOOD);
        Body polygon7 = BodyFactory.createRectangle(650, 345, 20, 70, Material.LIGHT_WEIGHT_WOOD);
        Body polygon8 = BodyFactory.createRectangle(740, 345, 20, 70, Material.LIGHT_WEIGHT_WOOD);

        Body polygon9 = BodyFactory.createRectangle(600, 300, 100, 20, Material.LIGHT_WEIGHT_WOOD);
        Body polygon10 = BodyFactory.createRectangle(700, 300, 100, 20, Material.LIGHT_WEIGHT_WOOD);

        Body polygon11 = BodyFactory.createRectangle(560, 255, 20, 70, Material.LIGHT_WEIGHT_WOOD);
        Body polygon12 = BodyFactory.createRectangle(650, 255, 20, 70, Material.LIGHT_WEIGHT_WOOD);
        Body polygon13 = BodyFactory.createRectangle(740, 255, 20, 70, Material.LIGHT_WEIGHT_WOOD);

        Body polygon14 = BodyFactory.createRectangle(600, 210, 100, 20, Material.LIGHT_WEIGHT_WOOD);
        Body polygon15 = BodyFactory.createRectangle(700, 210, 100, 20, Material.LIGHT_WEIGHT_WOOD);

        Pig pig1 = BodyFactory.createPig(605, Game.GROUND_LEVEL - Pig.imageIcon.getIconHeight() / 2.0);
        Pig pig2 = BodyFactory.createPig(695, Game.GROUND_LEVEL - Pig.imageIcon.getIconHeight() / 2.0);

        Pig pig3 = BodyFactory.createPig(605, 380 - Pig.imageIcon.getIconHeight() / 2.0);
        Pig pig4 = BodyFactory.createPig(695, 380 - Pig.imageIcon.getIconHeight() / 2.0);

        Pig pig5 = BodyFactory.createPig(605, 290 - Pig.imageIcon.getIconHeight() / 2.0);
        Pig pig6 = BodyFactory.createPig(695, 290 - Pig.imageIcon.getIconHeight() / 2.0);

        bodies.add(polygon1);
        bodies.add(polygon2);
        bodies.add(polygon3);
        bodies.add(polygon4);
        bodies.add(polygon5);
        bodies.add(polygon6);
        bodies.add(polygon7);
        bodies.add(polygon8);
        bodies.add(polygon9);
        bodies.add(polygon10);
        bodies.add(polygon11);
        bodies.add(polygon12);
        bodies.add(polygon13);
        bodies.add(polygon14);
        bodies.add(polygon15);

        bodies.add(pig1);
        bodies.add(pig2);
        bodies.add(pig3);
        bodies.add(pig4);
        bodies.add(pig5);
        bodies.add(pig6);

        return new Stage(bodies);
    }

    public static Stage makeStage4() {
        ArrayList<Body> bodies = new ArrayList<>();

        Vector[] vertices =
                new Vector[]{new Vector(-100, -150), new Vector(0, -150),
                        new Vector(-100, 150), new Vector(150, 150)};
        Body polygon1 = BodyFactory.createPolygon(350, 320, vertices, Material.STATIC);

        Body circle = BodyFactory.createCircle(300, 120, 50, Material.STEEL);

        Body polygon2 = BodyFactory.createRectangle(560, 435, 20, 70, Material.LIGHT_WEIGHT_WOOD);
        Body polygon3 = BodyFactory.createRectangle(650, 435, 20, 70, Material.LIGHT_WEIGHT_WOOD);
        Body polygon4 = BodyFactory.createRectangle(740, 435, 20, 70, Material.LIGHT_WEIGHT_WOOD);

        Body polygon5 = BodyFactory.createRectangle(600, 390, 100, 20, Material.LIGHT_WEIGHT_WOOD);
        Body polygon6 = BodyFactory.createRectangle(700, 390, 100, 20, Material.LIGHT_WEIGHT_WOOD);

        Body polygon7 = BodyFactory.createRectangle(560, 345, 20, 70, Material.LIGHT_WEIGHT_WOOD);
        Body polygon8 = BodyFactory.createRectangle(650, 345, 20, 70, Material.LIGHT_WEIGHT_WOOD);
        Body polygon9 = BodyFactory.createRectangle(740, 345, 20, 70, Material.LIGHT_WEIGHT_WOOD);

        Body polygon10 = BodyFactory.createRectangle(600, 300, 100, 20, Material.LIGHT_WEIGHT_WOOD);
        Body polygon11 = BodyFactory.createRectangle(700, 300, 100, 20, Material.LIGHT_WEIGHT_WOOD);

        Body polygon12 = BodyFactory.createRectangle(560, 255, 20, 70, Material.LIGHT_WEIGHT_WOOD);
        Body polygon13 = BodyFactory.createRectangle(650, 255, 20, 70, Material.LIGHT_WEIGHT_WOOD);
        Body polygon14 = BodyFactory.createRectangle(740, 255, 20, 70, Material.LIGHT_WEIGHT_WOOD);

        Body polygon15 = BodyFactory.createRectangle(600, 210, 100, 20, Material.LIGHT_WEIGHT_WOOD);
        Body polygon16 = BodyFactory.createRectangle(700, 210, 100, 20, Material.LIGHT_WEIGHT_WOOD);

        Pig pig1 = BodyFactory.createPig(605, Game.GROUND_LEVEL - Pig.imageIcon.getIconHeight() / 2.0);
        Pig pig2 = BodyFactory.createPig(695, Game.GROUND_LEVEL - Pig.imageIcon.getIconHeight() / 2.0);

        Pig pig3 = BodyFactory.createPig(605, 380 - Pig.imageIcon.getIconHeight() / 2.0);
        Pig pig4 = BodyFactory.createPig(695, 380 - Pig.imageIcon.getIconHeight() / 2.0);

        Pig pig5 = BodyFactory.createPig(605, 290 - Pig.imageIcon.getIconHeight() / 2.0);
        Pig pig6 = BodyFactory.createPig(695, 290 - Pig.imageIcon.getIconHeight() / 2.0);

        bodies.add(circle);

        bodies.add(polygon1);
        bodies.add(polygon2);
        bodies.add(polygon3);
        bodies.add(polygon4);
        bodies.add(polygon5);
        bodies.add(polygon6);
        bodies.add(polygon7);
        bodies.add(polygon8);
        bodies.add(polygon9);
        bodies.add(polygon10);
        bodies.add(polygon11);
        bodies.add(polygon12);
        bodies.add(polygon13);
        bodies.add(polygon14);
        bodies.add(polygon15);
        bodies.add(polygon16);


        bodies.add(pig1);
        bodies.add(pig2);
        bodies.add(pig3);
        bodies.add(pig4);
        bodies.add(pig5);
        bodies.add(pig6);

        return new Stage(bodies);
    }

    public static Stage makeStage5() {
        ArrayList<Body> bodies = new ArrayList<>();

        Body polygon1 = BodyFactory.createRectangle(415, 425, 20, 100, Material.WOOD);
        Body polygon2 = BodyFactory.createRectangle(445, 395, 20, 150, Material.LIGHT_WEIGHT_WOOD);

        Pig pig1 = BodyFactory.createPig(350, Game.GROUND_LEVEL - Pig.imageIcon.getIconHeight() / 2.0);
        Pig pig2 = BodyFactory.createPig(490, Game.GROUND_LEVEL - Pig.imageIcon.getIconHeight() / 2.0);

        Body polygon3 = BodyFactory.createRectangle(535, 395, 20, 150, Material.STEEL);
        Body polygon4 = BodyFactory.createSquare(590, 430, 80,  Material.WOOD);

        Pig pig3 = BodyFactory.createRoughPig(590, 390 - Pig.imageIcon.getIconHeight() / 2.0);
        Pig pig4 = BodyFactory.createPig(425, 60 - Pig.imageIcon.getIconHeight() / 2.0);

        Body polygon5 = BodyFactory.createRectangle(500, 200, 420, 40, Material.STATIC);
        Body polygon6 = BodyFactory.createRectangle(425, 120, 80, 120, Material.LIGHT_ROUGH_WOOD);
        Body polygon7 = BodyFactory.createSquare(505, 140, 80, Material.WOOD);

        Pig pig5 = BodyFactory.createPig(595, 180 - Pig.imageIcon.getIconHeight() / 2.0);

        bodies.add(polygon1);
        bodies.add(polygon2);
        bodies.add(polygon3);
        bodies.add(polygon4);
        bodies.add(polygon5);
        bodies.add(polygon6);
        bodies.add(polygon7);


        bodies.add(pig1);
        bodies.add(pig2);
        bodies.add(pig3);
        bodies.add(pig4);
        bodies.add(pig5);

        return new Stage(bodies);
    }

}
