package net.evilblock.prisonaio.module.user.listener

import net.evilblock.prisonaio.PrisonAIO
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerQuitEvent

object DropPickaxeListeners : Listener {

    @EventHandler
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        if (event.itemDrop.itemStack.type.name.contains("_PICKAXE")) {
            if (!event.player.hasMetadata("CONFIRM_DROP")) {
                event.player.sendMessage("${ChatColor.RED}You must type ${ChatColor.BOLD}/drop confirm ${ChatColor.RED}to drop your pickaxe!")
                event.isCancelled = true
                return
            }
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        event.player.removeMetadata("CONFIRM_DROP", PrisonAIO.instance)
    }

}