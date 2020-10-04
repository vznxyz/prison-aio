/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.privatemine.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.privatemine.PrivateMineHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.lang.IllegalStateException
import java.util.*

object CreateCommand {

    @Command(
        names = ["privatemine create", "pmine create"],
        permission = "op",
        async = true
    )
    @JvmStatic fun execute(sender: CommandSender, @Param("player") uuid: UUID) {
        try {
            PrivateMineHandler.createMine(uuid)

            val player = Bukkit.getPlayer(uuid) ?: return

            player.sendMessage("")
            player.sendMessage(" ${ChatColor.GREEN}${ChatColor.BOLD}Private Mine Ready")
            player.sendMessage(" ${ChatColor.GRAY}Your private mine is ready to be mined!")
            player.sendMessage(" ${ChatColor.YELLOW}Type /pmine to get started!")
            player.sendMessage("")
        } catch (e: IllegalStateException) {
            sender.sendMessage("${ChatColor.RED}Failed to generate private mine. Please contact an admin.")

            if (sender.isOp) {
                sender.sendMessage("${ChatColor.RED}${e.message}")
            }

            e.printStackTrace()
        }
    }

}