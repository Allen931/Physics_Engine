import javax.swing.*;
import java.awt.*;

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

    public class Panel extends JPanel {
        PhysicsEngine physicsEngine = new PhysicsEngine();

        public Panel() {
            setBackground(Color.white);

        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }

}
