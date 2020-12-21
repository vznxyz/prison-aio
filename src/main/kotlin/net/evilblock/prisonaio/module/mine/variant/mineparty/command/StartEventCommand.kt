package net.evilblock.prisonaio.module.mine.variant.mineparty.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.Duration
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.variant.mineparty.MinePartyHandler
import net.evilblock.prisonaio.module.mine.variant.mineparty.MinePartyMine
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object StartEventCommand {

    @Command(
        names = ["mineparty start", "mine-party start"],
        description = "Start a MineParty event",
        permission = Permissions.MINE_PARTY,
        async = true
    )
    @JvmStatic
    fun execute(
        sender: CommandSender,
        @Param(name = "mine") mine: Mine,
        @Param(name = "goal") goal: Int,
        @Param(name = "duration") duration: Duration
    ) {
        if (MinePartyHandler.isEventActive()) {
            sender.sendMessage("${ChatColor.RED}There is already an active event!")
            return
        }

        if (mine !is MinePartyMine) {
            sender.sendMessage("${ChatColor.RED}The mine must be a MineParty mine!")
            return
        }

        if (mine.getBreakableCuboid() == null || !mine.blocksConfig.blockTypes.any { it.percentage > 0 }) {
            sender.sendMessage("${ChatColor.RED}That mine hasn't been completely setup!")
            return
        }

        if (goal <= 0) {
            sender.sendMessage("${ChatColor.RED}You must provide a valid goal! (Must be > 0)")
            return
        }

        if (duration.get() <= 0) {
            sender.sendMessage("${ChatColor.RED}You must provide a valid duration! (Must be > 0)")
            return
        }

        MinePartyHandler.startEvent(mine, goal, duration)
    }

}