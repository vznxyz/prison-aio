package net.evilblock.prisonaio.module.environment.analytic.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.environment.analytic.Analytic
import net.evilblock.prisonaio.module.environment.analytic.AnalyticHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object WipeAnalyticsCommand {

    @Command(
        names = ["prison analytics wipe"],
        description = "Resets all tracked analytics data",
        permission = Permissions.SYSTEM_ADMIN
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        Analytic.values().forEach { it.updateValue(it.defaultValue) }
        AnalyticHandler.saveData()

        sender.sendMessage("${ChatColor.GREEN}Successfully wiped tracked analytics.")
    }

}