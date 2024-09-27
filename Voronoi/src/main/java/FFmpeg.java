


import jakarta.annotation.Nonnull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class FFmpeg {
    private final int width;
    private final int height;
    private Process process;
    private OutputStream outputStream;

    public FFmpeg(final int width, final int height) throws IOException {
        this.width = width;
        this.height = height;                                                                // color (RGBA format)
//        process = convertRgbToMp4();
        // Get the output stream of the process (stdin of FFmpeg)
//
    }

    public void close() throws IOException, InterruptedException {
        outputStream.close();

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        process.waitFor();
        process.destroy();
    }

    @Nonnull
    private Process _ffmpeg(final String... stringArray) throws IOException {
        final ProcessBuilder processBuilder = new ProcessBuilder(stringArray);
        processBuilder.redirectErrorStream(true); // Merge stderr with stdout
        process = processBuilder.start();
        outputStream = process.getOutputStream();
        return process;
    }

    @Nonnull
    public Process convertRgbToMp4(final int frameRate, final String outputFilePath) throws IOException {
        return _ffmpeg("ffmpeg",
                "-f", "rawvideo",
                "-pix_fmt", "rgba",
                "-s", String.format("%dx%d", width, height), // Replace WIDTH and HEIGHT with your frame dimensions
                "-r", String.valueOf(frameRate), // Set the frame rate
                "-i", "-", // Read input from stdin
                "-c:v", "libx264",
                "-crf", "23", // Set constant rate factor (0-51, where lower is better quality but larger file
                // size)
                outputFilePath);
    }

    @Nonnull
    public Process convertRgbToGIF(final int frameRate, final String outputFilePath) throws IOException {
        return _ffmpeg("ffmpeg",
//                "-y",
                "-f", "rawvideo",
                "-vcodec", "rawvideo",
                "-pix_fmt", "rgba",
                "-s", String.format("%dx%d", width, height),
                "-r", String.valueOf(frameRate),
                "-i", "-", // Read input from stdin
                "-filter_complex", "[0:v] split [a][b];[a] palettegen [p];[b][p] paletteuse",
                outputFilePath);
    }

    public void provideRawData(final byte[] imgBytes) throws IOException {
        if (outputStream != null) {
            outputStream.write(imgBytes);
        }
        // System.out.println("Frame " + framesCount);
    }
}
