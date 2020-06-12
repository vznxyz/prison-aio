package net.evilblock.prisonaio.module.rank.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.rank.RankHandler
import net.evilblock.prisonaio.module.rank.RanksModule
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.command.CommandSender

object RanksReloadCommand {

    @Command(
        names = ["prison ranks reload"],
        description = "Reloads the PrisonAIO rank price multipliers which are based on prestige",
        permission = Permissions.SYSTEM_ADMIN
    )
    @JvmStatic
    fun execute(ender: CommandSender) {
        RanksModule.onReload()
    }

}