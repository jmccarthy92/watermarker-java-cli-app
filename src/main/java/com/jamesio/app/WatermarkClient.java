package com.jamesio.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegProgress;
import com.github.kokorin.jaffree.ffmpeg.NullOutput;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

public class WatermarkClient {
    private String source;
    private String watermark;
    private String destination;

    private final AtomicLong duration;

    public long getDuration() {
        return duration.get();
    }

    public WatermarkClient(String source, String destination, String watermark) {
        this.source = source;
        this.destination = destination;
        this.watermark = watermark;
        this.duration = new AtomicLong();
    }

    public void watermarkFile() {
        this.setDuration();
        FFmpeg
                .atPath()
                .addInput(UrlInput.fromUrl(source))
                .addInput(UrlInput.fromUrl(watermark))
                .setComplexFilter(
                        // Scale watermark to media aspect ratio & overlay the watermark over the media.
                        "[1:v][0:v]scale2ref=w=iw/2:h=ow/mdar[logo1][base];[base][logo1]overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2")
                .addOutput(
                        UrlOutput.toUrl(destination)
                                .addArguments("-preset", "ultrafast")
                                .setDuration(30, TimeUnit.SECONDS))
                .setProgressListener((FFmpegProgress progress) -> {
                    double percentage = Math.round(100. * progress.getTimeMillis() / duration.get());
                    System.out
                            .println("Source: " + source + " Progress: " + percentage + '%');
                })
                .setOverwriteOutput(true)
                // .setLogLevel(LogLevel.DEBUG)
                .execute();
    }

    private void setDuration() {
        FFmpeg.atPath()
                .addInput(UrlInput.fromUrl(source))
                .setOverwriteOutput(true)
                .addOutput(new NullOutput())
                .setProgressListener((FFmpegProgress progress) -> {
                    duration.set(progress.getTimeMillis());
                })
                .execute();

    }

    public void optimizeFileSize() throws IOException {
        Path destinationPath = Paths.get(destination);
        Path pathToOptimized = destinationPath.resolveSibling("optimized-" +
                destinationPath.getFileName());
        FFmpeg.atPath()
                .addInput(UrlInput.fromPath(destinationPath))
                .addOutput(UrlOutput.toPath(pathToOptimized))
                .execute();
        Files.move(pathToOptimized, destinationPath,
                StandardCopyOption.REPLACE_EXISTING);
    }

}