/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.pickaxe.prestige

import com.google.gson.annotations.JsonAdapter
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeData
import net.evilblock.prisonaio.module.enchant.serialize.EnchantsMapReferenceSerializer
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.economy.Economy
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.text.NumberFormat

class PickaxePrestige(val number: Int) {

    var moneyRequired: Long = 0L
    var tokensRequired: Long = 0L
    var prestigeRequired: Int = 0
    var blocksMinedRequired: Int = 0

    @JsonAdapter(EnchantsMapReferenceSerializer::class)
    val enchantLimits: MutableMap<AbstractEnchant, Int> = hashMapOf()

    fun meetsRequirements(player: Player): Boolean {
        val user = UserHandler.getUser(player.uniqueId)
        return user.getMoneyBalance() >= moneyRequired
                && user.getTokenBalance() >= tokensRequired
                && user.getPrestige() >= prestigeRequired
                && user.statistics.getBlocksMined() >= blocksMinedRequired
    }

    fun renderRequirements(player: Player): List<String> {
        val user = UserHandler.getUser(player.uniqueId)

        val hasMoney = user.getMoneyBalance() >= moneyRequired
        val hasTokens = user.getTokenBalance() >= tokensRequired
        val hasPrestige = user.getPrestige() >= prestigeRequired
        val hasBlocksMined = user.statistics.getBlocksMined() >= blocksMinedRequired

        return listOf(
            if (hasMoney) { "${ChatColor.GREEN}${ChatColor.STRIKETHROUGH}" } else { "${ChatColor.RED}" } + "Acquire \$${NumberFormat.getInstance().format(moneyRequired)}",
            if (hasTokens) { "${ChatColor.GREEN}${ChatColor.STRIKETHROUGH}" } else { "${ChatColor.RED}" } + "Acquire ${NumberFormat.getInstance().format(tokensRequired)} tokens",
            if (hasPrestige) { "${ChatColor.GREEN}${ChatColor.STRIKETHROUGH}" } else { "${ChatColor.RED}" } + "Reach prestige ${NumberFormat.getInstance().format(prestigeRequired)}",
            if (hasBlocksMined) { "${ChatColor.GREEN}${ChatColor.STRIKETHROUGH}" } else { "${ChatColor.RED}" } + "Mine ${NumberFormat.getInstance().format(blocksMinedRequired)} blocks"
        )
    }

    fun purchase(player: Player, pickaxeItem: ItemStack, pickaxeData: PickaxeData) {
        val user = UserHandler.getUser(player.uniqueId)
        user.subtractTokensBalance(tokensRequired)

        Economy.takeBalance(player.uniqueId, moneyRequired.toDouble())

        pickaxeData.prestige++
        pickaxeData.applyLore(pickaxeItem)
    }

}