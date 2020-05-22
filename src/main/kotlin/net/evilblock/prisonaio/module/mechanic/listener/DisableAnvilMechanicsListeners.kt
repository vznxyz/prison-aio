package net.evilblock.prisonaio.module.mechanic.listener

import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerInteractEvent

object DisableAnvilMechanicsListeners : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onCraftAnvil(event: CraftItemEvent) {
        if (MechanicsModule.areAnvilMechanicsDisabled()) {
            if (event.recipe.result.type === Material.ANVIL) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onInteractAnvil(event: PlayerInteractEvent) {
        if (MechanicsModule.areAnvilMechanicsDisabled()) {
            if (event.action !== Action.RIGHT_CLICK_BLOCK) {
                return
            }

            if (event.clickedBlock.type === Material.ANVIL) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlaceAnvil(event: BlockPlaceEvent) {
        if (MechanicsModule.areAnvilMechanicsDisabled()) {
            if (event.block.type === Material.ANVIL) {
                event.isCancelled = true
            }
        }
    }

}