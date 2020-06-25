/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.listener

import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerInteractEvent

object DisableFarmingMechanicsListeners : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onInteractEvent(event: PlayerInteractEvent) {
        if (MechanicsModule.areFarmingMechanicsDisabled()) {
            if (event.action == Action.RIGHT_CLICK_BLOCK) {
                val itemInHand = event.player.inventory.itemInMainHand
                if (itemInHand != null && itemInHand.type.name.contains("_HOE")) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onCraftHoe(event: CraftItemEvent) {
        if (MechanicsModule.areFarmingMechanicsDisabled()) {
            if (event.recipe.result.type.name.contains("_HOE")) {
                event.isCancelled = true
            }
        }
    }

}