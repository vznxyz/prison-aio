/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.listener

import net.evilblock.cubed.entity.tool.EntityMoveTool
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.entity.JerryNpcEntity
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

object GangJerryListeners : Listener {

    /**
     * Prevents players from placing Jerry outside of their cell with the move tool.
     */
    @EventHandler
    fun onEntityMovedEvent(event: EntityMoveTool.EntityMovedEvent) {
        if (event.entity is JerryNpcEntity) {
            if (!((event.entity as JerryNpcEntity).gang.cuboid.contains(event.location))) {
                event.isCancelled = true
                event.player.sendMessage("${ChatColor.RED}You must place Jerry inside of your cell!")
                return
            }
        }
    }

    /**
     * Removes any move tools from the player's inventory when they leave the grid world.
     *
     * If any move tools are found, the entity is extracted from the item metadata
     * and the entity's visibility is updated to visible to prevent any NPCs
     * from becoming "lost".
     */
    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerTeleportEvent(event: PlayerTeleportEvent) {
        val gridWorld = GangHandler.getGridWorld()
        if (event.from.world == gridWorld && event.to.world != gridWorld) {
            EntityMoveTool.clearInventoryOfTool(event.player)
        }
    }

}