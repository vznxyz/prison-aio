/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.sentient.guard.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.prisonaio.module.system.sentient.SentientHandler
import net.evilblock.prisonaio.module.system.sentient.guard.entity.PrisonGuard
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpawnPrisonGuardCommand {

    @Command(
        names = ["npc spawn prison-guard"],
        description = "Spawns a Prison Guard NPC",
        permission = Permissions.SYSTEM_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        val guard = PrisonGuard(player.location)
        guard.initializeData()
        guard.spawn(player)

        EntityManager.trackEntity(guard)
        EntityManager.saveData()

        player.sendMessage("${ChatColor.GREEN}Spawned a ${SentientHandler.getPrisonGuardName()}${ChatColor.GREEN}!")
    }

}