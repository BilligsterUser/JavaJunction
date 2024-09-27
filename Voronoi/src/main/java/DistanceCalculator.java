import static java.lang.Math.*;

public class DistanceCalculator {
    public static double minkowskiDistance(final Vector2 p , final Vector2 p1 , final int o) {
        double d = 0;
        d += Math.pow(abs(p.x - p1.x) , o);
        d += Math.pow(abs(p.y - p1.y) , o);
        return Math.pow(d , 1.0 / o);
    }

    public static double manhattanDistance(final Vector2 p , final Vector2 p1) {
        return abs(p1.x - p.x) + abs(p1.y - p.y);
    }

    public static double euclideanDistance(final Vector2 p , final Vector2 p1) {
        final double dx = p.x - p1.x;
        final double dy = p.y - p1.y;
        return sqrt(dx * dx + dy * dy);
    }

    public static double chebyshevDistance(final Vector2 p , final Vector2 p1) {
        final double max = abs(p.x - p1.x);
        final double distance = abs(p.y - p1.y);
        return max(distance , max);
    }

    public static void main(final String[] args) {
        final Vector2 p = new Vector2(1 , 2);
        final Vector2 p1 = new Vector2(3 , 4);
        System.out.println("Minkowski Distance: " + minkowskiDistance(p , p1 , 3));
        System.out.println("Manhattan Distance: " + manhattanDistance(p , p1));
        System.out.println("Euclidean Distance: " + euclideanDistance(p , p1));
        System.out.println("Chebyshev Distance: " + chebyshevDistance(p , p1));
        System.out.println("Distance.sqrDist Distance: " + Distance.sqrDist(p , p1));
    }

    public static double calculateDistance(final Vector2 p , final Vector2 p1 , final DistanceMetric metric) {
        final double dx = p.x - p1.x;
        final double dy = p.y - p1.y;

        switch (metric) {
            case MINKOWSKI:
                final int o = 3; // or any other value for the order
                final double d = Math.pow(Math.abs(dx) , o) + Math.pow(Math.abs(dy) , o);
                return Math.pow(d , 1.0 / o);
            case MANHATTAN:
                return Math.abs(dx) + Math.abs(dy);
            case EUCLIDEAN:
                final double squaredDistance = dx * dx + dy * dy;
                return Math.sqrt(squaredDistance);
            case CHEBYSHEV:
                return Math.max(Math.abs(dx) , Math.abs(dy));
            default:
                throw new IllegalArgumentException("Invalid distance metric");
        }
    }

    public enum DistanceMetric {
        MINKOWSKI,
        MANHATTAN,
        EUCLIDEAN,
        CHEBYSHEV
    }
}
