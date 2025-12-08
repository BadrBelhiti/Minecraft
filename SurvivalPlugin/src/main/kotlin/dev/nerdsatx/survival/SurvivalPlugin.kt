package dev.nerdsatx.survival

import dev.nerdsatx.shared.Constants
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

/**
 * Main plugin class for the Survival server
 */
class SurvivalPlugin : JavaPlugin(), Listener {

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

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.sendMessage("Welcome to Nerds @ ATX Survival Server!")
    }

    private fun registerListeners() {
        // Register event listeners here
        server.pluginManager.registerEvents(this, this);
    }
}
