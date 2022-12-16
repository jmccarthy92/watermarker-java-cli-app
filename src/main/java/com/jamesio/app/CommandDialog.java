package com.jamesio.app;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

public class CommandDialog {
    private final static String DEST_STRING = "dest";
    private final static String SOURCES_STRING = "sources";
    private final static String WATERMARK_PATH_STRING = "watermark";
    private final static String WATERMARK_ENV_VAR_STRING = "WATERMARK_PATH";

    public enum CommandDialogField {
        DESTINATION,
        SOURCES,
        WATERMARK,
    }

    private Options options;

    // Defaults to outputting files in current dir.
    private String destination = ".";

    public String getDestination() {
        return destination;
    }

    private String[] sources;

    public String[] getSources() {
        return sources;
    }

    private String watermarkPath;

    public String getWatermark() {
        return watermarkPath;
    }

    public CommandDialog() {
        String destinationEnv = System.getenv("DESTINATION");
        if (destinationEnv != null)
            destination = destinationEnv;
        options = new Options();
        addHelpOption();
        addWatermarkOption();
        addDestinationOption();
        addSourceOption();
    }

    public void printOptions() {
        System.out.println(String.format("Destination: %s\nWatermark: %s\nSources: %s",
                destination,
                watermarkPath,
                Arrays.toString(sources)));
    }

    private void printHelp() {
        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(110);
        hf.printHelp("Watermarker", options, true);
    }

    private void addHelpOption() {
        @SuppressWarnings("static-access")
        Option helpOption = OptionBuilder
                .withLongOpt("help")
                .create('h');
        options.addOption(helpOption);
    }

    private void addWatermarkOption() {
        @SuppressWarnings("static-access")
        Option watermarkOption = OptionBuilder
                .withLongOpt(CommandDialog.WATERMARK_PATH_STRING)
                .withDescription(
                        "Watermark file path. If no argument is provided, the environment variable WATERMARK_PATH will be checked. If none are provided an Exception will be thrown")
                .hasArg()
                .create();
        options.addOption(watermarkOption);
    }

    private void addSourceOption() {
        @SuppressWarnings("static-access")
        Option sourceOption = OptionBuilder
                .withLongOpt(CommandDialog.SOURCES_STRING)
                .withDescription("List of paths to sources. Publicly accessible files over the network can be used.")
                .hasArgs()
                .withArgName("PATHS").create();
        options.addOption(sourceOption);
    }

    private void addDestinationOption() {
        options.addOption("d", CommandDialog.DEST_STRING, false,
                "Output destination for watermarked files. Watermarker output files into a directory /watermark in the directory the process is run.");
    }

    public void parseOptions(String[] args) throws ParseException {
        CommandLineParser parser = new GnuParser();
        CommandLine line = parser.parse(options, args);
        if (line.hasOption('h')) {
            this.printHelp();
            return;
        }
        this.parseDestination(line);
        sources = line.getOptionValues("sources");
        this.parseWatermark(line);
    }

    private String parseDestination(CommandLine line) {
        if (line.hasOption(CommandDialog.DEST_STRING)) {
            destination = line.getOptionValue(CommandDialog.DEST_STRING);
        }
        return destination;
    }

    private String parseWatermark(CommandLine line) throws ParseException {
        if (line.hasOption(CommandDialog.WATERMARK_PATH_STRING)) {
            watermarkPath = line.getOptionValue(CommandDialog.WATERMARK_PATH_STRING);
        } else {
            String value = System.getenv(CommandDialog.WATERMARK_ENV_VAR_STRING);
            if (value == null) {
                throw new ParseException("No watermark provided");
            } else {
                watermarkPath = value;
            }
        }
        return watermarkPath;
    }

    public boolean hasHttpUri(CommandDialogField field) {
        switch (field) {
            case DESTINATION:
                return destination.startsWith("http");
            case SOURCES:
                return hasHttpSources();
            case WATERMARK:
                return watermarkPath.startsWith("http");
            default:
                return false;
        }
    }

    private boolean hasHttpSources() {
        for (int counter = 0; counter < sources.length; counter++) {
            if (sources[counter].startsWith("http"))
                return true;
        }
        return false;
    }

}
