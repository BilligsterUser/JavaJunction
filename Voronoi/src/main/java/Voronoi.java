import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

public class Voronoi {
    private static final Logger logger = LoggerFactory.getLogger(Voronoi.class);
    private static final Random RANDOM = new Random();
    private final int width;
    private final int height;
    private final int seedCount;
    private final SeedMarker[] seeds;
    private final int seedRadius;
    private final int[][] image;

    public Voronoi(final int width, final int height, final int seedCount) {
        this.width = width;
        this.height = height;
        this.seedCount = seedCount;
        this.seeds = new SeedMarker[seedCount];
        image = new int[height][width];
        this.seedRadius = 5;
        generateRandomSeeds();
    }

    public Voronoi(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.seedCount = 10;
        this.seeds = new SeedMarker[seedCount];
        this.seedRadius = 5;
        image = new int[height][width];
        generateRandomSeeds();
    }

    public Voronoi(final int[][] image, final int width, final int height, final int seedCount, final SeedMarker[] seeds, final int seedRadius) {
        this.image = image;
        this.width = width;
        this.height = height;
        this.seedCount = seedCount;
        this.seeds = seeds;
        this.seedRadius = seedRadius;
    }

    private static void handleSeedCollisions(final SeedMarker[] seeds, final int radius) {
        final int n = seeds.length;
        for (int i = 0; i < n; i++) {
            final SeedMarker seed1 = seeds[i];
            for (int j = i + 1; j < n; j++) {
                final SeedMarker seed2 = seeds[j];
                final double dx = seed1.x - seed2.x;
                final double dy = seed1.y - seed2.y;
                final double distanceSquared = dx * dx + dy * dy;
                final double maxDistanceSquared = (radius * 2) * (radius * 2);

                if (distanceSquared < maxDistanceSquared) {
                    final double distance = Math.sqrt(distanceSquared);
                    swapVelocities(seed1, seed2);
                    resolveOverlap(seed1, seed2, radius, dx, dy, distance);
                }
            }
        }
    }

    private static void swapVelocities(final SeedMarker seed1, final SeedMarker seed2) {
        final Vector2 temp = seed1.velocity;
        seed1.velocity = seed2.velocity;
        seed2.velocity = temp;
    }

    private static void resolveOverlap(
            final SeedMarker seed1,
            final SeedMarker seed2,
            final int radius,
            final double dx,
            final double dy,
            final double distance
    ) {
        final double overlap = radius * 2 - distance;
        final double adjustX = (overlap * dx / distance) / 2;
        final double adjustY = (overlap * dy / distance) / 2;

        seed1.x += (int) adjustX;
        seed1.y += (int) adjustY;
        seed2.x -= (int) adjustX;
        seed2.y -= (int) adjustY;
    }

    private static float lerp(final float a, final float b, final float t) {
        return a + (b - a) * t;
    }

    public static void main(final String[] args) throws IOException, InterruptedException {
        final Voronoi voronoi = new Voronoi(1920, 1080, 15);
//        voronoi.nextFrame();
//        voronoi.saveImageAsPPM("test.ppm");
//        voronoi.nextFrame();
//        voronoi.saveImageAsPPM("test2.ppm");
        voronoi.renderVideoMode();
    }

    private static byte[] toByteArr(final int[][] buf) {
        final ByteBuffer buffer = ByteBuffer.allocate(buf.length * buf[0].length * 4);
        for (final int[] ints : buf) {
            for (final int anInt : ints) {
                final Color pixel = new Color(anInt); // Get color with alpha
                buffer.put((byte) pixel.getRed()); // red
                buffer.put((byte) pixel.getGreen()); // green
                buffer.put((byte) pixel.getBlue()); // blue
                buffer.put((byte) 255); // alpha
            }
        }
        return buffer.array();
    }

    private static void generateRandomSeeds(final int radius, final SeedMarker[] seeds, final int[][] image) {
        final int height = image.length;
        final int width = image[0].length;
        for (int i = 0; i < seeds.length; ++i) {
            final float angle = RANDOM.nextFloat() * 2 * (float) Math.PI;
            final float mag = lerp(100, 200, RANDOM.nextFloat());
            final int x;
            final int y;
            x = RANDOM.nextInt(radius * 2, height - radius * 2);
            y = RANDOM.nextInt(radius * 2, width - radius * 2);
            final Vector2 vec = new Vector2((float) Math.cos(angle) * mag, (float) Math.sin(angle) * mag);
            seeds[i] = new SeedMarker(x, y, rndColor(), vec);
        }
    }

    private static int rndColor() {
        return new Color(
                RANDOM.nextInt(255),
                RANDOM.nextInt(255),
                RANDOM.nextInt(255)
        ).getRGB();
    }

