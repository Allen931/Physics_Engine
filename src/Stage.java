import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Stage implements Serializable {
    ArrayList<Body> bodies = new ArrayList<>();

    public Stage(List<Body> bodies) {
        this.bodies.addAll(bodies);
    }

}
