package net.evilblock.prisonaio.module.gang.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.prisonaio.module.gang.GangHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object GangTrophiesResetAllCommand {

    @Command(
        names = ["gang admin trophies reset-all"],
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        if (sender is Player) {
            ConfirmMenu { confirmed ->
                if (confirmed) {
                    reset(sender)
                }
            }.openMenu(sender)
        } else {
            reset(sender)
        }
    }

    private fun reset(sender: CommandSender) {
        var reset = 0
        for (gang in GangHandler.getAllGangs()) {
            gang.setTrophies(0)
            reset++
        }

        GangHandler.saveGrid()

        sender.sendMessage("${ChatColor.GREEN}Reset $reset gangs trophies!")
    }

}