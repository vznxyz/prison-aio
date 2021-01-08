package net.evilblock.prisonaio.module.robot.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.robot.RobotUtils
import net.evilblock.prisonaio.module.robot.RobotsModule
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object GiveRobotCommand {

    @Command(
            names = ["robots give robot", "robot give robot"],
            description = "Give a Robot to a player",
            permission = "robots.give.robot"
    )
    @JvmStatic
    fun execute(
            sender: CommandSender,
            @Param(name = "player") player: Player,
            @Param(name = "amount", defaultValue = "1") amount: Int,
            @Param(name = "tier", defaultValue = "0") tier: Int
    ) {
        if (tier > RobotsModule.getMaxTiers()) {
            sender.sendMessage("${ChatColor.RED}You can't give a Tier $tier Robot as it's higher than the max tier!")
            return
        }

        val robotItemStack = RobotUtils.makeRobotItem(amount, tier)

        if (player.inventory.firstEmpty() == -1) {
            val existingSlot = player.inventory.first(robotItemStack)
            if (existingSlot == -1 || player.inventory.getItem(existingSlot)?.amount ?: 0 < 64) {
                sender.sendMessage("${ChatColor.RED}Player has no open inventory space.")
                return
            }
        }

        player.inventory.addItem(robotItemStack)
        player.updateInventory()

        player.sendMessage("${RobotsModule.CHAT_PREFIX}You were given ${ChatColor.RED}${amount}x ${ChatColor.BOLD}Robot${ChatColor.GRAY}!")
        sender.sendMessage("${RobotsModule.CHAT_PREFIX}You gave ${ChatColor.WHITE}${player.name} ${ChatColor.RED}${amount}x ${ChatColor.BOLD}Robot${ChatColor.GRAY}!")
    }

}