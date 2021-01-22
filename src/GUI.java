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
        PhysicsEngine physicsEngine = new PhysicsEngine();
        Timer timer;

        public Panel() {
            setBackground(Color.white);
            timer = new Timer(20, this);
            timer.start();
            Body A = Body.createCircle(200, 100, 20, Material.STANDARD);
            Body B = Body.createCircle(600, 100, 20, Material.STANDARD);
            A.setVelocity(new Vector(50, 0));
            B.setVelocity(new Vector(-50, 0));
            A.setAcceleration(new Vector(0, 9.8));
            B.setAcceleration(new Vector(0, 9.8));
            physicsEngine.add(A);
            physicsEngine.add(B);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawBodies(g);
        }

        private void drawBodies(Graphics graphics) {
            for (Body body : physicsEngine.getBodies()) {
                if (body.getShape() instanceof Circle) {
                    drawCircle(graphics, body);
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

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == timer) {
                for (int i = 0; i < 20; i++) {
                    physicsEngine.update();
                }
                repaint();
            }
        }
    }

}
