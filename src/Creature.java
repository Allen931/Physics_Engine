import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Creature extends Body {
    Image image;
    double healthPoint;

    public Creature(Vector position, Shape shape, Material material, Image image) {
        super(position, shape, material);
        this.image = image;
    }

    @Override
    public boolean isAlive() {
        return healthPoint > 0;
    }

}

class Bird extends Creature {
    Timer timer;

    public Bird(Vector position, Image image, double radius) {
        super(position, new Circle(radius), Material.BIRD, image);
        healthPoint = 5000;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                healthPoint = 0;
            }
        }, 15000);
    }

    public void loseHP(boolean isSleeping) {
        if (isSleeping) {
            healthPoint -= 1;
        }
    }
}

class Pig extends Creature {

    public Pig(Vector position, Image image, double radius) {
        super(position, new Circle(radius), Material.PIG, image);
        healthPoint = Math.pow(100000, 2);
    }

    public void loseHP(Vector impulse) {
        healthPoint -= impulse.lengthSquare();
    }

}
