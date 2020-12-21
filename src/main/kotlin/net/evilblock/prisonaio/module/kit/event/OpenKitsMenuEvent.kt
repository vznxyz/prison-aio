package net.evilblock.kits.event

import net.evilblock.cubed.plugin.PluginEvent
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable

class OpenKitsMenuEvent(val player: Player) : PluginEvent(), Cancellable {

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

}