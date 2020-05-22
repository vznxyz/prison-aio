package net.evilblock.prisonaio.module.mechanic.listener

import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent

object DisableBrewingMechanicsListeners : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onCraftBrewingStand(event: CraftItemEvent) {
        if (MechanicsModule.areBrewingMechanicsDisabled()) {
            if (event.recipe.result.type === Material.BREWING_STAND || event.recipe.result.type === Material.BREWING_STAND_ITEM) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onInteractBrewingStand(event: PlayerInteractEvent) {
        if (MechanicsModule.areBrewingMechanicsDisabled()) {
            if (event.action !== Action.RIGHT_CLICK_BLOCK) {
                return
            }

            if (event.clickedBlock.type === Material.ANVIL) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlaceBrewingStand(event: BlockPlaceEvent) {
        if (MechanicsModule.areBrewingMechanicsDisabled()) {
            if (event.block.type === Material.BREWING_STAND || event.block.type === Material.BREWING_STAND_ITEM) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onHopperEvent(event: InventoryMoveItemEvent) {
        if (MechanicsModule.areBrewingMechanicsDisabled()) {
            if (event.source.type == InventoryType.HOPPER && event.destination.type == InventoryType.BREWING) {
                event.isCancelled = true
                return
            }

            if (event.source.type == InventoryType.BREWING && event.destination.type == InventoryType.HOPPER) {
                event.isCancelled = true
                return
            }

            if (event.source.type == InventoryType.BREWING && event.destination.type == InventoryType.CHEST) {
                event.isCancelled = true
                return
            }
        }
    }

}