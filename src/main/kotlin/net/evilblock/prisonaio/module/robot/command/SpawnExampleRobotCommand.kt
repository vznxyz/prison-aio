package net.evilblock.prisonaio.module.robot.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.robot.Robot
import net.evilblock.prisonaio.module.robot.RobotHandler
import net.evilblock.prisonaio.module.robot.impl.ExampleRobot
import net.evilblock.prisonaio.module.robot.impl.StaticExampleRobot
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpawnExampleRobotCommand {

    @Command(
            names = ["robots spawn example"],
            description = "Spawns an Example Robot",
            permission = "op",
            async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "tier", defaultValue = "-1337") tier: Int) {
        val robot: Robot = if (tier == -1337) {
            ExampleRobot(player.location)
        } else {
            StaticExampleRobot(player.location, tier)
        }

        robot.initializeData()
        robot.spawn(player)

        RobotHandler.trackRobot(robot)

        player.sendMessage("${ChatColor.GREEN}Spawned an Example Robot!")
    }

}