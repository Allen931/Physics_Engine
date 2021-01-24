import java.io.Serializable;

public class Material implements Serializable {
    private final double density;
    private final double coefficientOfRestitution;
    private final double staticFrictionCoefficient;
    private final double dynamicFrictionCoefficient;

    public Material(double density, double coefficientOfRestitution,
                    double staticFrictionCoefficient, double dynamicFrictionCoefficient) {
        this.density = density;
        this.coefficientOfRestitution = coefficientOfRestitution;
        this.staticFrictionCoefficient = staticFrictionCoefficient;
        this.dynamicFrictionCoefficient = dynamicFrictionCoefficient;
    }

    public static final Material STANDARD =
            new Material(1, 1, 0.05, 0.04);
    public static final Material STATIC =
            new Material(0, 0.5, 0.4, 0.3);
    public static final Material WOOD =
            new Material(6, 0.7, 0.6, 0.5);
    public static final Material BIRD =
            new Material(10, 0.4, 0.6, 0.4);
    public static final Material PIG =
            new Material(5, 0.4, 0.8, 0.75);
    public static final Material ROUGH_PIG =
            new Material(5, 0.4, 5, 5);
    public static final Material IGNORE =
            new Material(0, 1, 0, 0);
    public static final Material STEEL =
            new Material(10, 0.8, 0.2, 0.1);
    public static final Material LIGHT_WEIGHT_WOOD =
            new Material(4, 0.5, 0.4, 0.3);
    public static final Material LIGHT_ROUGH_WOOD =
            new Material(4, 0.5, 1.5, 1.5);
    public static final Material HEAVY_WOOD =
            new Material(8, 0.5, 0.4, 0.3);


    public double getDensity() {
        return density;
    }

    public double getCoefficientOfRestitution() {
        return coefficientOfRestitution;
    }

    public double getStaticFrictionCoefficient() {
        return staticFrictionCoefficient;
    }

    public double getDynamicFrictionCoefficient() {
        return dynamicFrictionCoefficient;
    }
}
