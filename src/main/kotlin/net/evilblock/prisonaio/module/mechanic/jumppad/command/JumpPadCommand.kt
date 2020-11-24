/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.jumppad.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player

object JumpPadCommand {

    @Command(
        names = ["jumppad", "jump-pad"],
        description = "Spawn a Jump Pad",
        permission = "op"
    )
    @JvmStatic
    fun execute(player: Player) {
        val hologram = HologramEntity(text = "", location = player.eyeLocation.subtract(0.0, 0.5, 0.0))
        hologram.initializeData()
        hologram.updateLines(MechanicsModule.getJumpPadDefaultLines())
        hologram.spawn(player)

        EntityManager.trackEntity(hologram)
        Tasks.async { EntityManager.saveData() }

        player.location.block.getRelative(BlockFace.DOWN).type = Material.SPONGE
        player.sendMessage("${ChatColor.GREEN}Spawned a Jump Pad!")
    }

}