package com.jamesio.app;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Watermarker {

    public static void executeWatermarks(List<String> sources, String destination, String watermark) {
        ExecutorService service = Executors.newFixedThreadPool(sources.size());
        System.out.println("Watermarking Files...");
        sources.forEach(source -> {
            System.out.println(
                    String.format("Execution source: %s on Thread: %s", source, Thread.currentThread().getName()));
            String fileName = source.substring(source.lastIndexOf('/') + 1);
            int delimiterNdx = fileName.lastIndexOf('.');
            String ext = fileName.substring(delimiterNdx + 1);
            String fileNameNoExt = fileName.substring(0, delimiterNdx);
            String output = String.format("%s/%s_watermarked.%s", destination, fileNameNoExt, ext);
            WatermarkClient watermarkClient = new WatermarkClient(source, output, watermark);
            watermarkClient.watermarkFile();
            System.out.println(String.format("Total duration: %s ms", watermarkClient.getDuration()));
        });
        System.out.println(String.format("All %s files successfully watermarked.", sources.size()));
        service.shutdown();
    }
}
