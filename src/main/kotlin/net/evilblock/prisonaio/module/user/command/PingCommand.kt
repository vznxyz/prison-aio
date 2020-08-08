/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.nms.MinecraftReflection
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object PingCommand {

    @Command(
        names = ["ping", "lag"],
        description = "Shows the latency of a player"
    )
    @JvmStatic
    fun execute(sender: Player, @Param(name = "player", defaultValue = "self") target: Player) {
        val ping = Reflection.getDeclaredFieldValue(MinecraftReflection.getHandle(target), "ping") as Int

        val color = when {
            ping > 200 -> ChatColor.DARK_RED
            ping > 150 -> ChatColor.RED
            ping > 80 -> ChatColor.YELLOW
            ping > 25 -> ChatColor.GREEN
            else -> ChatColor.DARK_GREEN
        }

        sender.sendMessage("${ChatColor.GRAY}${target.name}'s ping: $color${ping}ms")
    }

}