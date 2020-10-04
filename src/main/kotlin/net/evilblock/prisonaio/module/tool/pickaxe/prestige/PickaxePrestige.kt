/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.pickaxe.prestige

import com.google.gson.annotations.JsonAdapter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.tool.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeData
import net.evilblock.prisonaio.module.tool.enchant.serialize.EnchantsMapReferenceSerializer
import net.evilblock.prisonaio.module.user.UserHandler
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

    fun meetsRequirements(player: Player, pickaxeData: PickaxeData): Boolean {
        val user = UserHandler.getUser(player.uniqueId)
        return user.hasMoneyBalance(moneyRequired.toDouble())
                && user.hasTokenBalance(tokensRequired)
                && user.getPrestige() >= prestigeRequired
                && pickaxeData.blocksMined >= blocksMinedRequired
    }

    fun renderRequirements(player: Player, pickaxeData: PickaxeData): List<String> {
        val user = UserHandler.getUser(player.uniqueId)

        val hasMoney = user.hasMoneyBalance(moneyRequired.toDouble())
        val hasTokens = user.hasTokenBalance(tokensRequired)
        val hasPrestige = user.getPrestige() >= prestigeRequired
        val hasBlocksMined = pickaxeData.blocksMined >= blocksMinedRequired

        val green = "${ChatColor.GREEN}${ChatColor.BOLD}${Constants.THICK_VERTICAL_LINE} ${ChatColor.GREEN}${ChatColor.STRIKETHROUGH}"
        val gray = "${ChatColor.RED}${ChatColor.BOLD}${Constants.THICK_VERTICAL_LINE} ${ChatColor.GRAY}"

        return listOf(
            if (hasMoney) { green } else { gray } + "Acquire \$${NumberFormat.getInstance().format(moneyRequired)}",
            if (hasTokens) { green } else { gray } + "Acquire ${NumberFormat.getInstance().format(tokensRequired)} tokens",
            if (hasPrestige) { green } else { gray } + "Reach prestige ${NumberFormat.getInstance().format(prestigeRequired)}",
            if (hasBlocksMined) { green } else { gray } + "Mine ${NumberFormat.getInstance().format(blocksMinedRequired)} blocks"
        )
    }

    fun purchase(player: Player, pickaxeItem: ItemStack, pickaxeData: PickaxeData) {
        val user = UserHandler.getUser(player.uniqueId)
        user.subtractTokensBalance(tokensRequired)

        VaultHook.useEconomy { economy ->
            economy.withdrawPlayer(player.name, moneyRequired.toDouble())
        }

        pickaxeData.prestige++
        pickaxeData.applyMeta(pickaxeItem)
    }

}