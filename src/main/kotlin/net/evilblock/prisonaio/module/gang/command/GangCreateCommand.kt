/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.GangModule
import net.evilblock.source.chat.filter.ChatFilterHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object GangCreateCommand {

    @Command(
        names = ["gang create", "gangs create"],
        description = "Create a new gang",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "name", wildcard = true) name: String) {
        if (ChatFilterHandler.filterMessage(name) != null) {
            player.sendMessage("${ChatColor.RED}The name you input contains inappropriate content. Please try a different name.")
            return
        }

        if (!name.matches(EzPrompt.IDENTIFIER_REGEX)) {
            player.sendMessage("${ChatColor.RED}The name you input does not match the regex pattern ${EzPrompt.IDENTIFIER_REGEX.pattern}.")
            return
        }

        if (GangHandler.getOwnedGangs(player.uniqueId).isNotEmpty()) {
            player.sendMessage("${ChatColor.RED}You can only have one gang at a time. To generate a new gang, delete your old gang and then try again.")
            return
        }

        if (GangHandler.getGangByName(name) != null) {
            player.sendMessage("${ChatColor.RED}The name `$name` is already taken by another gang.")
            return
        }

        if (name.length > GangModule.getMaxNameLength()) {
            player.sendMessage("${ChatColor.RED}A gang's name can only be ${GangModule.getMaxNameLength()} characters long. The name you entered was ${name.length} characters.")
            return
        }

        player.sendMessage("${ChatColor.GREEN}Creating your gang...")

        try {
            GangHandler.createNewGang(player.uniqueId, name) { gang ->
                Tasks.sync {
                    GangHandler.attemptJoinSession(player, gang)
                    player.sendMessage("${ChatColor.YELLOW}You are now the leader of this gang. Use ${ChatColor.YELLOW}/gang home ${ChatColor.YELLOW}to teleport back to your gang headquarters.")
                }
            }
        } catch (e: Exception) {
            if (player.isOp) {
                player.sendMessage("${ChatColor.RED}Failed to generate a new gang for you: ${e.message}")
            } else {
                player.sendMessage("${ChatColor.RED}Failed to generate a new gang for you. If this issue persists, please contact an administrator.")
            }
            return
        }
    }

}