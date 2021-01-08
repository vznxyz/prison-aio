package net.evilblock.prisonaio.module.robot.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.robot.RobotsModule
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object ToggleAnimationsCommand {

    @Command(
            names = ["robots toggle-animations", "robot toggle-animations"],
            description = "Toggles robot animations",
            permission = "op"
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        RobotsModule.toggleAnimations()

        if (RobotsModule.isAnimationsEnabled()) {
            sender.sendMessage("${ChatColor.GREEN}Robot animations enabled!")
        } else {
            sender.sendMessage("${ChatColor.RED}Robot animations disabled!")
        }
    }

}