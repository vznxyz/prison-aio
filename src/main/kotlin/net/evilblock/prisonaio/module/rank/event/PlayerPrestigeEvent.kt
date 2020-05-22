package net.evilblock.prisonaio.module.rank.event

import net.evilblock.prisonaio.module.PluginEvent
import net.evilblock.prisonaio.module.rank.Rank
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable

class PlayerPrestigeEvent(val player: Player, val from: Int, val to: Int) : PluginEvent(), Cancellable {

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }

}