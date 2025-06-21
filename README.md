# mojang2tiny

Convert Mojang mappings to Tiny format.

## Overview

**mojang2tiny** is a command-line tool that converts Mojang mapping files to the [Tiny](https://github.com/FabricMC/tiny-mappings-parser) format, using an intermediary mapping file. This is useful for Minecraft modding and tooling workflows that require Tiny mappings.

## Features
- Converts Mojang mappings to Tiny format
- Supports multiple mapping files
- Command-line interface powered by [picocli](https://picocli.info/)

## Requirements
- Java 17 or higher (Java 21 recommended)

## Usage

### Build

Build the project using Gradle:

```sh
gradle build
```

### Run

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

### Example

```sh
java -jar build/libs/mojang2tiny-1.0.0.jar \
  -i intermediary.tiny \
  -m mojang-mappings.txt \
  -o output/
```

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## Release Notes

See [RELEASE_NOTES.md](RELEASE_NOTES.md) for the latest changes.

## License

This project is licensed under the BSD 3-Clause License. See [LICENSE](LICENSE) for details.
