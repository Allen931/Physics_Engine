public class Creature extends Body{
    boolean isAlive = true;
    double healthPoint;

    public Creature(Vector position, Shape shape, Material material, double healthPoint) {
        super(position, shape, material);
        this.healthPoint = healthPoint;
    }
}
