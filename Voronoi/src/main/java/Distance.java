public class Distance {
    public static int sqrDist(final int x1 , final int y1 , final int x2 , final int y2) {
        final int dx = x1 - x2;
        final int dy = y1 - y2;
        return dx * dx + dy * dy;
    }

    public static float sqrDist(final float x1 , final float y1 , final float x2 , final float y2) {
        final float dx = x1 - x2;
        final float dy = y1 - y2;
        return dx * dx + dy * dy;
    }

    public static double sqrDist(final double x1 , final double y1 , final double x2 , final double y2) {
        final double dx = x1 - x2;
        final double dy = y1 - y2;
        return dx * dx + dy * dy;
    }

    public static float sqrDist(final Vector2 x , final Vector2 y) {
        return sqrDist(x.x , x.y , y.x , y.y);
    }
}


