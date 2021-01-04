package net.evilblock.prisonaio.module.gang.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.prisonaio.module.gang.GangHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object GangForceInvitesResetAllCommand {

    @Command(
        names = ["gang admin force-invites reset-all"],
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "amount") amount: Int) {
        if (sender is Player) {
            ConfirmMenu { confirmed ->
                if (confirmed) {
                    reset(sender, amount)
                }
            }.openMenu(sender)
        } else {
            reset(sender, amount)
        }
    }

    private fun reset(sender: CommandSender, amount: Int) {
        var reset = 0
        for (gang in GangHandler.getAllGangs()) {
            gang.setForceInvites(amount)
            reset++
        }

        GangHandler.saveGrid()

        sender.sendMessage("${ChatColor.GREEN}Reset $reset gangs force-invites!")
    }

}