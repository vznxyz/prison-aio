package net.evilblock.prisonaio.module.rank.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.rank.RankHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.command.CommandSender

object SetRankSetScaleMultiplierCommand {

    @Command(
        names = ["prison rank set-scale-multi"],
        description = "Set the rank price scale multiplier",
        permission = Permissions.SYSTEM_ADMIN
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "multi") multiplier: Double) {
        RankHandler.priceScaleMultiplier = multiplier
        sender.sendMessage("Set scale multiplier to $multiplier")
    }

}