package dev.nerdsatx.survival

import dev.nerdsatx.shared.Constants
import org.bukkit.plugin.java.JavaPlugin

/**
 * Main plugin class for the Survival server
 */
class SurvivalPlugin : JavaPlugin() {

    override fun onEnable() {
        logger.info("${Constants.NETWORK_PREFIX} SurvivalPlugin is enabling...")

        // Initialize plugin components here
        registerCommands()
        registerListeners()

        logger.info("${Constants.NETWORK_PREFIX} SurvivalPlugin has been enabled!")
    }

    override fun onDisable() {
        logger.info("${Constants.NETWORK_PREFIX} SurvivalPlugin is disabling...")

        // Cleanup resources here

        logger.info("${Constants.NETWORK_PREFIX} SurvivalPlugin has been disabled!")
    }

    private fun registerCommands() {
        // Register commands here
    }

    private fun registerListeners() {
        // Register event listeners here
    }
}
