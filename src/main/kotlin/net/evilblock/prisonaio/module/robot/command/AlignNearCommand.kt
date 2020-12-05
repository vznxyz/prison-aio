package net.evilblock.prisonaio.module.robot.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.prisonaio.module.robot.Robot
import net.evilblock.prisonaio.module.robot.RobotHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import kotlin.math.floor

object AlignNearCommand {

    @Command(
            names = ["robot align-near", "robots align-near"],
            description = "Aligns any robots in a given radius",
            permission = "op",
            async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "radius") radius: Int) {
        var aligned = 0
        for (robot in EntityManager.getEntities().filterIsInstance<Robot>()) {
            if (player.world == robot.location.world && player.location.distanceSquared(robot.location) <= radius) {
                val location = robot.location.clone()
                location.x = floor(location.x) + 0.5
                location.z = floor(location.z) + 0.5

                robot.updateLocation(location)

                aligned++
            }
        }

        RobotHandler.saveData()

        player.sendMessage("${ChatColor.GREEN}Aligned $aligned robots!")
    }

}