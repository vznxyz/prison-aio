package net.evilblock.prisonaio.module.environment.analytic.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object AnalyticsCommand {

    @Command(
        names = ["prison analytics"],
        description = "Opens the Analytics menu",
        permission = Permissions.SYSTEM_ADMIN
    )
    @JvmStatic
    fun execute(player: Player) {

    }

}