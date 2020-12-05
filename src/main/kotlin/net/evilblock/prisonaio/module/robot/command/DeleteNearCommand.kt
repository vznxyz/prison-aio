package net.evilblock.prisonaio.module.robot.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.prisonaio.module.robot.Robot
import net.evilblock.prisonaio.module.robot.RobotHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object DeleteNearCommand {

    @Command(
            names = ["robot delete-near", "robots delete-near"],
            description = "Deletes any robots in a given radius",
            permission = "op",
            async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "radius") radius: Int) {
        var deleted = 0
        for (robot in EntityManager.getEntities().filterIsInstance<Robot>()) {
            if (player.world == robot.location.world && player.location.distanceSquared(robot.location) <= radius) {
                EntityManager.forgetEntity(robot)
                EntityManager.forgetEntity(robot.hologram)

                RobotHandler.forgetRobot(robot)

                robot.destroyForCurrentWatchers()

                deleted++
            }
        }

        RobotHandler.saveData()

        player.sendMessage("${ChatColor.GREEN}Deleted $deleted robots!")
    }

}