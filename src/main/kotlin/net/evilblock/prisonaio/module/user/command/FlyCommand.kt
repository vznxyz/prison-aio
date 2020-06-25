/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.perk.Perk
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object FlyCommand {

    @Command(
        names = ["fly"],
        description = "Toggle your fly perk, if available"
    )
    @JvmStatic
    fun execute(player: Player) {
        val user = UserHandler.getUser(player.uniqueId)
        if (!user.perks.hasPerk(player, Perk.FLY)) {
            player.sendMessage("${ChatColor.RED}You don't have access to the fly perk.")
            return
        }

        user.perks.togglePerk(Perk.FLY)

        if (user.perks.isPerkEnabled(Perk.FLY)) {
            player.allowFlight = true
            player.isFlying = true
            player.sendMessage("${ChatColor.GREEN}Fly is now enabled.")
        } else {
            player.allowFlight = false
            player.isFlying = false
            player.sendMessage("${ChatColor.RED}Fly is now disabled.")
        }
    }

}