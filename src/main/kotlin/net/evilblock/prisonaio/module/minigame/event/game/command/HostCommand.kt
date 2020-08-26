package net.evilblock.prisonaio.module.minigame.event.game.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.minigame.event.game.menu.HostMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object HostCommand {

    @Command(
        names = ["host", "event host", "events host"],
        description = "Host an event",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        if (CombatTimerHandler.isOnTimer(player)) {
            player.sendMessage("${ChatColor.RED}You can't host an event while combat tagged!")
            return
        }

        HostMenu().openMenu(player)
    }

}