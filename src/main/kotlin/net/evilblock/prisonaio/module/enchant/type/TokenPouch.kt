/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.type

import net.evilblock.cubed.util.Chance
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.region.Region
import net.evilblock.prisonaio.module.user.UserHandler
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
        get() = ChatColor.GOLD

    override val menuDisplay: Material
        get() = Material.MAGMA_CREAM

    override fun getCost(level: Int): Long {
        return (10000 + (level - 1) * 80).toLong()
    }

    override fun onBreak(event: BlockBreakEvent, enchantedItem: ItemStack?, level: Int, region: Region) {
        val user = UserHandler.getUser(event.player.uniqueId)

        if (Chance.percent(0.0025 * level)) {
            val tokenAmount = Chance.pick(2200, floor(2200 + level * 1.1).toInt())
            user.addTokensBalance(tokenAmount.toLong())

            sendMessage(event.player, "You found a pouch with ${ChatColor.GOLD}$tokenAmount ${ChatColor.GRAY}tokens in it!")
        }
    }

}