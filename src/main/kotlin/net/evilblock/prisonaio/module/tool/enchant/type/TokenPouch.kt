/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.type

import net.evilblock.cubed.util.Chance
import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorHandler
import net.evilblock.prisonaio.module.tool.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.region.Region
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.floor

object TokenPouch : AbstractEnchant("token-pouch", "Token Pouch", 1000) {

    override val iconColor: Color
        get() = Color.YELLOW

    override val textColor: ChatColor
        get() = ChatColor.YELLOW

    override val menuDisplay: Material
        get() = Material.MAGMA_CREAM

//    override fun getCost(level: Int): Long {
//        return (10000 + (level - 1) * 80).toLong()
//    }

    override fun onBreak(event: BlockBreakEvent, enchantedItem: ItemStack?, level: Int, region: Region) {
        val user = UserHandler.getUser(event.player.uniqueId)

        if (Chance.percent(0.0025 * level)) {
            var tokenAmount = Chance.pick(2200, floor(2200 + level * 1.1).toInt())

            if (AbilityArmorHandler.getEquippedSet(event.player) != null) {
                tokenAmount = (tokenAmount * 2.0).toInt()
            }

            user.addTokensBalance(tokenAmount.toLong())

            sendMessage(event.player, "You found a pouch with ${Formats.formatTokens(tokenAmount.toLong())} ${ChatColor.GRAY}tokens in it!")
        }
    }

}