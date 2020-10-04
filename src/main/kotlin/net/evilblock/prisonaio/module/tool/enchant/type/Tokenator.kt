/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.type

import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorHandler
import net.evilblock.prisonaio.module.tool.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.tool.ToolsModule
import net.evilblock.prisonaio.module.region.Region
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.roundToInt

object Tokenator : AbstractEnchant("tokenator", "Tokenator", 100) {

    override val iconColor: Color
        get() = Color.AQUA

    override val textColor: ChatColor
        get() = ChatColor.AQUA

    override val menuDisplay: Material
        get() = Material.MAGMA_CREAM

//    override fun getCost(level: Int): Long {
//        return (readCost() + (level - 1) * 100).toLong()
//    }

    override fun onBreak(event: BlockBreakEvent, enchantedItem: ItemStack?, level: Int, region: Region) {
        var tokenAmount = (level * readMultiplier()).coerceAtLeast(1.0).roundToInt().toLong()

        val equippedSet = AbilityArmorHandler.getEquippedSet(event.player)
        if (equippedSet != null) {
            tokenAmount = (tokenAmount * 2.0).toLong()
        }

        UserHandler.getUser(event.player.uniqueId).addTokensBalance(tokenAmount)
    }

    fun readMultiplier(): Double {
        return ToolsModule.config.getDouble("tokenator.multiplier")
    }

}