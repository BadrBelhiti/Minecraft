# SurvivalServer

This module contains the Paper server files and configuration for the Survival server.

## Directory Structure

```
SurvivalServer/
└── server/
    ├── plugins/         # Place built plugin JARs here
    ├── server.properties
    ├── bukkit.yml
    ├── spigot.yml
    └── paper.yml
```

## Setup

1. Download Paper 1.20.4 JAR from https://papermc.io/downloads
2. Place it in the `server/` directory as `paper.jar`
3. Build the SurvivalPlugin: `./gradlew :SurvivalPlugin:shadowJar`
4. Copy the built JAR from `SurvivalPlugin/build/libs/SurvivalPlugin.jar` to `server/plugins/`
5. Run the server: `java -Xmx2G -Xms2G -jar paper.jar --nogui`

## Configuration

Server configurations will be stored in the `server/` directory.
