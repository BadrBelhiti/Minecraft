plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    // Internal dependencies
    implementation(project(":SharedCore"))

    // Paper API
    compileOnly(libs.paper.api)

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    // Test dependencies
    testImplementation(kotlin("test"))
}

tasks {
    shadowJar {
        archiveFileName.set("SurvivalPlugin.jar")

        // Relocate dependencies to avoid conflicts
        relocate("kotlin", "dev.nerdsatx.survival.shaded.kotlin")

        // Minimize the JAR by removing unused classes
        minimize()
    }

    // Build Docker image after building the plugin
    val buildDockerImage by registering(Exec::class) {
        description = "Build the Survival server Docker image"
        group = "docker"

        workingDir = rootProject.projectDir
        commandLine("docker-compose", "build", "survival")

        dependsOn(shadowJar)
    }

    build {
        dependsOn(shadowJar)
        finalizedBy(buildDockerImage)
    }
}