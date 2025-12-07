package dev.nerdsatx.cdk

import software.amazon.awscdk.App
import software.amazon.awscdk.Environment
import software.amazon.awscdk.StackProps

/**
 * CDK App entry point for Minecraft network infrastructure
 */
fun main() {
    val app = App()

    // Create the Minecraft network stack
    MinecraftStack(
        app,
        "MinecraftNetworkStack",
        StackProps.builder()
            .env(
                Environment.builder()
                    .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                    .region(System.getenv("CDK_DEFAULT_REGION"))
                    .build()
            )
            .description("Nerds @ ATX Minecraft Network Infrastructure")
            .build()
    )

    app.synth()
}
