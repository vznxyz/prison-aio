/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object NicknameGrantCommand {

    @Command(
        names = ["nickname grant", "nn grant"],
        description = "Grant a color to a player",
        permission = Permissions.NICKNAME_GRANT,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User, @Param(name = "color") color: ChatColor) {
        if (!UserHandler.NICKNAME_COLORS.contains(color) && !UserHandler.NICKNAME_STYLES.contains(color)) {
            sender.sendMessage("${ChatColor.RED}That color/style is disabled.")
            return
        }

        user.nicknameColors.add(color)
        user.requiresSave = true

        user.getPlayer()?.let { player ->
            player.sendMessage("")

            if (color.isColor) {
                player.sendMessage(" ${color}${ChatColor.BOLD}New Nickname Color Unlocked")
                player.sendMessage(" ${ChatColor.GRAY}Visit your ${color}${ChatColor.BOLD}/nickname ${ChatColor.GRAY}settings to enable it.")
            } else if (color.isFormat) {
                player.sendMessage(" ${ChatColor.GREEN}${ChatColor.BOLD}New Nickname Style Unlocked")
                player.sendMessage(" ${ChatColor.GRAY}Visit your ${ChatColor.GREEN}${ChatColor.BOLD}/nickname ${ChatColor.GRAY}settings to enable it.")
            }

            player.sendMessage("")
        }

        sender.sendMessage("${ChatColor.GREEN}You have granted the ${color}${ChatColor.BOLD}${Formats.capitalizeFully(color.name.replace("_", " "))} ${ChatColor.GREEN}color to ${user.getUsername()}.")
    }

}