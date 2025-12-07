package dev.nerdsatx.proxy

import dev.nerdsatx.shared.Constants
import net.md_5.bungee.api.plugin.Plugin

/**
 * Main plugin class for the BungeeCord proxy
 */
class ProxyPlugin : Plugin() {

    override fun onEnable() {
        logger.info("${Constants.NETWORK_PREFIX} ProxyPlugin is enabling...")

        // Initialize proxy components here
        registerCommands()
        registerListeners()

        logger.info("${Constants.NETWORK_PREFIX} ProxyPlugin has been enabled!")
    }

    override fun onDisable() {
        logger.info("${Constants.NETWORK_PREFIX} ProxyPlugin is disabling...")

        // Cleanup resources here

        logger.info("${Constants.NETWORK_PREFIX} ProxyPlugin has been disabled!")
    }

    private fun registerCommands() {
        // Register commands here
    }

    private fun registerListeners() {
        // Register event listeners here
    }
}
