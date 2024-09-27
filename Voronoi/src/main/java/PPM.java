

import jakarta.annotation.Nonnull;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

// a simple implementation of a PPM (Portable Pixmap) file.
public class PPM {
    @Nonnull
    public static byte[] generatePpmHeader(final int width, final int height) {
        return String.format("P6\n%d %d 255\n", width, height).getBytes(StandardCharsets.US_ASCII);
    }

    public static void createPpmFile(
            @Nonnull final String filename,
            @Nonnull final byte[] imageData,
            final int width,
            final int height
    ) throws IOException {
        final byte[] header = generatePpmHeader(width, height);
        try (final BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filename))) {
            outputStream.write(header);
            outputStream.write(imageData);

            // Flush the buffer to ensure all data is written
            outputStream.flush();
        } catch (final IOException e) {
            throw new IOException("Error creating PPM file: " + filename, e);
        }
    }

}
