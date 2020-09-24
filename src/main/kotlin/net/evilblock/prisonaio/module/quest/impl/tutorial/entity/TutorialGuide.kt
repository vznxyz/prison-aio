/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.impl.tutorial.entity

import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.prisonaio.module.quest.QuestsModule
import net.evilblock.prisonaio.module.quest.impl.tutorial.TutorialQuest
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.Location
import org.bukkit.entity.Player

class TutorialGuide(location: Location) : NpcEntity(location = location, lines = listOf(QuestsModule.getNpcName("tutorial-guide"))) {

    override fun initializeData() {
        super.initializeData()

        if (!hasTexture()) {
            updateTexture(QuestsModule.getNpcTextureValue("tutorial-guide"), QuestsModule.getNpcTextureSignature("tutorial-guide"))
        }
    }

    override fun isVisibleToPlayer(player: Player): Boolean {
        val progress = UserHandler.getUser(player.uniqueId).getQuestProgress(TutorialQuest)
        if (progress.hasStarted() && !progress.isCompleted() && progress.hasCurrentMission()) {
            return false
        }

        return super.isVisibleToPlayer(player)
    }

    override fun onRightClick(player: Player) {
        val user = UserHandler.getUser(player.uniqueId)

        val progress = user.getQuestProgress(TutorialQuest)
        val title = if (progress.isCompleted()) {
            "Restart tutorial?"
        } else {
            "Start tutorial?"
        }

        ConfirmMenu(title) { confirmed ->
            if (confirmed) {
                destroy(player)
                TutorialQuest.onStartQuest(player)
            }
        }.openMenu(player)
    }

}