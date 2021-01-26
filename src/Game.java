import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.util.ArrayList;

public class Game extends JFrame {
    public static Game instance = new Game();

    public static final int GROUND_LEVEL = 470;
    String fileName = "game.txt";

    GameState gameState;

    private Game() {
        setSize(800, 500);
        setTitle("Angry Birds");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        gameState = new GameState();
        setLocationRelativeTo(null);
    }

    private void changePanel(JPanel newPanel) {
        getContentPane().removeAll();
        getContentPane().add(newPanel);
        setVisible(true);
        setResizable(false);
    }

    public void toBeginningPanel() {
        changePanel(new BeginningPanel());
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
        setStages();
        GamePanel newPanel = new GamePanel(true);
        changePanel(newPanel);
        newPanel.timer.start();
    }

    private void nextStage() {
        gameState.currentStageIndex += 1;
        if (gameState.currentStageIndex >= gameState.stages.size()) {
            endingMessage();
            setStages();
            toBeginningPanel();
            return;
        }

        GamePanel newPanel = new GamePanel(true);
        changePanel(newPanel);
        saveGame();
        newPanel.timer.start();
    }

    private void setStages() {
        gameState.stages = new ArrayList<>();
        gameState.currentStageIndex = 0;
        gameState.stages.add(StageMaker.makeStage1());
        gameState.stages.add(StageMaker.makeStage2());
        gameState.stages.add(StageMaker.makeStage3());
        gameState.stages.add(StageMaker.makeStage4());
        gameState.stages.add(StageMaker.makeStage5());
    }

    private void loadGame() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
            gameState = (GameState) inputStream.readObject();
            inputStream.close();

            hint();

            GamePanel newPanel = new GamePanel(false);
            changePanel(newPanel);
            newPanel.timer.start();
        } catch (EOFException | FileNotFoundException e) {
            warning();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveGame() {
        ObjectOutputStream outputStream;
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
            outputStream.writeObject(gameState);
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

    public class GamePanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener, KeyListener {
        private final Vector initialBirdPosition = new Vector(125, 350);
        Timer timer;
        Timer victoryTimer = null;

        boolean isDragging = false;
        Vector mousePosition = null;
        Vector birdPosition = null;
        Vector birdDragged = Solver.ZERO;
        Vector birdSpeed = Solver.ZERO;

        public GamePanel(boolean isNewGame) {
            setBackground(Color.WHITE);

            timer = new Timer(16, this);

            if (isNewGame) {
                constructNewGame();
            }

            setFocusable(true);
            addKeyListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        private void constructNewGame() {
            gameState.birds = new ArrayList<>();
            gameState.pigs = new ArrayList<>();
            gameState.numberOfBirds = 0;

            gameState.solver.resetSolver(gameState.stages.get(gameState.currentStageIndex));
            for (Body body : gameState.solver.getBodies()) {
                if (body instanceof Pig) {
                    gameState.pigs.add((Pig) body);
                }
            }
            setGround();
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
                for (int i = 0; i < 32; i++) {
                    gameState.solver.update();
                }

                ArrayList<Bird> birdsToBeRemoved = new ArrayList<>();
                for (Bird bird : gameState.birds) {
                    if (!bird.isAlive()) {
                        birdsToBeRemoved.add(bird);
                    }
                }
                gameState.birds.removeAll(birdsToBeRemoved);

                ArrayList<Pig> pigsToBeRemoved = new ArrayList<>();
                for (Pig pig: gameState.pigs) {
                    if (!pig.isAlive()) {
                        pigsToBeRemoved.add(pig);
                    }
                }
                gameState.pigs.removeAll(pigsToBeRemoved);

                if (checkVictory() && victoryTimer == null) {
                    victoryTimer = new Timer(2000, this);
                    victoryTimer.addActionListener(e1 -> {
                        victoryTimer.stop();
                        timer.stop();
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
            if (gameState.numberOfBirds == 1) {
                message = "Clear Stage With ONE Bird!";
            } else {
                message = "Clear Stage!\n(" + gameState.numberOfBirds + " birds used in Stage "+
                        (gameState.currentStageIndex + 1) +")\n" + "The game will be saved automatically.";
            }
            JOptionPane.showMessageDialog(null, message, "CLEAR", JOptionPane.INFORMATION_MESSAGE);
        }

        private boolean checkVictory() {
            return gameState.pigs.size() == 0;
        }

        private void drawBodies(Graphics graphics) {
            for (Body body : gameState.solver.getBodies()) {
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

            if (!isDragging) {
                birdPosition = initialBirdPosition;
            } else {
//                Graphics2D graphics2D = (Graphics2D) graphics;
//                graphics2D.setStroke(new BasicStroke(3));
//                graphics2D.setColor(Color.PINK);
//                graphics2D.drawLine((int) birdPosition.getX(), (int) birdPosition.getY(),
//                        (int) initialBirdPosition.getX(), (int) initialBirdPosition.getY());
//                graphics2D.setStroke(new BasicStroke(1));

                graphics.setColor(Color.PINK);
                for (double time = 0; time < 6; time += 0.01) {
                    int x = (int) (birdPosition.getX() + birdSpeed.getX() * time);
                    int y = (int) (birdPosition.getY() + birdSpeed.getY() * time
                            + Solver.GRAVITY.getY() * time * time / 2.0);

                    if (x < 0 || x > 800) {
                        break;
                    }

                    int size = (int) Math.round(5 - time * 0.5);
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
            gameState.solver.add(ground);
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
            if (isDragging && gameState.birds.size() < 5) {
                Bird bird = BodyFactory.createBird(birdPosition);
                bird.setVelocity(birdSpeed);
                gameState.birds.add(bird);
                gameState.solver.add(bird);
                gameState.numberOfBirds += 1;
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
                birdDragged = initialBirdPosition.subtract(mousePosition);
                double draggedDistanceSquare = birdDragged.lengthSquare();

                if (draggedDistanceSquare >= 100 * 100) {
                    birdDragged = birdDragged.toUnitVector().multiply(100);
                }

                birdPosition = initialBirdPosition.subtract(birdDragged);
                birdSpeed = birdDragged.multiply(10);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }


}
