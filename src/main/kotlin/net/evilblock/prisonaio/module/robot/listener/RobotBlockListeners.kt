package net.evilblock.prisonaio.module.robot.listener

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

object RobotBlockListeners : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {
        if (event.blockPlaced.hasMetadata("RobotBlock")) {
            event.isCancelled = true
            event.player.sendMessage("${ChatColor.RED}You can't a place a block there!")
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        if (event.block.hasMetadata("RobotBlock")) {
            event.isCancelled = true
            event.player.sendMessage("${ChatColor.RED}You can't break that block!")
        }
    }

}