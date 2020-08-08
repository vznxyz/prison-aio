/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.booster.GangBooster
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.UsersModule
import net.evilblock.prisonaio.module.user.perk.Perk
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SalesMultiplierCommand {

    @Command(
        names = ["salesmultiplier", "salesmulti", "salesboost", "multi", "multiplier"],
        description = "Shows your active sales multiplier"
    )
    @JvmStatic
    fun execute(player: Player) {
        val user = UserHandler.getUser(player.uniqueId)

        for ((permission, multiplier) in UsersModule.getPermissionSalesMultipliers()) {
            if (player.hasPermission(permission)) {
                player.sendMessage("${ChatColor.GRAY}You have a permission-based sales multiplier of ${ChatColor.RED}${ChatColor.BOLD}$multiplier${ChatColor.GRAY}.")
                break
            }
        }

        if (user.perks.hasPerk(player, Perk.SALES_BOOST)) {
            val activePerk = user.perks.getActivePerkGrant(Perk.SALES_BOOST)!!
            val perkMultiplier = user.perks.getSalesMultiplier(player)

            if (activePerk.isPermanent()) {
                player.sendMessage("${ChatColor.GRAY}You have a granted sales multiplier of ${ChatColor.RED}${ChatColor.BOLD}$perkMultiplier ${ChatColor.GRAY}for a period of ${ChatColor.RED}forever${ChatColor.GRAY}.")
            } else {
                val formattedDuration = TimeUtil.formatIntoDetailedString((activePerk.getRemainingTime() / 1000.0).toInt())
                player.sendMessage("${ChatColor.GRAY}You have a granted sales multiplier of ${ChatColor.RED}${ChatColor.BOLD}$perkMultiplier ${ChatColor.GRAY}for a period of ${ChatColor.RED}$formattedDuration${ChatColor.GRAY}.")
            }
        } else {
            player.sendMessage("${ChatColor.RED}You don't have any granted sales multipliers.")
        }

        val assumedGang = GangHandler.getAssumedGang(player.uniqueId)
        if (assumedGang != null && assumedGang.hasBooster(GangBooster.BoosterType.SALES_MULTIPLIER)) {
            val booster = assumedGang.getBooster(GangBooster.BoosterType.SALES_MULTIPLIER)!!
            val formattedDuration = TimeUtil.formatIntoDetailedString((booster.getRemainingTime() / 1000.0).toInt())
            player.sendMessage("${ChatColor.GRAY}You have a Gang Booster sales multiplier of ${ChatColor.RED}${ChatColor.BOLD}${5.0} ${ChatColor.GRAY}for a period of ${ChatColor.RED}$formattedDuration${ChatColor.GRAY}.")
        } else {
            player.sendMessage("${ChatColor.RED}You don't have any gang booster sales multipliers.")
        }
    }

}