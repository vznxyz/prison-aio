package net.evilblock.prisonaio.module.robot.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.robot.RobotUtils
import net.evilblock.prisonaio.module.robot.RobotsModule
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object GiveMechanicCommand {

    @Command(
            names = ["robots give mechanic", "robot give mechanic"],
            description = "Give a Robot Mechanic Egg to a player",
            permission = "robots.give.mechanic"
    )
    @JvmStatic
    fun execute(
            sender: CommandSender,
            @Param(name = "player") player: Player,
            @Param(name = "amount", defaultValue = "1") amount: Int
    ) {
        val eggItemStack = RobotUtils.makeMechanicEggItem(amount)

        if (player.inventory.firstEmpty() == -1) {
            val existingSlot = player.inventory.first(eggItemStack)
            if (existingSlot == -1 || player.inventory.getItem(existingSlot)?.amount ?: 0 < 64) {
                sender.sendMessage("${ChatColor.RED}Player has no open inventory space.")
                return
            }
        }

        player.inventory.addItem(eggItemStack)
        player.updateInventory()

        player.sendMessage("${RobotsModule.CHAT_PREFIX}You were given ${ChatColor.RED}${amount}x ${ChatColor.BOLD}Robot Mechanic Egg${ChatColor.GRAY}!")
        sender.sendMessage("${RobotsModule.CHAT_PREFIX}You gave ${ChatColor.WHITE}${player.name} ${ChatColor.RED}${amount}x ${ChatColor.BOLD}Robot Mechanic Egg${ChatColor.GRAY}!")
    }

}