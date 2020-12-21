package net.evilblock.prisonaio.module.mine.variant.mineparty.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.mine.variant.mineparty.MinePartyHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object SetGoalCommand {

    @Command(
        names = ["mineparty set-goal", "mine-party set-goal"],
        description = "Sets the goal of a MineParty event",
        permission = Permissions.MINE_PARTY
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "goal") goal: Int) {
        if (!MinePartyHandler.isEventActive()) {
            sender.sendMessage("${ChatColor.RED}There is no active event!")
            return
        }

        MinePartyHandler.getEvent()!!.goal = goal
        
        sender.sendMessage("${ChatColor.GREEN}Set goal to $goal!")
    }

}