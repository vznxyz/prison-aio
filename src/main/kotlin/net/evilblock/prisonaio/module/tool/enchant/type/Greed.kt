/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.type

import net.evilblock.cubed.util.Chance
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.tool.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.tool.ToolsModule
import net.evilblock.prisonaio.module.shop.receipt.ShopReceiptItem
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object Greed : AbstractEnchant("greed", "Greed", 3) {

    override val iconColor: Color
        get() = Color.YELLOW

    override val textColor: ChatColor
        get() = ChatColor.YELLOW

//    override fun getCost(level: Int): Long {
//        return (12000 + (level - 1) * 4000).toLong()
//    }

    override val menuDisplay: Material
        get() = Material.PAPER

    override fun onSellAll(player: Player, enchantedItem: ItemStack?, level: Int, event: PlayerSellToShopEvent) {
        if (event.items.isEmpty()) {
            return
        }

        if (Chance.percent(readChance())) {
            val randomItem: ShopReceiptItem = event.items.toList()[Chance.pick(event.items.size)]

            val multiplierMap = readLevelToMultiplierMap()

            val multiplier: Double = multiplierMap.getOrElse(level) {
                multiplierMap.entries.maxBy { it.key }!!.value
            }

            if (randomItem.multiplier < multiplier) {
                randomItem.multiplier = multiplier

                val formattedPrice = (randomItem.itemType.buyPricePerUnit * randomItem.item.amount) * randomItem.multiplier
                sendMessage(event.player, "You sold ${ChatColor.AQUA}${randomItem.item.amount}x ${Formats.formatItemStack(randomItem.item)} ${ChatColor.GRAY}for a multiplier of ${ChatColor.YELLOW}$multiplier ${ChatColor.GRAY}(${ChatColor.AQUA}$${ChatColor.GREEN}${ChatColor.BOLD}${NumberUtils.format(formattedPrice.toLong())}${ChatColor.GRAY})!")
            }
        }
    }

    private fun readLevelToMultiplierMap(): Map<Int, Double> {
        val section = ToolsModule.config.getConfigurationSection("greed.level-to-multiplier")
        return section.getKeys(false).shuffled().map { it.toInt() to section.getDouble(it) }.toMap()
    }

}