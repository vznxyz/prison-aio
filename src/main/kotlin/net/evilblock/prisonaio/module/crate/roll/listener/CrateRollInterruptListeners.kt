package net.evilblock.prisonaio.module.crate.roll.listener

import net.evilblock.prisonaio.module.crate.roll.CrateRollHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object CrateRollInterruptListeners : Listener {

    /**
     * Prematurely finishes the roll to force the player to receive the winnings before they are completely logged out.
     */
    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        if (CrateRollHandler.isRolling(event.player)) {
            val activeRoll = CrateRollHandler.getActiveRoll(event.player)
            activeRoll.finish(event.player)

            CrateRollHandler.forgetRoll(activeRoll)
        }
    }

}