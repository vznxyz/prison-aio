package net.evilblock.prisonaio.module.robot.cosmetic.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.robot.cosmetic.CosmeticHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object CosmeticsCommand {

    @Command(
            names = ["robots cosmetics", "robot cosmetics"],
            description = "List the cosmetic IDs",
            permission = "op"
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}Robot Cosmetics")
        sender.sendMessage(CosmeticHandler.getRegisteredCosmetics().joinToString(separator = "${ChatColor.RESET}, ", transform = { "${it.getName()} ${ChatColor.GRAY}- ${it.getUniqueId()}" }))
    }

}