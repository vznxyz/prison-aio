package net.evilblock.prisonaio.module.mechanic.listener

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object DisableNicknameListeners : Listener {

    @EventHandler
    fun onPlayerCommandPreprocessEvent(event: PlayerCommandPreprocessEvent) {
        if (event.message.startsWith("/enick", ignoreCase = true) ||
            event.message.startsWith("/essentials:enick", ignoreCase = true) ||
            event.message.startsWith("/nick", ignoreCase = true) ||
            event.message.startsWith("/essentials:nick", ignoreCase = true)) {
            event.isCancelled = true
            event.player.sendMessage("${ChatColor.RED}Nicknames are currently disabled.")
        }
    }

}