import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {
    ArrayList<Stage> stages = null;
    int currentStageIndex = 0;

    public Solver solver = new Solver();

    public ArrayList<Bird> birds;
    public ArrayList<Pig> pigs;
    public int numberOfBirds;
}
