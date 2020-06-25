/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

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