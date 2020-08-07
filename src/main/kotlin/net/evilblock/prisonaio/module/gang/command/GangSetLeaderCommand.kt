/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.gang.GangHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object GangSetLeaderCommand {

    @Command(
        names = ["gang leader", "gangs leader"],
        description = "Relinquish ownership of your gang to a member",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "player") newOwner: UUID) {
        val gang = GangHandler.getAssumedGang(player.uniqueId)
        if (gang == null) {
            player.sendMessage("${ChatColor.RED}You must be inside a gang to set relinquish ownership of it.")
            return
        }

        if (!gang.isOwner(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}Only the owner can relinquish ownership of the gang.")
            return
        }

        if (player.uniqueId == newOwner) {
            player.sendMessage("${ChatColor.RED}You are already the owner of your gang.")
            return
        }

        val newOwnerUsername = Cubed.instance.uuidCache.name(newOwner)

        if (!gang.isMember(newOwner)) {
            player.sendMessage("${ChatColor.RED}$newOwnerUsername is not a member of your gang.")
            return
        }

        gang.updateOwner(newOwner)
        player.sendMessage("${ChatColor.GREEN}You have given ownership of your gang to $newOwnerUsername.")
    }

}