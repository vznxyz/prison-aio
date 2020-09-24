/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack.listener

import net.evilblock.prisonaio.module.mechanic.backpack.Backpack
import net.evilblock.prisonaio.module.mechanic.backpack.BackpackHandler
import net.evilblock.prisonaio.module.mechanic.event.AnvilPrepareEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent

object BackpackListeners : Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        if (!event.player.hasPlayedBefore()) {
            val backpack = Backpack()
            BackpackHandler.trackBackpack(backpack)

            event.player.inventory.addItem(backpack.toBackpackItem())
            event.player.updateInventory()
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            if (event.hasItem() && BackpackHandler.isBackpackItem(event.item)) {
                val backpack = BackpackHandler.extractBackpack(event.item)
                if (backpack != null) {
                    event.isCancelled = true

                    backpack.updateLore(event.item)
                    backpack.open(event.player)
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onAnvilRenameEvent(event: AnvilPrepareEvent) {
        if (BackpackHandler.isBackpackItem(event.result)) {
            event.isCancelled = true
        }
    }

}