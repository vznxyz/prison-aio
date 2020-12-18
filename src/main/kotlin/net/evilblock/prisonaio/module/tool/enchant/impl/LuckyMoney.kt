/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.impl

import net.evilblock.cubed.util.Chance
import net.evilblock.prisonaio.module.tool.enchant.Enchant
import net.evilblock.prisonaio.module.region.Region
import net.evilblock.prisonaio.module.tool.enchant.EnchantCategory
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

object LuckyMoney : Enchant("lucky-money", "Lucky Money", 3) {

    override fun getCategory(): EnchantCategory {
        return EnchantCategory.WEALTH_LOW
    }

    override val menuDisplay: Material
        get() = Material.GOLD_INGOT

//    override fun getCost(level: Int): Long {
//        return when (level) {
//            1 -> 60000
//            2 -> 100000
//            else -> 150000
//        }
//    }

    override fun onBreak(event: BlockBreakEvent, enchantedItem: ItemStack?, level: Int, region: Region) {
        if (Chance.percent(1)) {
            val money: Double = when (level) {
                1 -> 100_000.0
                2 -> 250_000.0
                else -> 500_000.0
            }

            val user = UserHandler.getUser(event.player.uniqueId)
            user.addMoneyBalance(money)
            user.requiresSave()

            sendMessage(event.player, "You found ${Formats.formatMoney(money)} ${ChatColor.GRAY}while mining!")
        }
    }

}