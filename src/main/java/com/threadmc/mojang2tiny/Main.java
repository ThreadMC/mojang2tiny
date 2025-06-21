package com.threadmc.mojang2tiny;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.*;
import java.util.concurrent.Callable;
import java.util.List;

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
    @Option(names = {"-m", "--mappings"}, required = true, description = {"Mojang mapping files"}, arity = "1..*")
    private List<File> mappingFiles;
    @Option(names = {"-o", "--output-dir"}, required = true, description = {"Output directory"})
    private File outputDir;

    public Main() {}

    @Override
    public Integer call() throws Exception {
        System.out.println("Loading intermediary");
        Intermediary intermediary;
        try (InputStream in = new FileInputStream(intermediaryFile)) {
            intermediary = Intermediary.load(in);
        }
        MojangMap mojang = MojangMap.empty();
        for (File mappingFile : mappingFiles) {
            System.out.println("Loading mojang: " + mappingFile.getName());
            try (InputStream in = new FileInputStream(mappingFile)) {
                MojangMap m = MojangMap.load(in);
                mojang.combine(m);
            }
        }
        System.out.println("Writing mapping files");
        MappingOutput.write(outputDir, intermediary, mojang);
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
    public static void convert(File intermediaryFile, List<File> mappingFiles, File outputDir) throws Exception {
        Intermediary intermediary;
        try (InputStream in = new FileInputStream(intermediaryFile)) {
            intermediary = Intermediary.load(in);
        }
        MojangMap mojang = MojangMap.empty();
        for (File mappingFile : mappingFiles) {
            try (InputStream in = new FileInputStream(mappingFile)) {
                MojangMap m = MojangMap.load(in);
                mojang.combine(m);
            }
        }
        MappingOutput.write(outputDir, intermediary, mojang);
    }
}
