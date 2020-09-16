/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.impl.tutorial.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.quest.impl.tutorial.TutorialQuest
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.entity.Player

object TutorialCommand {

    @Command(
        names = ["tutorial"],
        description = "Start the Tutorial quest"
    )
    @JvmStatic
    fun execute(player: Player) {
        val user = UserHandler.getUser(player.uniqueId)
        if (user.getQuestProgress(TutorialQuest).isCompleted()) {
            player.sendMessage("")
            player.sendMessage("")
            player.sendMessage("")
        } else {

        }
    }

}