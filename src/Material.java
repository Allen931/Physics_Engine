public class Material {
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
            new Material(1, 1, 0.0005, 0.00045);
    public static final Material STATIC =
            new Material(0, 0.5, 0.0005, 0.00045);

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