    private static void generate(final SeedMarker[] seeds, final int[][] image) {
        final int height = image.length;
        final int width = image[0].length;

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                int closestPoint = 0;
                int minDist = Integer.MAX_VALUE;
                for (int i = 0; i < seeds.length; i++) {
                    final int dist = Distance.sqrDist(seeds[i].x, seeds[i].y, x, y);
                    if (dist < minDist) {
                        minDist = dist;
                        closestPoint = i;
                    }
                }
                image[x][y] = seeds[closestPoint].color;
            }
        }
    }

    private static void renderSeedMarkers(final SeedMarker[] seeds, final int[][] image) {
        for (final SeedMarker seed : seeds) {
            fillCircle(seed.x, seed.y, 5, Color.BLACK.getRGB(), image);
        }
    }

    private static void fillCircle(final int cx, final int cy, final int radius, final int color, final int[][] image) {
        final int x0 = Math.max(0, cx - radius);
        final int y0 = Math.max(0, cy - radius);
        final int xMax = Math.min(image.length, cx + radius);
        final int yMax = Math.min(image[0].length, cy + radius);

        final int radiusSquared = radius * radius;
        for (int x = x0; x < xMax; x++) {
            for (int y = y0; y < yMax; y++) {
                final int dx = x - cx;
                final int dy = y - cy;
                if (dx * dx + dy * dy <= radiusSquared) {
                    image[x][y] = color;
                }
            }
        }
    }

    public static void fill(final int[][] image, final int color) {
        for (final int[] row : image) {
            Arrays.fill(row, color);
        }
    }

    private void logCurrentMethodName() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final String methodName = stackTrace[2].getMethodName();
        logger.info("Current method: {}", methodName);
    }

    private void updateSeedPositions(final double deltaTime) {
        for (final SeedMarker seed : seeds) {
            // Update seed position based on velocity and delta time
            seed.x += (int) (seed.velocity.x * deltaTime);
            seed.y += (int) (seed.velocity.y * deltaTime);
        }
        handleBoundaryConditions(seeds);
        handleSeedCollisions(seeds, seedRadius);
    }

    private void handleBoundaryConditions(final SeedMarker[] seeds) {
        for (final SeedMarker seed : seeds) {
            // Define boundaries for seed movement
            final int maxX = height - 1;
            final int maxY = width - 1;
            if (seed.x < seedRadius) {
                seed.x = seedRadius;
                seed.velocity.x *= -1;
            } else if (seed.x > maxX - seedRadius) {
                seed.x = maxX - seedRadius;
                seed.velocity.x *= -1;
            }
            if (seed.y < seedRadius) {
                seed.y = seedRadius;
                seed.velocity.y *= -1;
            } else if (seed.y > maxY - seedRadius) {
                seed.y = maxY - seedRadius;
                seed.velocity.y *= -1;
            }
        }
    }

    private int[][] renderFrame(final double deltaTime) {
        fill(image, Color.WHITE.getRGB());
        updateSeedPositions(deltaTime);
        generate(seeds, image);
        renderSeedMarkers(seeds, image);
        return image;
    }

    private int[][] renderFrame() {
        return renderFrame(0.1);
    }

    private void renderVideoMode() throws IOException, InterruptedException {
        final int height = image.length;
        final int width = image[0].length;
        double fps = 30.0;
        double deltaTime = 1.0 / fps;
        double prevTime = System.currentTimeMillis();
        final double duration = 30.0;
        long framesCounter = 0;
        double lastTime = System.currentTimeMillis();
        final long framesCount = Math.round(duration / deltaTime);
        final FFmpeg ffmpeg = new FFmpeg(width, height);
        ffmpeg.convertRgbToMp4((int) fps, "output.mp4");
        final FFmpeg ffmpeg2 = new FFmpeg(width, height);
        ffmpeg2.convertRgbToGIF((int) fps, "output.gif");
        for (long i = 0; i < framesCount; ++i) {
            renderFrame(deltaTime);
            try {
                ffmpeg.provideRawData(toByteArr(image));
                ffmpeg2.provideRawData(toByteArr(image));
            } catch (final IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
            final long curTime = System.currentTimeMillis();
            deltaTime = (curTime - prevTime) / 1000.0; // Convert to seconds
            prevTime = curTime;

            framesCounter++;
            final double currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= 1000) { // Calculate FPS every second
                fps = framesCounter / ((currentTime - lastTime) / 1000.0); // FPS calculation
                System.out.println("FPS: " + fps);
                framesCounter = 0; // Reset the frame count
                lastTime = currentTime; // Reset the last time
            }
            System.out.println("INFO: Rendered " + (i + 1) + "/" + framesCount + " frames - delta " + deltaTime + " -" +
                    " fps:" + fps);

//            System.out.println(seeds[0].x + " " + seeds[0].y + " " + seeds[0].velocity.x + " " + seeds[0].velocity.y);
        }
        ffmpeg.close();
        ffmpeg2.close();
        logCurrentMethodName();
        logger.info("Voronoi video rendering complete");
    }


    private void nextFrame() {
        this.renderFrame();
    }

    private void saveImageAsPPM(final String filePath) throws IllegalArgumentException, IOException {
        PPM.createPpmFile(filePath, toByteArr(renderFrame()), width, height);
    }

    private void generateRandomSeeds() {
        generateRandomSeeds(seedRadius, seeds, image);
    }
}
