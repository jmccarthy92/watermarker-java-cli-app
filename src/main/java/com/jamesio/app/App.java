package com.jamesio.app;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.cli.ParseException;

import com.jamesio.app.CommandDialog.CommandDialogField;

public class App {
    public static void main(String[] args) {
        CommandDialog dialog = new CommandDialog();
        try {
            dialog.parseOptions(args);
            if (dialog.getSources() == null)
                return;
        } catch (ParseException pe) {
            logParseException(pe);
            return;
        }
        dialog.printOptions();
        String watermark;
        List<String> sources;
        try {
            watermark = getWatermark(dialog);
        } catch (IOException e) {
            System.out.println("Error downloading watermark...");
            return;
        }
        try {
            sources = getSources(dialog);
        } catch (IOException e) {
            System.out.println("Error downloading source files...");
            return;
        }
        try {
            validateInputs(sources, watermark);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
            return;
        }
        Watermarker.executeWatermarks(sources, dialog.getDestination(), watermark);
    }

    private static String getWatermark(CommandDialog dialog) throws IOException {
        String watermark = dialog.getWatermark();
        if (dialog.hasHttpUri(CommandDialogField.WATERMARK))
            return FileLoader.getLoader().downloadFile(watermark);
        return watermark;
    }

    private static List<String> getSources(CommandDialog dialog) throws IOException {
        List<String> sources = Arrays.asList(dialog.getSources());
        List<String> allSources = filterLocalSources(sources);
        FileLoader fileLoader = FileLoader.getLoader();
        if (dialog.hasHttpUri(CommandDialogField.SOURCES)) {
            List<String> downloadedSources = fileLoader.downloadFiles(filterHttpSources(sources));
            allSources.addAll(downloadedSources);
        }
        return allSources;
    }

    private static void logParseException(ParseException pe) {
        String message = String.format(
                "Unexpected exception: %s is %s",
                pe.getMessage(),
                pe.getCause());
        System.out.println(message);
    }

    private static List<String> filterHttpSources(List<String> sources) {
        return sources.stream()
                .filter(source -> source.startsWith(("http")))
                .collect(Collectors.toList());
    }

    private static List<String> filterLocalSources(List<String> sources) {
        return sources.stream()
                .filter(source -> !source.startsWith(("http")))
                .collect(Collectors.toList());
    }

    private static boolean validateInputs(List<String> sources, String watermark) throws IOException {
        System.out.println("Validating inputs...");
        ExecutorService service = Executors.newFixedThreadPool(sources.size() + 1);
        service.execute(() -> validate(watermark));
        sources.forEach(source -> {
            service.execute(() -> validate(source));
        });
        service.shutdown();
        return true;
    }

    private static void validate(String path) {
        try {
            new Validator(path).validate();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
