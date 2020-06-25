/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.TimeUtil
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

        if (!user.perks.hasPerk(player, Perk.SALES_BOOST)) {
            player.sendMessage("${ChatColor.RED}You don't have any granted sales multipliers.")
            return
        }

        val activeGrant = user.perks.getActivePerkGrant(Perk.SALES_BOOST)!!
        val formattedMultiplier = user.perks.getSalesMultiplier(player)

        if (activeGrant.isPermanent()) {
            player.sendMessage("${ChatColor.GRAY}You have a granted sales multiplier of ${ChatColor.RED}${ChatColor.BOLD}$formattedMultiplier ${ChatColor.GRAY}for a period of ${ChatColor.RED}forever${ChatColor.GRAY}.")
        } else {
            val formattedDuration = TimeUtil.formatIntoDetailedString((activeGrant.getRemainingTime() / 1000.0).toInt())
            player.sendMessage("${ChatColor.GRAY}You have a granted sales multiplier of ${ChatColor.RED}${ChatColor.BOLD}$formattedMultiplier ${ChatColor.GRAY}for a period of ${ChatColor.RED}$formattedDuration${ChatColor.GRAY}.")
        }
    }

}