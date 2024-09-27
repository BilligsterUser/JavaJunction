public class SeedMarker {
    public final int color;
    public int x;
    public int y;
    public Vector2 velocity;

    public SeedMarker(final int x , final int y , final int colors) {
        this.x = x;
        this.y = y;
        this.color = colors;
    }

    public SeedMarker(final int x , final int y , final int colors , final Vector2 velocity) {
        this.x = x;
        this.y = y;
        this.color = colors;
        this.velocity = velocity;
    }

    public int sqrDist(final int x , final int y) {
        return Distance.sqrDist(this.x , this.y , x , y);
    }
}
