package net.evilblock.prisonaio.module.privatemine.listener

import net.evilblock.prisonaio.module.privatemine.PrivateMineHandler
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerToggleSneakEvent

object PrivateMineInventoryListeners : Listener {

    /**
     * Teleports a player to the mine spawn if they sneak while their inventory is full.
     */
    @EventHandler
    fun onPlayerToggleSneakEvent(event: PlayerToggleSneakEvent) {
        if (event.isSneaking) {
            if (event.player.world == PrivateMineHandler.getGridWorld()) {
                if (event.player.inventory.firstEmpty() == -1) {
                    val currentMine = PrivateMineHandler.getCurrentMine(event.player)
                        if (currentMine != null) {
                            event.player.sendMessage("${ChatColor.YELLOW}You have been teleported to the mine's spawn because you pressed shift while having a full inventory.")
                            event.player.teleport(currentMine.spawnPoint)
                        }
                }
            }
        }
    }

}