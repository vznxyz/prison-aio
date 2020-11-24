/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.luckyblock.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlockHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object LuckyBlockToggleCommand {

    @Command(
        names = ["luckyblock toggle"],
        description = "Toggles LuckyBlock functionality",
        permission = Permissions.SYSTEM_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        LuckyBlockHandler.disabled = !LuckyBlockHandler.disabled

        if (LuckyBlockHandler.disabled) {
            sender.sendMessage("${ChatColor.RED}LuckyBlock functionality has been disabled!")
        } else {
            sender.sendMessage("${ChatColor.GREEN}LuckyBlock functionality has been enabled!")
        }
    }

}