/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.listener

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent

object CraftingMechanicsListeners : Listener {

    /**
     * List of items that are prevented from being crafted.
     */
    private val bannedRecipeItems = arrayListOf(
        Material.DIAMOND_PICKAXE,
        Material.GOLD_PICKAXE,
        Material.IRON_PICKAXE,
        Material.STONE_PICKAXE,
        Material.WOOD_PICKAXE,
        Material.END_CRYSTAL,
        Material.BEACON,
        Material.NETHER_STAR,
        Material.ENCHANTMENT_TABLE,
        Material.BOOK_AND_QUILL
    )

    /**
     * Prevents banned recipes from being crafted.
     */
    @EventHandler(ignoreCancelled = true)
    fun onCraftBannedRecipe(event: CraftItemEvent) {
        if (bannedRecipeItems.contains(event.recipe.result.type)) {
            event.isCancelled = true
        }
    }

}