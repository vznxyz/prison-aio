/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.personal.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.mine.variant.personal.PrivateMineHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object PersonalMineKickCommand {

    @Command(
            names = ["privatemine kick", "pmine kick"],
            description = "Kick an active player from your mine"
    )
    @JvmStatic
    fun execute(player: Player, @Param("player") target: Player) {
        val currentMine = PrivateMineHandler.getCurrentMine(player)
        if (currentMine == null) {
            player.sendMessage("${ChatColor.RED}")
        } else {
            if (currentMine.owner == player.uniqueId) {
                player.sendMessage("${ChatColor.YELLOW}You have kicked ${ChatColor.GRAY}${target.name} ${ChatColor.YELLOW}from your Private Mine.")
                target.sendMessage("${ChatColor.RED}You have been kicked from ${player.name}'s Private Mine!")
            } else {
                player.sendMessage("${ChatColor.RED}You can't kick players from ${ChatColor.GRAY}${currentMine.getOwnerName()}${ChatColor.RED}'s Private Mine!")
            }
        }
    }

}