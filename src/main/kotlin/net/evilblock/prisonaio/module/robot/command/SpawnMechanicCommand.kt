package net.evilblock.prisonaio.module.robot.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.prisonaio.module.robot.mechanic.RobotMechanic
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpawnMechanicCommand {

    @Command(
            names = ["npc spawn robot-mechanic"],
            description = "Spawns a Robot Mechanic",
            permission = "op",
            async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        val npc = RobotMechanic(player.location)
        npc.initializeData()

        EntityManager.trackEntity(npc)

        player.sendMessage("${ChatColor.GREEN}Spawned a Robot Mechanic!")
    }

}