import java.io.*;

public class Game implements Serializable {
    String fileName = "game.txt";
    boolean shouldBeSaved = false;

    Solver solver;

    public Game() {

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

    private void loadGame() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
            Game savedGame = (Game) inputStream.readObject();
            inputStream.close();
//            rooms = savedGame.rooms;
//            hallWays = savedGame.hallWays;
//            world = savedGame.world;
//            random = savedGame.random;
//            player = savedGame.player;
//            door = savedGame.door;
        } catch (FileNotFoundException e) {
            System.err.println("Cannot found saved game.");
//            quitGame();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void quitGame() {
        if (shouldBeSaved) {
            saveGame();
        }
        System.exit(0);
    }
}
