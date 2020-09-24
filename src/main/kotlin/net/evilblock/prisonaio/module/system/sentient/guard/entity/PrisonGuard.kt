/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.sentient.guard.entity

import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.prisonaio.module.system.sentient.SentientHandler
import org.bukkit.Location

class PrisonGuard(location: Location) : NpcEntity(lines = listOf(SentientHandler.getPrisonGuardName()), location = location) {

    override fun initializeData() {
        super.initializeData()

        updateLines(listOf(SentientHandler.getPrisonGuardName()))
        updateTexture(SentientHandler.getPrisonGuardTextureValue(), SentientHandler.getPrisonGuardTextureSignature())
        updatePhrases(SentientHandler.getPrisonGuardPhrases())
    }

}