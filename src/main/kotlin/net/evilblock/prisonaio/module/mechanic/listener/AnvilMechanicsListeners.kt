/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.listener

import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.module.mechanic.event.AnvilPrepareEvent
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent

object AnvilMechanicsListeners : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onAnvilPrepare(event: InventoryClickEvent) {
        if (event.currentItem == null || event.currentItem.type == Material.AIR) {
            return
        }

        if (event.inventory.type == InventoryType.ANVIL) {
            if (event.slotType == InventoryType.SlotType.RESULT) {
                if (!AnvilPrepareEvent(event.whoClicked as Player, event.currentItem).callEvent()) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onCraftAnvil(event: CraftItemEvent) {
        if (MechanicsModule.areAnvilMechanicsDisabled()) {
            if (event.recipe.result.type == Material.ANVIL) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onInteractAnvil(event: PlayerInteractEvent) {
        if (MechanicsModule.areAnvilMechanicsDisabled()) {
            if (RegionBypass.hasBypass(event.player) && event.player.gameMode == GameMode.CREATIVE) {
                RegionBypass.attemptNotify(event.player)
                return
            }

            if (event.action != Action.RIGHT_CLICK_BLOCK) {
                return
            }

            if (event.clickedBlock.type == Material.ANVIL) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlaceAnvil(event: BlockPlaceEvent) {
        if (MechanicsModule.areAnvilMechanicsDisabled()) {
            if (RegionBypass.hasBypass(event.player) && event.player.gameMode == GameMode.CREATIVE) {
                RegionBypass.attemptNotify(event.player)
                return
            }

            if (event.block.type == Material.ANVIL) {
                event.isCancelled = true
            }
        }
    }

}