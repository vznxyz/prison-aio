package net.evilblock.prisonaio.module.mechanic.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent

object CraftingMechanicsListeners : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onCraftPickaxe(event: CraftItemEvent) {
        if (event.recipe.result.type.name.contains("PICKAXE")) {
            event.isCancelled = true
        }
    }

}