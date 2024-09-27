public class Vector2 {
    public float x;
    public float y;

    Vector2(final int x , final int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(final float x , final float y) {
        this.x = x;
        this.y = y;
    }

    public static float sqrDist(final Vector2 x , final Vector2 y) {
        return Distance.sqrDist(x , y);
    }

    public float sqrDist(final float x , final float y) {
        return Distance.sqrDist(this.x , this.y , x , y);
    }

    public float sqrDist(final Vector2 y) {
        return Distance.sqrDist(this , y);
    }

}
