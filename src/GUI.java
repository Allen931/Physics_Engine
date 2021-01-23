import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {
    public GUI() {
        setSize(800, 500);
        setTitle("Test");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Panel panel = new GUI.Panel();
        Container c = getContentPane();
        c.add(panel);
        setVisible(true);
        setResizable(false);
    }

    public static void main(String[] args) {
        new GUI();
    }

    public class Panel extends JPanel implements ActionListener {
        Solver solver = new Solver();
        Timer timer;

        public Panel() {
            setBackground(Color.white);
            timer = new Timer(16, this);
            timer.start();
            setGround();
//            Body A = Body.createCircle(200, 80, 40, Material.STANDARD);
//            Body B = Body.createCircle(600, 100, 40, Material.STANDARD);
            Vector[] vertices = new Vector[]{new Vector(10, -10), new Vector(10, 10),
                    new Vector(-10, 10), new Vector(-10, -10)};
            Vector[] vertices2 = new Vector[]{new Vector(40, -40), new Vector(40, 40),
                    new Vector(-40, 40), new Vector(-40, -40)};

            Body A = Body.createPolygon(200, 90, vertices2, Material.STANDARD);
            Body B = Body.createPolygon(600, 120, vertices2, Material.STANDARD);
//            System.out.println(A.momentOfInertia);
//            System.out.println(B.momentOfInertia);
//            A.setVelocity(new Vector(100, 0));
//            B.setVelocity(new Vector(-100, 0));
//            A.setAcceleration(new Vector(0, 200));
//            B.setAcceleration(new Vector(0, 200));
//            A.angularVelocity = 2;
//            B.angularVelocity = 1;
//            physicsEngine.add(A);
//            physicsEngine.add(B);

            Vector[] vertices3 = new Vector[]{new Vector(10, -10), new Vector(10, 10),
                    new Vector(-10, 10), new Vector(-10, -10)};
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    Body polygon = Body.createPolygon(60 * j + 300, 40 * i, vertices3, Material.STANDARD);
                    polygon.setVelocity(new Vector(50, 0));
                    polygon.setAcceleration(new Vector(0, 200));
                    polygon.angularVelocity = 20;
                    solver.add(polygon);
                }
            }

            for (int p = 0; p < 5; p++) {
                for (int q = 0; q < 5; q++) {
                    Body circle = Body.createCircle(60 * p + 320, 40 * q + 20, 10, Material.STANDARD);
                    circle.setVelocity(new Vector(-50, 0));
                    circle.setAcceleration(new Vector(0, 200));
                    circle.angularVelocity = 0;
                    solver.add(circle);
                }
            }

        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawBodies(g);
        }

        private void drawBodies(Graphics graphics) {
            for (Body body : solver.getBodies()) {
                if (body.getShape() instanceof Circle) {
                    drawCircle(graphics, body);
                } else if (body.getShape() instanceof Polygon) {
                    drawPolygon(graphics, body);
                }
            }
        }

        private void drawCircle(Graphics graphics, Body body) {
            Circle circle = (Circle) body.getShape();
            graphics.drawOval((int) Math.round(body.position.getX() - circle.getRadius()),
                    (int) Math.round(body.position.getY() - circle.getRadius()),
                    (int) Math.round(circle.getRadius() * 2),
                    (int) Math.round(circle.getRadius() * 2));
        }

        private void drawPolygon(Graphics graphics, Body body) {
            Polygon polygon = (Polygon) body.getShape();
            Vector[] vertices = polygon.getVertices();
            int[] x = new int[polygon.getVertices().length];
            int[] y = new int[polygon.getVertices().length];

            for (int i = 0; i < vertices.length; i++) {
                Vector vertex = body.toUICoordinates(vertices[i]);
                x[i] = (int) vertex.getX();
                y[i] = (int) vertex.getY();
            }

            graphics.drawPolygon(x, y, vertices.length);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == timer) {
                for (int i = 0; i < 16; i++) {
                    solver.update();
                }
                repaint();
            }
        }

        public void setGround() {
            Vector[] vertices = {new Vector(0, 450), new Vector(800, 450),
                                new Vector(0, 600), new Vector(800, 600)};
            Body ground = Body.createPolygon(getWidth() / 2.0 , getHeight(), vertices, Material.STATIC);
            solver.add(ground);
        }
    }

}
