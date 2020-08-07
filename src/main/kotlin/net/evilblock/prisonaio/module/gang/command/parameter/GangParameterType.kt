/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command.parameter

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.GangHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object GangParameterType : ParameterType<Gang> {

    override fun transform(sender: CommandSender, source: String): Gang? {
        if (source == "self") {
            val gang = GangHandler.getAssumedGang((sender as Player).uniqueId)
            if (gang == null) {
                sender.sendMessage("${ChatColor.RED}You are not in a gang right now.")
            }
            return gang
        }

        try {
            val gang = GangHandler.getGangById(UUID.fromString(source))
            if (gang != null) {
                return gang
            }
        } catch (e: Exception) {
            val gang = GangHandler.getGangByName(source)
            if (gang != null) {
                return gang
            }
        }

        val playerUuid = Cubed.instance.uuidCache.uuid(source)
        if (playerUuid != null) {
            val assumedGang = GangHandler.getAssumedGang(playerUuid)
            if (assumedGang != null) {
                return assumedGang
            }
        }

        sender.sendMessage("${ChatColor.RED}Couldn't find a gang with that name or ID.")

        return null
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return emptyList()
    }

}