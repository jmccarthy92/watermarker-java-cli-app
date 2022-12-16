package com.jamesio.app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileLoader {
    private static FileLoader fileLoader = null;

    private String tmpDir;

    private FileLoader() throws IOException {
        this.tmpDir = this.getTempDir();
    }

    private String getTempDir() throws IOException {
        return Files.createTempDirectory("tmp").toFile().getAbsolutePath();
    }

    public static FileLoader getLoader() throws IOException {
        if (fileLoader == null)
            fileLoader = new FileLoader();
        return fileLoader;
    }

    public List<String> downloadFiles(List<String> sources) {
        ExecutorService service = Executors.newFixedThreadPool(sources.size());
        List<String> downloadedSources = Collections.synchronizedList(sources);
        sources.forEach(source -> {
            service.execute(() -> {
                try {
                    downloadedSources.add(downloadFile(source));
                } catch (IOException e) {
                    System.out.println("Error downloading source file...");
                    throw new UncheckedIOException(e);
                }
            });
        });
        service.shutdown();
        return downloadedSources;
    }

    public String downloadFile(String uri) throws IOException {
        String segments[] = uri.split("/");
        String tmpLocalPath = tmpDir + segments[segments.length - 1];
        URL url = new URL(uri);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(tmpLocalPath);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
        return tmpLocalPath;
    }
}
