/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.impl

import net.evilblock.cubed.util.Chance
import net.evilblock.cubed.util.TextUtil
import net.evilblock.prisonaio.module.tool.enchant.Enchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantHandler.upgradeEnchant
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeHandler
import net.evilblock.prisonaio.module.region.Region
import net.evilblock.prisonaio.module.tool.enchant.EnchantCategory
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

object Scavenger : Enchant("scavenger", "Scavenger", 1) {

    override fun getCategory(): EnchantCategory {
        return EnchantCategory.WEALTH_HIGH
    }

    override val menuDisplay: Material
        get() = Material.GOLD_NUGGET

//    override fun getCost(level: Int): Long {
//        return readCost()
//    }

    override fun onBreak(event: BlockBreakEvent, enchantedItem: ItemStack?, level: Int, region: Region) {
        val pickaxeData = PickaxeHandler.getPickaxeData(enchantedItem) ?: return

        if (Chance.percent(readChance())) {
            val enchant: Enchant = if (Chance.random()) {
                Efficiency
            } else {
                Fortune
            }

            val result = upgradeEnchant(event.player, pickaxeData, enchantedItem!!, enchant, 1, false)
            if (result) {
                sendMessage(event.player, "You found " + TextUtil.aOrAn(enchant.enchant) + " level while mining! It has been applied to your pickaxe.")
            }
        }
    }

}