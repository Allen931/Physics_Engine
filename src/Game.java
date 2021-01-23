import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.util.ArrayList;

public class Game extends JFrame implements Serializable {
    String fileName = "game.txt";
    JPanel currentPanel;

    public Game() {
        setSize(800, 500);
        setTitle("HAngry Birds");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        currentPanel = new StartPanel();
        getContentPane().add(currentPanel);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    private void toStartPanel() {
        currentPanel = new StartPanel();
        getContentPane().removeAll();
        getContentPane().add(currentPanel);
        setVisible(true);
        setResizable(false);
    }

    private void toGamePanel(String request) {
        switch (request) {
            case "Start Game":
                startNewGame();
                break;
            case "Load Game":
                loadGame();
                break;
            case "Quit":
                quitGame();
                break;
        }
    }

    private void hint() {
        JOptionPane.showMessageDialog(null,
                "You may press \"q\" or \"escape\" to save and return to start window.",
                "Hint", JOptionPane.WARNING_MESSAGE);
    }

    private void warning() {
        JOptionPane.showMessageDialog(null,
                "Cannot found saved game.",
                "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void startNewGame() {
        hint();
        currentPanel = new GamePanel();
        getContentPane().removeAll();
        getContentPane().add(currentPanel);
        setVisible(true);
        setResizable(false);
    }

    private void loadGame() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
            Game savedGame = (Game) inputStream.readObject();
            inputStream.close();

            hint();

            currentPanel = savedGame.currentPanel;
            getContentPane().removeAll();
            getContentPane().add(currentPanel);
            setVisible(true);
            setResizable(false);
            ((GamePanel) currentPanel).timer.start();
        } catch (FileNotFoundException e) {
            warning();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveGame() {
        ObjectOutputStream outputStream;
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
            outputStream.writeObject(this);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void quitGame() {
        System.exit(0);
    }

    public class StartPanel extends JPanel implements ActionListener {
        public StartPanel() {
            setBackground(Color.WHITE);

            JLabel title = new JLabel("HAngry Birds", JLabel.CENTER);
            title.setFont(new Font("Dialog", 1, 50));
            title.setSize(800, 200);

            add(Box.createRigidArea(new Dimension(0, 250)));
            add(title);
            add(Box.createRigidArea(new Dimension(0, 30)));

            JButton startGame = new JButton("Start Game");
            startGame.addActionListener(this);

            JButton loadGame = new JButton("Load Game");
            loadGame.addActionListener(this);

            JButton quit = new JButton("Quit");
            quit.addActionListener(this);

            add(Box.createRigidArea(new Dimension(800, 0)));
            add(startGame);
            add(loadGame);
            add(quit);
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            String request = ((JButton) e.getSource()).getText();
            toGamePanel(request);
        }
    }

    public class GamePanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener, KeyListener, Serializable {
        Solver solver = new Solver();
        Timer timer;
        Vector initialBirdPosition = null;
        Vector mousePosition = null;
        boolean isDragging = false;

        ArrayList<Bird> birds = new ArrayList<>();
        ArrayList<Pig> pigs = new ArrayList<>();
        int numberOfBirds = 0;

        Image birdImage;
        Image pigImage;

        public GamePanel() {
            setBackground(Color.WHITE);
            setGround();
            Vector[] vertices3 = new Vector[]{new Vector(10, -10), new Vector(10, 10),
                    new Vector(-10, 10), new Vector(-10, -10)};
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    Body polygon = BodyFactory.createPolygon(60 * j + 100, 40 * i, vertices3, Material.STANDARD);
                    polygon.setVelocity(new Vector(50, 200));
                    polygon.setAcceleration(new Vector(0, 200));
                    polygon.angularVelocity = 20;
                    solver.add(polygon);
                }
            }

            for (int p = 0; p < 10; p++) {
                for (int q = 0; q < 10; q++) {
                    Body circle = BodyFactory.createCircle(60 * p + 120, 40 * q + 20, 20, Material.STANDARD);
                    circle.setVelocity(new Vector(-50, 200));
                    circle.setAcceleration(new Vector(0, 200));
                    circle.angularVelocity = 0;
                    solver.add(circle);
                }
            }
            setFocusable(true);
            addKeyListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);

            ImageIcon birdIcon = new ImageIcon("bird.jpg");
            birdImage = birdIcon.getImage();

            ImageIcon pigIcon = new ImageIcon("pig.png");
            pigImage = pigIcon.getImage();

            timer = new Timer(16, this);
            timer.start();
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
                } else if (body instanceof Creature) {
                    drawCreature((Creature) body, graphics);
                }
            }
        }

        private void drawCreature(Creature creature, Graphics graphics) {
            double radius = ((Circle) creature.getShape()).getRadius();
            double positionX = creature.position.getX() - radius;
            double positionY = creature.position.getY() - radius;

            AffineTransform transform = new AffineTransform();
            transform.translate(positionX, positionY);
            transform.rotate(creature.orientation, radius, radius);

            Graphics2D graphics2D = (Graphics2D) graphics;
//            java.awt.Shape origClip = g2.getClip();
            graphics2D.setClip(new Ellipse2D.Double(positionX, positionY, radius, radius));
            graphics2D.drawImage(creature.image, (int) Math.round(positionX),
                    (int) Math.round(positionY), (int) Math.round(radius),
                    (int) Math.round(radius), null);
//            g2.setClip(origClip);
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

        private void drawBird(Graphics graphics) {
            Vector birdPosition;
            Vector birdDragged = null;
            if (!isDragging) {
                birdPosition = initialBirdPosition;
            } else {
                birdDragged = initialBirdPosition.subtract(mousePosition);
                birdPosition = getBirdPosition(birdDragged);
            }

            Bird bird = BodyFactory.createBird(birdPosition, birdImage, birdImage.getWidth(this) / 2.0);
            drawCreature(bird, graphics);



        }

        private Vector getBirdPosition(Vector birdDragged) {
            double draggedDistanceSquare = birdDragged.lengthSquare();

            if (draggedDistanceSquare >= 120 * 120) {
                birdDragged = birdDragged.toUnitVector().multiply(120);

            }
            return initialBirdPosition.subtract(birdDragged);
        }

        public void setGround() {
            Vector[] vertices = {new Vector(0, 450), new Vector(800, 450),
                    new Vector(0, 600), new Vector(800, 600)};
            Body ground = BodyFactory.createPolygon(getWidth() / 2.0, getHeight(), vertices, Material.STATIC);
            solver.add(ground);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == timer) {
                requestFocusInWindow();
                for (int i = 0; i < 16; i++) {
                    solver.update();
                }
                repaint();
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_Q || key == KeyEvent.VK_ESCAPE) {
                timer.stop();
                saveGame();
                toStartPanel();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            mousePosition = new Vector(e.getX(), e.getY());
            if (mousePosition.squareOfDistance(initialBirdPosition)
                    <= Math.pow(birdImage.getWidth(this) / 2.0, 2)) {
                isDragging = true;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (isDragging && birds.size() < 5) {
                isDragging = false;
                Vector birdDragged = initialBirdPosition.subtract(mousePosition);
                Vector birdPosition = getBirdPosition(birdDragged);

                Bird bird = BodyFactory.createBird(birdPosition,
                        birdImage, birdImage.getWidth(this) / 2.0);
                bird.setVelocity(birdDragged.multiply(10));
                birds.add(bird);
                solver.add(bird);
                numberOfBirds += 1;
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (isDragging) {
                mousePosition = new Vector(e.getX(), e.getY());
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }


}
