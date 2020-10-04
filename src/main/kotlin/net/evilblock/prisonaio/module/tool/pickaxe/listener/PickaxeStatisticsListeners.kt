/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.pickaxe.listener

import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

object PickaxeStatisticsListeners : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val itemInHand = event.player.inventory.itemInMainHand
        PickaxeHandler.getPickaxeData(itemInHand)?.let {
            it.blocksMined++
            it.applyMeta(event.player.inventory.itemInMainHand)
        }
    }

}