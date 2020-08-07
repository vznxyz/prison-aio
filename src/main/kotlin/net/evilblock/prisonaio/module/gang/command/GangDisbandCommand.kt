/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.region.RegionsModule
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object GangDisbandCommand {

    @Command(
        names = ["gang disband", "gangs disband"],
        description = "Disband your gang",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        val gang = GangHandler.getAssumedGang(player.uniqueId)
        if (gang == null) {
            player.sendMessage("${ChatColor.RED}You must be in a gang to disband it.")
            return
        }

        if (!gang.isOwner(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}You must be the owner of this gang to disband it.")
            return
        }

        gang.sendMessagesToAll("${ChatColor.YELLOW}The gang has been disbanded by the owner.")

        for (member in gang.getMembers()) {
            GangHandler.updateGangAccess(member, gang, false)
        }

        for (activePlayer in gang.getActivePlayers()) {
            GangHandler.updateVisitingGang(activePlayer, null)

            Tasks.sync {
                activePlayer.teleport(Bukkit.getWorlds()[0].spawnLocation)
            }
        }

        GangHandler.forgetGang(gang)
        RegionsModule.clearBlockCache(gang)

        player.sendMessage("${ChatColor.GREEN}You have successfully disbanded your gang.")
    }

}