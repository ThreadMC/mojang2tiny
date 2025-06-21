# mojang2tiny

Convert Mojang mappings to Tiny format.

## Overview

**mojang2tiny** is a tool that converts Mojang mapping files to the [Tiny](https://github.com/FabricMC/tiny-mappings-parser) format, using an intermediary mapping file. It can be used as a **command-line tool** or as a **Java library (API)** in your own applications. This is useful for Minecraft modding and tooling workflows that require Tiny mappings.

## Features
- Converts Mojang mappings to Tiny format
- Supports multiple mapping files
- Usable as a CLI or as a Java library (API)
- Command-line interface powered by [picocli](https://picocli.info/)

## Requirements
- Java 17 or higher (Java 21 recommended)

## Usage

### Build

Build the project using Gradle:

```sh
gradle build
```

### Run as CLI

You can either **download the latest jar from the [Releases tab](https://github.com/ThreadMC/mojang2tiny/releases) of this GitHub repository**, or build it yourself as shown above.

After building (or downloading), you can run the tool from the command line:

```sh
java -jar build/libs/mojang2tiny-1.0.0.jar \
  -i <intermediary.tiny> \
  -m <mappings1.txt> [<mappings2.txt> ...] \
  -o <output-directory>
```

#### Arguments
- `-i`, `--intermediary` — Path to the intermediary mapping file (Tiny format)
- `-m`, `--mappings` — One or more Mojang mapping files to convert
- `-o`, `--output-dir` — Output directory for the generated Tiny files

### Example (CLI)

```sh
java -jar build/libs/mojang2tiny-1.0.0.jar \
  -i intermediary.tiny \
  -m mojang-mappings.txt \
  -o output/
```

### Use as a Java Library (API)

You can also use mojang2tiny programmatically in your own Java applications by calling the static API method:

```java
import com.threadmc.mojang2tiny.Main;
import java.io.File;
import java.util.Arrays;

public class Example {
    public static void main(String[] args) throws Exception {
        File intermediary = new File("intermediary.tiny");
        File mapping1 = new File("mojang-mappings.txt");
        File outputDir = new File("output");
        Main.convert(intermediary, Arrays.asList(mapping1), outputDir);
    }
}
```

- `Main.convert(File intermediaryFile, List<File> mappingFiles, File outputDir)`
- Throws `Exception` if an error occurs during conversion.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## Release Notes

See [RELEASE_NOTES.md](RELEASE_NOTES.md) for the latest changes.

## License

This project is licensed under the BSD 3-Clause License. See [LICENSE](LICENSE) for details.
