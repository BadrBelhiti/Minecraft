plugins {
    base  // Required for IntelliJ to recognize as a Gradle module
}

// Configuration for PlantUML dependency
val plantuml by configurations.creating

dependencies {
    plantuml("net.sourceforge.plantuml:plantuml:1.2024.3")
}

val plantumlSourceDir = file("src/main/plantuml")
val plantumlOutputDir = file("diagrams")

tasks {
    // Generate PlantUML diagrams
    val generateDiagrams by registering(JavaExec::class) {
        description = "Generate PNG diagrams from PlantUML files"
        group = "documentation"

        mainClass.set("net.sourceforge.plantuml.Run")
        classpath = plantuml
        jvmArgs("-DPLANTUML_LIMIT_SIZE=8192")
        args(
            "-v",
            "-tpng",
            "-SdefaultRenderer=smetana",  // Use built-in Smetana renderer instead of Graphviz
            "-o", plantumlOutputDir.absolutePath,
            plantumlSourceDir.absolutePath
        )

        inputs.dir(plantumlSourceDir)
        outputs.dir(plantumlOutputDir)

        doFirst {
            plantumlOutputDir.mkdirs()
        }
    }

    // Clean generated diagrams
    val cleanDiagrams by registering(Delete::class) {
        description = "Delete generated diagram files"
        group = "documentation"

        delete(plantumlOutputDir)
    }

    clean {
        dependsOn(cleanDiagrams)
    }

    build {
        dependsOn(generateDiagrams)
    }
}
