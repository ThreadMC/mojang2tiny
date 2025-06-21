package com.threadmc.mojang2tiny;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.*;
import java.util.concurrent.Callable;

/**
 * Main entry point for mojang2tiny. Can be used as a CLI (via main method)
 * or as a library via the static convert() method.
 */
@Command(
        name = "mojang2tiny",
        mixinStandardHelpOptions = true,
        version = {"1.0"},
        description = {"Convert Mojang mappings to Tiny format."}
)
public class Main implements Callable<Integer> {
    @Option(names = {"-i", "--intermediary"}, required = true, description = {"Intermediary mapping file"})
    private File intermediaryFile;
    @Option(names = {"-m", "--mappings"}, required = true, description = {"Mojang mapping file"})
    private File mappingFile;
    @Option(names = {"-o", "--output-dir"}, required = true, description = {"Output directory"})
    private File outputDir;
    @Option(names = {"-t", "--tiny-version"}, description = {"Tiny format version (v1 or v2), default: v2"})
    private String tinyVersion = "v2";

    public Main() {}

    @Override
    public Integer call() throws Exception {
        System.out.println("[Mojang2Tiny] Loading intermediary");
        Intermediary intermediary;
        try (InputStream in = new FileInputStream(intermediaryFile)) {
            intermediary = Intermediary.load(in);
        }
        MojangMap mojang = MojangMap.empty();
        System.out.println("[Mojang2Tiny] Loading mojang: " + mappingFile.getName());
        try (InputStream in = new FileInputStream(mappingFile)) {
            MojangMap m = MojangMap.load(in);
            mojang.combine(m);
        }
        System.out.println("[Mojang2Tiny] Writing mapping file");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        File outFile = new File(outputDir, "mappings.tiny");
        MappingOutput.write(outFile, intermediary, mojang, tinyVersion);
        return 0;
    }

    /**
     * CLI entry point. Use this method to run mojang2tiny from the command line.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    /**
     * Programmatic API for converting mappings. Use this method to invoke the conversion from Java code.
     * @param intermediaryFile Intermediary mapping file
     * @param mappingFiles List of Mojang mapping files
     * @param outputDir Output directory for Tiny mappings
     * @throws Exception if an error occurs during conversion
     */
    public static void convert(File intermediaryFile, File mappingFile, File outputDir, String tinyVersion) throws Exception {
        Intermediary intermediary;
        try (InputStream in = new FileInputStream(intermediaryFile)) {
            intermediary = Intermediary.load(in);
        }
        MojangMap mojang = MojangMap.empty();
        try (InputStream in = new FileInputStream(mappingFile)) {
            MojangMap m = MojangMap.load(in);
            mojang.combine(m);
        }
        File outFile = new File(outputDir, "mappings.tiny");
        MappingOutput.write(outFile, intermediary, mojang, tinyVersion);
    }
}
