/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.impl.tutorial.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.quest.impl.tutorial.TutorialQuest
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object TutorialQuestConfigCommands {

    @Command(
        names = ["quest config tutorial start-location"],
        description = "Sets the start location of the Tutorial quest",
        permission = Permissions.SYSTEM_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        TutorialQuest.config.startLocation = player.location
        TutorialQuest.saveData()
    }

}