/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.kit.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.kits.Kit
import net.evilblock.prisonaio.module.kit.KitsModule
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object GiveCommand {

    @Command(
        names = ["kit give", "kits give"],
        description = "Gives a kit to a player",
        permission = "kits.give.player"
    )
    @JvmStatic
    fun execute(
        sender: CommandSender,
        @Param(name = "player") player: Player,
        @Param(name = "kit") kit: Kit
    ) {
        val message = StringBuilder()
        if (sender is Player) {
            message.append("${KitsModule.getChatPrefix()}You've been given the ${kit.name} ${ChatColor.GRAY}kit by ${ChatColor.YELLOW}${sender.name}${ChatColor.GRAY}!")
        } else {
            message.append("${KitsModule.getChatPrefix()}You've been given the ${kit.name} kit!")
        }

        kit.giveItems(player)

        sender.sendMessage("${KitsModule.getChatPrefix()}You've given the ${kit.name} ${ChatColor.GRAY}kit to ${ChatColor.YELLOW}${player.name}${ChatColor.GRAY}!")
    }

}