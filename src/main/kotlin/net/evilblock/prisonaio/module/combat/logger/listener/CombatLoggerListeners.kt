package net.evilblock.prisonaio.module.combat.logger.listener

import net.evilblock.prisonaio.module.combat.logger.CombatLoggerHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object CombatLoggerListeners : Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        val logger = CombatLoggerHandler.getLoggerByOwner(event.player.uniqueId)
        if (logger != null) {
            CombatLoggerHandler.forgetLogger(logger)
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {

    }

}