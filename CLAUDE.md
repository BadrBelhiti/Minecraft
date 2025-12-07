# Nerds @ ATX Minecraft Network

A Kotlin-based Minecraft server network with AWS CDK infrastructure management.

## Project Structure

This is a Gradle monorepo with a flat directory structure:

```
Minecraft/
├── settings.gradle.kts           # Defines all modules
├── build.gradle.kts              # Root build config with common settings
├── gradle.properties             # Gradle configuration
├── gradle/
│   └── libs.versions.toml        # Centralized dependency versions
├── MinecraftCdk/                 # AWS CDK infrastructure as code
│   ├── build.gradle.kts
│   └── src/main/kotlin/dev/nerdsatx/cdk/
├── BungeeCordProxy/              # BungeeCord proxy plugin
│   ├── build.gradle.kts
│   └── src/main/kotlin/dev/nerdsatx/proxy/
├── SurvivalServer/               # Survival server configuration
│   ├── build.gradle.kts
│   └── server/
├── SkyBlockServer/               # SkyBlock server configuration
│   ├── build.gradle.kts
│   └── server/
├── SurvivalPlugin/               # Survival game plugin
│   ├── build.gradle.kts
│   └── src/main/kotlin/dev/nerdsatx/survival/
├── SkyBlockPlugin/               # SkyBlock game plugin
│   ├── build.gradle.kts
│   └── src/main/kotlin/dev/nerdsatx/skyblock/
└── SharedCore/                   # Shared utilities and models
    ├── build.gradle.kts
    └── src/main/kotlin/dev/nerdsatx/shared/
```

## Package Structure

All code uses the base package: `dev.nerdsatx`

- `dev.nerdsatx.shared` - Shared utilities, models, database code
- `dev.nerdsatx.survival` - Survival plugin code
- `dev.nerdsatx.skyblock` - SkyBlock plugin code
- `dev.nerdsatx.proxy` - BungeeCord proxy plugin
- `dev.nerdsatx.cdk` - AWS infrastructure code

## Module Dependencies

```
SurvivalPlugin  ──→  SharedCore
SkyBlockPlugin  ──→  SharedCore
BungeeCordProxy ──→  SharedCore
MinecraftCdk    (independent)
SurvivalServer  (independent)
SkyBlockServer  (independent)
```

## Technology Stack

- **Language**: Kotlin 1.9.22
- **Build Tool**: Gradle with Kotlin DSL
- **Minecraft Platform**: Paper API (1.20.4) for game servers
- **Proxy**: BungeeCord API
- **Infrastructure**: AWS CDK
- **Plugin Packaging**: Shadow plugin for fat JARs

## Key Gradle Patterns

### Version Catalog (`gradle/libs.versions.toml`)

We use Gradle's version catalog feature for centralized dependency management:

```toml
[versions]
kotlin = "1.9.22"
paper = "1.20.4-R0.1-SNAPSHOT"

[libraries]
paper-api = { module = "io.papermc.paper:paper-api", version.ref = "paper" }

[plugins]
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
```

Access in build files: `libs.paper.api` or `alias(libs.plugins.kotlin.jvm)`

### Common Build Patterns

**Plugin Module Pattern** (SurvivalPlugin, SkyBlockPlugin):
```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(project(":SharedCore"))
    compileOnly(libs.paper.api)
    implementation(libs.kotlin.stdlib)
}

tasks {
    shadowJar {
        archiveFileName.set("PluginName.jar")
        relocate("kotlin", "dev.nerdsatx.pluginname.shaded.kotlin")
    }
    build {
        dependsOn(shadowJar)
    }
}
```

**Proxy Module Pattern** (BungeeCordProxy):
```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(project(":SharedCore"))
    compileOnly(libs.bungeecord.api)
    implementation(libs.kotlin.stdlib)
}
```

**CDK Module Pattern** (MinecraftCdk):
```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

application {
    mainClass.set("dev.nerdsatx.cdk.MinecraftCdkAppKt")
}

dependencies {
    implementation(libs.aws.cdk.lib)
    implementation(libs.aws.constructs)
}
```

## Common Gradle Commands

```bash
# Build entire project
./gradlew build

# Build specific plugin
./gradlew :SurvivalPlugin:build
./gradlew :SurvivalPlugin:shadowJar

# Build all plugins
./gradlew :SurvivalPlugin:shadowJar :SkyBlockPlugin:shadowJar :BungeeCordProxy:shadowJar

# Deploy infrastructure
./gradlew :MinecraftCdk:run

# Clean and rebuild
./gradlew clean build
```

## IntelliJ IDEA Setup

1. **Import Project**: File → Open → Select root `build.gradle.kts`
2. **Auto-import**: Settings → Build, Execution, Deployment → Build Tools → Gradle
    - Enable "Reload changes automatically"
3. **Build Delegation**: Build and run using "Gradle" (not IntelliJ)
4. **JVM Version**: Ensure Gradle JVM is set to Java 17+

## Plugin Development Workflow

1. Make changes to plugin code in `src/main/kotlin/`
2. Update `plugin.yml` in `src/main/resources/` if needed
3. Build with `./gradlew :PluginName:shadowJar`
4. Output JAR is in `PluginName/build/libs/`
5. Copy JAR to server's `plugins/` folder
6. Restart server or use plugin reload

## Shared Code Guidelines

- Put common utilities in `SharedCore`
- Database models and DAOs go in `SharedCore/src/main/kotlin/dev/nerdsatx/shared/database/`
- Shared data classes go in `SharedCore/src/main/kotlin/dev/nerdsatx/shared/models/`
- Use `compileOnly` for Paper/Bungee APIs in SharedCore to avoid conflicts

## Shadow JAR & Relocation

Plugins shade and relocate dependencies to avoid conflicts:

```kotlin
shadowJar {
    relocate("kotlin", "dev.nerdsatx.pluginname.shaded.kotlin")
    relocate("org.jetbrains.exposed", "dev.nerdsatx.pluginname.shaded.exposed")
    relocate("com.zaxxer.hikari", "dev.nerdsatx.pluginname.shaded.hikari")
}
```

## AWS Infrastructure

The `MinecraftCdk` module contains CDK for:
- ECS Fargate for game servers and bungee cord proxy
- VPC and networking configuration
- Security groups and IAM roles
- S3 buckets for backups
- CloudWatch monitoring

Minimize cross-stack dependencies as much as possible. All
game server and bungee cord servers should be in one stack.
There can be a separate database stack once we add one. There
can be a separate website stack once we add one.

## Development Guidelines

- **Kotlin Style**: Follow official Kotlin coding conventions
- **JVM Target**: Java 17
- **Null Safety**: Leverage Kotlin's null safety features
- **Coroutines**: Use for async operations where appropriate
- **API Compatibility**: Target Paper API, not Bukkit/Spigot directly
- **Testing**: Add tests in `src/test/kotlin/` (JUnit 5)

## Repositories

Maven repositories are configured in root `build.gradle.kts`:
- Maven Central (default dependencies)
- Paper Maven (Paper API)
- Sonatype OSS (BungeeCord API)

## Important Notes

- Each module is independent and can be built separately
- SharedCore must be built before dependent plugins
- Use `:ModuleName` syntax for inter-module dependencies
- Shadow plugin creates fat JARs with all dependencies included
- Always relocate shaded dependencies to avoid classpath conflicts