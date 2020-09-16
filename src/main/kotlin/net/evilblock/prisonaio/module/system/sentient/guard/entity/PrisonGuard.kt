/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.sentient.guard.entity

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.system.sentient.SentientHandler
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.ThreadLocalRandom

class PrisonGuard(location: Location) : NpcEntity(lines = listOf(SentientHandler.getPrisonGuardName()), location = location) {

    var nextWalk: Long = randomTime()
    var nextPhrase: Long = randomTime()
    var walkRegion: Cuboid? = null

    override fun initializeData() {
        super.initializeData()

        nextWalk = randomTime()
        nextPhrase = randomTime()

        updateLines(listOf(SentientHandler.getPrisonGuardName()))
        updateTexture(SentientHandler.getPrisonGuardTextureValue(), SentientHandler.getPrisonGuardTextureSignature())
    }

    fun randomTime(): Long {
        return System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(10_000L, 17_000L)
    }

    object TypeAdapter : ParameterType<PrisonGuard> {
        override fun transform(sender: CommandSender, source: String): PrisonGuard? {
            val guard = EntityManager.getEntityById(source.toInt())
            return if (guard !is PrisonGuard) {
                sender.sendMessage("${ChatColor.RED}Couldn't find a Prison Guard with the ID `$source`!")
                null
            } else {
                guard
            }
        }

        override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
            return emptyList()
        }
    }

}