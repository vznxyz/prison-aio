/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.type

import net.evilblock.cubed.util.Chance
import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.region.Region
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import java.text.NumberFormat

object LuckyMoney : AbstractEnchant("lucky-money", "Lucky Money", 3) {

    override val iconColor: Color
        get() = Color.YELLOW

    override val textColor: ChatColor
        get() = ChatColor.GOLD

    override val menuDisplay: Material
        get() = Material.GOLD_INGOT

    override fun getCost(level: Int): Long {
        return when (level) {
            1 -> 60000
            2 -> 100000
            else -> 150000
        }
    }

    override fun onBreak(event: BlockBreakEvent, enchantedItem: ItemStack?, level: Int, region: Region) {
        if (Chance.percent(1)) {
            val money: Double = when (level) {
                1 -> 100_000.0
                2 -> 250_000.0
                else -> 500_000.0
            }

            // payout the money to the player
            VaultHook.useEconomyAndReturn { economy -> economy.depositPlayer(event.player, money) }

            // send notification
            sendMessage(event.player, "You found ${ChatColor.AQUA}$${ChatColor.GREEN}${ChatColor.BOLD}${NumberFormat.getInstance().format(money)} ${ChatColor.GREEN}while mining!")
        }
    }

}