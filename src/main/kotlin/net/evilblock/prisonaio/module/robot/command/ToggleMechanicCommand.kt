package net.evilblock.prisonaio.module.robot.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.robot.mechanic.RobotMechanic
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object ToggleMechanicCommand {

    @Command(
            names = ["robots toggle-mechanic", "robot toggle-mechanic"],
            description = "Toggles the Robot Mechanic functionality",
            permission = "op"
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        RobotMechanic.disabled = !RobotMechanic.disabled

        if (RobotMechanic.disabled) {
            sender.sendMessage("${ChatColor.RED}Robot Mechanic functionality disabled!")
        } else {
            sender.sendMessage("${ChatColor.GREEN}Robot Mechanic functionality enabled!")
        }
    }

}