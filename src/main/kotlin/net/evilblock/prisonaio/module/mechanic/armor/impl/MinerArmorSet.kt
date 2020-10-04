/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.armor.impl

import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorHandler
import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorSet
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

object MinerArmorSet : AbilityArmorSet(
    "Miner",
    "${ChatColor.AQUA}${ChatColor.BOLD}Miner",
    ItemStack(Material.IRON_HELMET),
    ItemStack(Material.IRON_CHESTPLATE),
    ItemStack(Material.IRON_LEGGINGS),
    ItemStack(Material.IRON_BOOTS)
), Listener {

    override fun getSetDescription(): String {
        return "While the full set is equipped, you will receive a 4x shop multiplier stacked on any other active multipliers you have."
    }

    override fun getShortSetDescription(): String {
        return "Receive a 4x shop multiplier"
    }

    override fun getInheritedArmorSets(): List<AbilityArmorSet> {
        return listOf(InmateArmorSet)
    }

    /**
     * Applies the Miner Armor shop multiplier when selling to shops.
     */
    @EventHandler
    fun onPlayerSellToShopEvent(event: PlayerSellToShopEvent) {
        val abilityArmor = AbilityArmorHandler.getEquippedSet(event.player)
        if (abilityArmor is MinerArmorSet || abilityArmor is WardenArmorSet) {
            event.multiplier = event.multiplier + 4.0
        }
    }

}