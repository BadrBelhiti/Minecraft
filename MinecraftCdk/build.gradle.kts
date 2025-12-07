plugins {
    application
}

application {
    mainClass.set("dev.nerdsatx.cdk.MinecraftCdkAppKt")
}

dependencies {
    // AWS CDK
    implementation(libs.aws.cdk.lib)
    implementation(libs.aws.constructs)

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    // Test dependencies
    testImplementation(kotlin("test"))
}