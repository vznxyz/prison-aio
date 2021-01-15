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
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.lang.IllegalStateException
import java.util.*

object PersonalMineCreateCommand {

    @Command(
        names = ["privatemine create", "pmine create"],
        permission = Permissions.PMINE_CREATE,
        async = true
    )
    @JvmStatic fun execute(sender: CommandSender, @Param("player") uuid: UUID) {
        try {
            PrivateMineHandler.createMine(uuid) { mine ->
                val player = Bukkit.getPlayer(uuid)
                if (player != null) {
                    player.sendMessage("")
                    player.sendMessage(" ${ChatColor.GREEN}${ChatColor.BOLD}Private Mine Ready")
                    player.sendMessage(" ${ChatColor.GRAY}Your private mine is ready to be progress!")
                    player.sendMessage(" ${ChatColor.YELLOW}Type /pmine to get started!")
                    player.sendMessage("")
                }
            }
        } catch (e: IllegalStateException) {
            sender.sendMessage("${ChatColor.RED}Failed to generate private mine!")

            if (sender.isOp) {
                sender.sendMessage("${ChatColor.RED}${e.message}")
            }

            e.printStackTrace()
        }
    }

}