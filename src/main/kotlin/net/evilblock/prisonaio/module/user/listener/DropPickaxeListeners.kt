package net.evilblock.prisonaio.module.user.listener

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent

object DropPickaxeListeners : Listener {

    @EventHandler
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        if (event.player.inventory.itemInMainHand.type.name.contains("_PICKAXE")) {
            if (!event.player.hasMetadata("CONFIRM_DROP")) {
                event.player.sendMessage("${ChatColor.RED}You must type ${ChatColor.BOLD}/drop confirm ${ChatColor.RED}to drop your pickaxe!")
                event.isCancelled = true
                return
            }
        }
    }

}