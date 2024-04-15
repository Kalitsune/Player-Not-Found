![Banner](./Banner.png)

# Player Not Found

PlayerNotFound is a minigame plugin for Minecraft servers where players have to find the true player among a group of
NPCs.

## Features

- Support for multiple fully customizable Arenas
- Compatible with server-side commands, enabling execution from datapacks and command blocks.

## Compatibility

You'll need to have [citizens2](https://www.spigotmc.org/resources/citizens.13811/)
and [CommandAPI](https://www.spigotmc.org/resources/api-commandapi-1-15-1-20-4.62353/) installed on your server to use
this plugin.

**Tested with**

- Minecraft 1.16.5
- Paper 1.16.5
- Citizens 2.0.33
- CommandAPI 6.3.1
- OpenJDK 17

**⚠️ If you encounter any compatibility issue with newer versions,
please [open an issue](https://github.com/Kalitsune/player-not-found/issues ) on the GitHub repository.**

## Download

Downloadable files for PlayerNotFound are available in
the [Release tab](https://github.com/Kalitsune/player-not-found/releases) of the GitHub repository.

## Build Instructions

To build PlayerNotFound, follow these steps:

1. Clone the repository from [GitHub](https://github.com/Kalitsune/player-not-found).
2. Navigate to the cloned directory.
3. Execute the following command using Maven:

```shell
mvn -B clean package
```

4. Once the build is successful, locate the output file in the `target` directory.
   For a better developper experience you can enable the `export-to-test-server` profile