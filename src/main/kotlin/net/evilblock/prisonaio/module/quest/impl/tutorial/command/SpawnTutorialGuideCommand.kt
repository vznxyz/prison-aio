/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.impl.tutorial.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.prisonaio.module.quest.QuestsModule
import net.evilblock.prisonaio.module.quest.impl.tutorial.entity.TutorialGuide
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpawnTutorialGuideCommand {

    @Command(
        names = ["npc spawn tutorial-guide"],
        description = "Spawns a Tutorial Guide NPC",
        permission = Permissions.SYSTEM_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        val guide = TutorialGuide(player.location)
        guide.initializeData()
        guide.spawn(player)

        EntityManager.trackEntity(guide)
        EntityManager.saveData()

        player.sendMessage("${ChatColor.GREEN}Spawned a ${QuestsModule.getNpcName("tutorial-guide")}${ChatColor.GREEN}!")
    }

}