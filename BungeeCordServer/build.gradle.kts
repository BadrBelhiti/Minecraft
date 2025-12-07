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

    build {
        dependsOn(shadowJar)
    }
}
