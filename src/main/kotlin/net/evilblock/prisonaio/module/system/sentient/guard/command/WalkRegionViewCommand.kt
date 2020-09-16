/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.sentient.guard.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.system.sentient.guard.entity.PrisonGuard
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

object WalkRegionViewCommand {

    @Command(
        names = ["npc walk-region view"],
        description = "View an NPC's walk region",
        permission = Permissions.SYSTEM_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "guard") guard: PrisonGuard) {
        if (guard.walkRegion == null) {
            player.sendMessage("${ChatColor.RED}That NPC does not have a walk region!")
            return
        }

        val changedBlocks = arrayListOf<Location>()

        val dyeId = randomWoolDyeId()
        for (location in guard.walkRegion!!.getBlockLocations()) {
            if (location.isChunkLoaded) {
                changedBlocks.add(location)
                player.sendBlockChange(location, Material.WOOL, dyeId)
            }
        }

        Tasks.asyncDelayed(20L * 5) {
            for (location in changedBlocks) {
                if (location.isChunkLoaded) {
                    val block = location.block
                    player.sendBlockChange(location, block.type, block.data)
                }
            }
        }
    }

    private fun randomWoolDyeId(): Byte {
        return listOf(11, 9, 3, 5, 4, 1, 14, 2, 10).random().toByte()
    }

}