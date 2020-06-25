/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.dialogue.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.quest.QuestHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object QuestDialogueSkipCommand {

    @Command(names = ["quest dialogue skip", "quests dialogue skip"], description = "", permission = "")
    @JvmStatic
    fun execute(player: Player) {
        if (QuestHandler.isDialogueSequencePlaying(player)) {
            QuestHandler.stopDialogueSequence(player)
            player.sendMessage("${ChatColor.GREEN}Skipped dialogue!")
        } else {
            player.sendMessage("${ChatColor.RED}You have no dialogue playing.")
        }
    }

}