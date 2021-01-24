import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.util.ArrayList;

public class Game extends JFrame implements Serializable {
    public static final int GROUND_LEVEL = 470;
    String fileName = "game.txt";
    JPanel currentPanel;
    ArrayList<Stage> stages = null;
    int currentStageIndex = 0;

    public Game() {
        setSize(800, 500);
        setTitle("HAngry Birds");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        currentPanel = new BeginningPanel();
        getContentPane().add(currentPanel);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    private void toBeginningPanel() {
        currentPanel = new BeginningPanel();
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
                "You may press \"q\" or \"escape\" to save and return to the beginning page.\n" +
                        "You may have 5 launched birds in the same time.",
                "Hint", JOptionPane.INFORMATION_MESSAGE);
    }

    private void warning() {
        JOptionPane.showMessageDialog(null,
                "Cannot found saved game.",
                "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void endingMessage() {
        JOptionPane.showMessageDialog(null,
                "Congratulation!",
                "Clear", JOptionPane.INFORMATION_MESSAGE);
    }

    private void startNewGame() {
        hint();
        stages = new ArrayList<>();
//        stages.add(StageMaker.makeTestStage());
        stages.add(StageMaker.makeStage1());
        stages.add(StageMaker.makeStage2());
        stages.add(StageMaker.makeStage3());
        stages.add(StageMaker.makeStage4());
        stages.add(StageMaker.makeStage5());
        currentStageIndex = 0;
        currentPanel = new GamePanel(stages.get(currentStageIndex));
        getContentPane().removeAll();
        getContentPane().add(currentPanel);
        setVisible(true);
        setResizable(false);
    }

    private void nextStage() {
        currentStageIndex += 1;
        if (currentStageIndex == stages.size()) {
            endingMessage();
            quitGame();
        }
        currentPanel.setVisible(false);
        currentPanel = new GamePanel(stages.get(currentStageIndex));
        getContentPane().removeAll();
        getContentPane().add(currentPanel);
        setVisible(true);
        setResizable(false);
        saveGame();
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

    public class BeginningPanel extends JPanel implements ActionListener, Serializable {
        public BeginningPanel() {
            setBackground(Color.WHITE);

            JLabel title = new JLabel("HAngry Birds", JLabel.CENTER);
            title.setFont(new Font("Dialog", Font.BOLD, 50));
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
        public Solver solver = new Solver();
        public Timer timer;
        public Timer victoryTimer = null;
        public final Vector initialBirdPosition = new Vector(125, 350);
        public Vector mousePosition = null;
        public boolean isDragging = false;

        public ArrayList<Bird> birds = new ArrayList<>();
        public ArrayList<Pig> pigs = new ArrayList<>();
        public int numberOfBirds = 0;

        public GamePanel(Stage stage) {
            setBackground(Color.WHITE);

            solver.resetSolver(stage);
            for (Body body : solver.getBodies()) {
                if (body instanceof Pig) {
                    pigs.add((Pig) body);
                }
            }
            setGround();

            setFocusable(true);
            addKeyListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);

            timer = new Timer(16, this);
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            drawBodies(g);
            drawBird(g);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == timer) {
                requestFocusInWindow();
                for (int i = 0; i < 16; i++) {
                    solver.update();
                }

                ArrayList<Bird> birdsToBeRemoved = new ArrayList<>();
                for (Bird bird : birds) {
                    if (!bird.isAlive()) {
                        birdsToBeRemoved.add(bird);
                    }
                }
                birds.removeAll(birdsToBeRemoved);

                ArrayList<Pig> pigsToBeRemoved = new ArrayList<>();
                for (Pig pig: pigs) {
                    if (!pig.isAlive()) {
                        pigsToBeRemoved.add(pig);
                    }
                }
                pigs.removeAll(pigsToBeRemoved);

                if (checkVictory() && victoryTimer == null) {
                    victoryTimer = new Timer(2000, this);
                    victoryTimer.addActionListener(e1 -> {
                        victoryTimer.stop();
                        winMessage();
                        setVisible(false);
                        nextStage();
                    });
                    victoryTimer.setRepeats(false);
                    victoryTimer.start();
                }

                repaint();
            }
        }

        private void winMessage() {
            String message;
            if (numberOfBirds == 1) {
                message = "Clear Stage With ONE Bird!";
            } else {
                message = "Clear Stage!\n(" + numberOfBirds + " birds used in Stage "+
                        (currentStageIndex + 1) +")\n" + "The game will be saved automatically.";
            }
            JOptionPane.showMessageDialog(null, message, "CLEAR", JOptionPane.INFORMATION_MESSAGE);
        }

        private boolean checkVictory() {
            return pigs.size() == 0;
        }

        private void drawBodies(Graphics graphics) {
            for (Body body : solver.getBodies()) {
                if (body instanceof Creature) {
                    drawCreature(graphics, (Creature) body);
                } else if (body.getShape() instanceof Circle) {
                    drawCircle(graphics, body);
                } else if (body.getShape() instanceof Polygon) {
                    drawPolygon(graphics, body);
                }
            }
        }

        private void drawCreature(Graphics graphics, Creature creature) {
            double radius = ((Circle) creature.getShape()).getRadius();
            double positionX = creature.position.getX() - radius;
            double positionY = creature.position.getY() - radius;

            AffineTransform transform = new AffineTransform();
            transform.translate(positionX, positionY);
            transform.rotate(creature.orientation, radius, radius);

            Graphics2D graphics2D = (Graphics2D) graphics;
            java.awt.Shape originalClip = graphics2D.getClip();
            graphics2D.setClip(new Ellipse2D.Double(positionX, positionY,
                    creature.imageIcon.getIconWidth(),
                    creature.imageIcon.getIconWidth()));
            graphics2D.drawImage(creature.imageIcon.getImage(), transform, null);
            graphics2D.setClip(originalClip);
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
            if (!isDragging) {
                birdPosition = initialBirdPosition;
            } else {
                Vector birdDragged = initialBirdPosition.subtract(mousePosition);
                double draggedDistanceSquare = birdDragged.lengthSquare();

                if (draggedDistanceSquare >= 100 * 100) {
                    birdDragged = birdDragged.toUnitVector().multiply(100);
                }

                birdPosition = initialBirdPosition.subtract(birdDragged);

                Vector speed = birdDragged.multiply(10);

                graphics.drawLine((int) birdPosition.getX(), (int) birdPosition.getY(),
                        (int) initialBirdPosition.getX(), (int) initialBirdPosition.getY());

                for (double time = 0; time < 10; time += 0.05) {
                    int x = (int) (birdPosition.getX() + speed.getX() * time);
                    int y = (int) (birdPosition.getY() + speed.getY() * time
                            + Solver.GRAVITY.getY() * time * time / 2.0);

                    if (x < 0 || x > 800) {
                        break;
                    }

                    graphics.setColor(Color.PINK);
                    int size = (int) Math.round(7 - time * 0.6);
                    graphics.fillOval((int) Math.round(x - size / 2.0), (int) Math.round(y - size / 2.0), size, size);
                }
            }

            graphics.setColor(Color.BLACK);
            Bird bird = BodyFactory.createBird(birdPosition);
            drawCreature(graphics, bird);

            double standHeight = 500 - (initialBirdPosition.getY() + Bird.imageIcon.getIconHeight() / 2.0);
            Body stand = BodyFactory.createRectangle(initialBirdPosition.getX(), 500 - standHeight / 2.0,
                    30, standHeight, Material.IGNORE);
            drawPolygon(graphics, stand);
        }

        public void setGround() {
            Vector[] vertices = {new Vector(-400, -30), new Vector(400, -30),
                    new Vector(-400, 100), new Vector(400, 100)};
            Body ground = BodyFactory.createPolygon(400, 500, vertices, Material.STATIC);
            solver.add(ground);
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
                setVisible(true);
                toBeginningPanel();
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
                    <= Math.pow(Bird.imageIcon.getIconWidth() / 2.0, 2)) {
                isDragging = true;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (isDragging && birds.size() < 5) {
                Vector birdDragged = initialBirdPosition.subtract(mousePosition);
                double draggedDistanceSquare = birdDragged.lengthSquare();

                if (draggedDistanceSquare >= 100 * 100) {
                    birdDragged = birdDragged.toUnitVector().multiply(100);
                }

                Vector birdPosition = initialBirdPosition.subtract(birdDragged);

                Bird bird = BodyFactory.createBird(birdPosition);
                bird.setVelocity(birdDragged.multiply(10));
                birds.add(bird);
                solver.add(bird);
                numberOfBirds += 1;
            }
            isDragging = false;
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
