package com.jamesio.app;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.rules.TemporaryFolder;

public class WatermarkTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final Path workingDir = Path.of("", "src/test/resources");

    private final String watermarkPath = "watermark.png";

    @Test
    public void watermarkImage(@TempDir Path tempDir) throws IOException {
        String destination = tempDir.toFile().getAbsolutePath();
        Path file = this.workingDir.resolve("test.png");
        String source = file.toFile().getAbsolutePath();
        Path watermarkFile = this.workingDir.resolve(watermarkPath);
        String watermark = watermarkFile.toFile().getAbsolutePath();
        String output = String.format("%s/%s_watermarked.jpg", destination, "test");
        WatermarkClient wClient = new WatermarkClient(source, output, watermark);
        wClient.watermarkFile();
    }

    @Test
    public void watermarkVideo(@TempDir Path tempDir) throws IOException {
        String destination = tempDir.toFile().getAbsolutePath();
        Path file = this.workingDir.resolve("test.mp4");
        String source = file.toFile().getAbsolutePath();
        Path watermarkFile = this.workingDir.resolve(watermarkPath);
        String watermark = watermarkFile.toFile().getAbsolutePath();
        String output = String.format("%s/%s_watermarked.mp4", destination, "test");
        WatermarkClient wClient = new WatermarkClient(source, output, watermark);
        wClient.watermarkFile();
    }
}
