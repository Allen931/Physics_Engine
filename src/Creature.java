import javax.swing.*;

public class Creature extends Body {
    ImageIcon imageIcon;
    double healthPoint;

    public Creature(Vector position, Shape shape, Material material, ImageIcon imageIcon) {
        super(position, shape, material);
        this.imageIcon = imageIcon;
    }

    @Override
    public boolean isAlive() {
        if (position.getY() > 500) {
            return false;
        }
        return healthPoint > 0;
    }
}

class Bird extends Creature {
    public static String filename = "bird.jpg";
    public static ImageIcon imageIcon = new ImageIcon(filename);

    public Bird(Vector position) {
        super(position, new Circle(imageIcon.getIconWidth() / 2.0), Material.BIRD, imageIcon);
        healthPoint = 5000;
    }

    public void loseHP(boolean isSleeping) {
        if (isSleeping) {
            healthPoint -= 1;
        } else {
            healthPoint = 5000;
        }
    }
}

class Pig extends Creature {
    public static String filename = "pig.png";
    public static ImageIcon imageIcon = new ImageIcon(filename);

    public Pig(Vector position) {
        super(position, new Circle(imageIcon.getIconWidth() / 2.0), Material.PIG, imageIcon);
        healthPoint = 3000000;
    }

    public void loseHP(Vector impulse) {
        double damage = impulse.length();
//        System.out.println(damage);
        if (damage > 100000) {
            healthPoint -= damage;
        }
    }

}

class RoughPig extends Pig {
    public RoughPig(Vector position) {
        super(position);
        material = Material.ROUGH_PIG;
    }
}
