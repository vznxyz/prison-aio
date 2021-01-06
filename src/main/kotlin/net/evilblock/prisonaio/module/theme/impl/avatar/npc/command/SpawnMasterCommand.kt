/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.theme.impl.avatar.npc.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.prisonaio.module.theme.impl.avatar.npc.MasterNPC
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpawnMasterCommand {

    @Command(
        names = ["npc spawn avatar-master"],
        description = "Spawns an Avatar Master NPC",
        permission = Permissions.SYSTEM_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        val npc = MasterNPC(player.location)
        npc.initializeData()

        EntityManager.trackEntity(npc)
        EntityManager.saveData()

        player.sendMessage("${ChatColor.GREEN}Spawned an Avatar Master!")
    }

}