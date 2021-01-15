/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.GangsModule
import net.evilblock.prisonaio.module.gang.booster.GangBooster
import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorHandler
import net.evilblock.prisonaio.module.mechanic.armor.impl.MinerArmorSet
import net.evilblock.prisonaio.module.mechanic.armor.impl.WardenArmorSet
import net.evilblock.prisonaio.module.reward.multiplier.GlobalMultiplierHandler
import net.evilblock.prisonaio.module.reward.multiplier.GlobalMultiplierType
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.UsersModule
import net.evilblock.prisonaio.module.user.perk.Perk
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MultipliersCommand {

    @Command(
        names = ["shopmulti", "shopmultiplier", "salesmultiplier", "salesmulti", "salesboost", "multi", "multiplier"],
        description = "Shows your active shop multiplier"
    )
    @JvmStatic
    fun execute(player: Player) {
        val user = UserHandler.getUser(player.uniqueId)

        var shopMultiplier = 0.0
        var tokenMultiplier = 0.0

        for ((permission, multiplier) in UsersModule.permissionSalesMultipliers) {
            if (player.hasPermission(permission)) {
                player.sendMessage("${ChatColor.GRAY}You have a ${ChatColor.GREEN}${ChatColor.BOLD}rank-based shop multiplier ${ChatColor.GRAY}of ${ChatColor.GREEN}${ChatColor.BOLD}${NumberUtils.formatDecimal(multiplier)}x${ChatColor.GRAY}.")
                shopMultiplier += multiplier
                break
            }
        }

        if (user.perks.hasPerk(player, Perk.SALES_BOOST)) {
            val activePerk = user.perks.getActivePerkGrant(Perk.SALES_BOOST)!!
            val perkMultiplier = activePerk.metadata.get("multiplier")?.asDouble ?: 1.0

            shopMultiplier += perkMultiplier

            val formattedPerkMulti = NumberUtils.formatDecimal(perkMultiplier)

            if (activePerk.isPermanent()) {
                player.sendMessage("${ChatColor.GRAY}You have a ${ChatColor.GREEN}${ChatColor.BOLD}granted shop multiplier of ${ChatColor.GREEN}${ChatColor.BOLD}${formattedPerkMulti}x ${ChatColor.GRAY}for a period of ${ChatColor.GREEN}forever${ChatColor.GRAY}.")
            } else {
                val formattedDuration = TimeUtil.formatIntoDetailedString((activePerk.getRemainingTime() / 1000.0).toInt())
                player.sendMessage("${ChatColor.GRAY}You have a ${ChatColor.GREEN}${ChatColor.BOLD}granted shop multiplier of ${ChatColor.GREEN}${ChatColor.BOLD}${formattedPerkMulti}x ${ChatColor.GRAY}for a period of ${ChatColor.GREEN}$formattedDuration${ChatColor.GRAY}.")
            }
        }

        val assumedGang = GangHandler.getGangByPlayer(player.uniqueId)
        if (assumedGang != null && assumedGang.hasBooster(GangBooster.BoosterType.SALES_MULTIPLIER)) {
            shopMultiplier += GangsModule.readSalesMultiplierMod()

            val booster = assumedGang.getBooster(GangBooster.BoosterType.SALES_MULTIPLIER)!!
            val formattedDuration = TimeUtil.formatIntoDetailedString((booster.getRemainingTime() / 1000.0).toInt())
            player.sendMessage("${ChatColor.GRAY}You have a ${ChatColor.GOLD}${ChatColor.BOLD}Gang Booster ${ChatColor.GREEN}${ChatColor.BOLD}shop multiplier ${ChatColor.GRAY}of ${ChatColor.GREEN}${ChatColor.BOLD}${NumberUtils.formatDecimal(GangsModule.readSalesMultiplierMod())}x ${ChatColor.GRAY}for a period of ${ChatColor.RED}$formattedDuration${ChatColor.GRAY}.")
        }

        val globalShopMulti = GlobalMultiplierHandler.getEvent(GlobalMultiplierType.SHOP)
        if (globalShopMulti != null) {
            shopMultiplier += globalShopMulti.multiplier
            player.sendMessage("${ChatColor.GRAY}You have an ${ChatColor.GREEN}${ChatColor.BOLD}event shop multiplier ${ChatColor.GRAY}of ${ChatColor.GREEN}${ChatColor.BOLD}${NumberUtils.formatDecimal(globalShopMulti.multiplier)}x ${ChatColor.GRAY}for the next ${ChatColor.RED}${globalShopMulti.getRemainingTime()}${ChatColor.GRAY}.")
        }

        val equippedSet = AbilityArmorHandler.getEquippedSet(player)
        if (equippedSet != null && equippedSet.hasAbility(MinerArmorSet)) {
            shopMultiplier += 4.0
            player.sendMessage("${ChatColor.GRAY}You have a ${MinerArmorSet.setName} Armor ${ChatColor.GREEN}${ChatColor.BOLD}shop multiplier ${ChatColor.GRAY}of ${ChatColor.GREEN}${ChatColor.BOLD}${NumberUtils.formatDecimal(4.0)}x${ChatColor.GRAY}.")
        }

        val globalTokenMulti = GlobalMultiplierHandler.getEvent(GlobalMultiplierType.TOKEN)
        if (globalTokenMulti != null) {
            tokenMultiplier += globalTokenMulti.multiplier
            player.sendMessage("${ChatColor.GRAY}You have an ${ChatColor.YELLOW}${ChatColor.BOLD}event token multiplier ${ChatColor.GRAY}of ${ChatColor.GOLD}${ChatColor.BOLD}${NumberUtils.formatDecimal(globalTokenMulti.multiplier)}x ${ChatColor.GRAY}for the next ${ChatColor.RED}${globalTokenMulti.getRemainingTime()}${ChatColor.GRAY}.")
        }

        if (AbilityArmorHandler.getEquippedSet(player)?.hasAbility(WardenArmorSet) == true) {
            player.sendMessage("${ChatColor.GRAY}You have a ${WardenArmorSet.setName} Armor ${ChatColor.YELLOW}${ChatColor.BOLD}token multiplier ${ChatColor.GRAY}of ${ChatColor.GOLD}${ChatColor.BOLD}${NumberUtils.formatDecimal(2.0)}x${ChatColor.GRAY}.")
            tokenMultiplier += 2.0
        }

        val globalBlocksMinedMulti = GlobalMultiplierHandler.getEvent(GlobalMultiplierType.BLOCKS_MINED)
        if (globalBlocksMinedMulti != null) {
            player.sendMessage("${ChatColor.GRAY}You have a ${ChatColor.RED}${ChatColor.BOLD}blocks mined multiplier ${ChatColor.GRAY}of ${ChatColor.RED}${ChatColor.BOLD}${NumberUtils.formatDecimal(globalBlocksMinedMulti.multiplier)}${ChatColor.GRAY}.")
        }

        if (shopMultiplier > 0.0 || tokenMultiplier > 0.0) {
            player.sendMessage("${ChatColor.DARK_GRAY}-")
        }

        player.sendMessage("${ChatColor.GRAY}Your ${ChatColor.GREEN}${ChatColor.BOLD}shop multiplier ${ChatColor.GRAY}is ${ChatColor.GREEN}${ChatColor.BOLD}${NumberUtils.formatDecimal(shopMultiplier.coerceAtLeast(1.0))}x${ChatColor.GRAY}!")
        player.sendMessage("${ChatColor.GRAY}Your ${ChatColor.YELLOW}${ChatColor.BOLD}token multiplier ${ChatColor.GRAY}is ${ChatColor.YELLOW}${ChatColor.BOLD}${NumberUtils.formatDecimal(tokenMultiplier.coerceAtLeast(1.0))}x${ChatColor.GRAY}!")
    }

}