package net.evilblock.prisonaio.module.mine.variant.mineparty.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.mine.variant.mineparty.MinePartyHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object JoinEventCommand {

    @Command(
        names = ["mineparty", "mine-party", "mineparty join", "mine-party join"],
        description = "Join the active MineParty"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (CombatTimerHandler.isOnTimer(player)) {
            player.sendMessage("${ChatColor.RED}You can't join events while your combat timer is active!")
            return
        }

        if (!MinePartyHandler.isEventActive()) {
            player.sendMessage("${ChatColor.RED}There is no active event!")
            return
        }

        val event = MinePartyHandler.getEvent()!!
        if (event.spawnPoint == null) {
            player.sendMessage("${ChatColor.RED}The mine's spawn location hasn't been set!")
            return
        }

        player.teleport(MinePartyHandler.getEvent()!!.spawnPoint)
        player.sendMessage("${ChatColor.GREEN}You've been teleported to the event!")
    }

}