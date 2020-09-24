/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.impl.tutorial.entity

import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.prisonaio.module.quest.QuestsModule
import net.evilblock.prisonaio.module.quest.impl.tutorial.TutorialQuest
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.Location
import org.bukkit.entity.Player

class PersonalTutorialGuide(location: Location, @Transient val player: Player) : NpcEntity(location = location, lines = listOf(QuestsModule.getNpcName("tutorial-guide"))) {

    override fun initializeData() {
        super.initializeData()

        if (!hasTexture()) {
            updateTexture(QuestsModule.getNpcTextureValue("tutorial-guide"), QuestsModule.getNpcTextureSignature("tutorial-guide"))
        }
    }

    override fun isVisibleToPlayer(player: Player): Boolean {
        return player == this.player && super.isVisibleToPlayer(player)
    }

    override fun onRightClick(player: Player) {

    }

}