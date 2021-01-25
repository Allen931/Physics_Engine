import javax.swing.*;

public class Creature extends Body {
    ImageIcon imageIcon;

    public Creature(Vector position, Shape shape, Material material, ImageIcon imageIcon) {
        super(position, shape, material);
        this.imageIcon = imageIcon;
    }
}

class Bird extends Creature {
    public static String filename = "bird.jpg";
    public static ImageIcon imageIcon = new ImageIcon(filename);

    public Bird(Vector position) {
        super(position, new Circle(imageIcon.getIconWidth() / 2.0), Material.BIRD, imageIcon);
        healthPoint = 3000;
    }

    public void loseHP(boolean isSleeping) {
        // die if continuing sleeping for more than 1.5s
        if (isSleeping) {
            healthPoint -= 1;
        } else {
            healthPoint = 3000;
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
}

class RoughPig extends Pig {
    public RoughPig(Vector position) {
        super(position);
        material = Material.ROUGH_PIG;
    }
}
