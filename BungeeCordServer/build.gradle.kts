plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    // Internal dependencies
    implementation(project(":SharedCore"))

    // BungeeCord API
    compileOnly(libs.bungeecord.api)

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    // Test dependencies
    testImplementation(kotlin("test"))
}

tasks {
    shadowJar {
        archiveFileName.set("BungeeCordServer.jar")

        // Relocate dependencies to avoid conflicts
        relocate("kotlin", "dev.nerdsatx.proxy.shaded.kotlin")

        // Minimize the JAR by removing unused classes
        minimize()
    }

    // Build Docker image after building the plugin
    val buildDockerImage by registering(Exec::class) {
        description = "Build the BungeeCord server Docker image"
        group = "docker"

        workingDir = rootProject.projectDir
        commandLine("docker-compose", "build", "bungeecord")

        dependsOn(shadowJar)
    }

    build {
        dependsOn(shadowJar)
        finalizedBy(buildDockerImage)
    }
}
