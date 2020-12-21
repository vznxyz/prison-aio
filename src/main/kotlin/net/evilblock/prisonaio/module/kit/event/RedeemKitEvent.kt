package net.evilblock.kits.event

import net.evilblock.cubed.plugin.PluginEvent
import net.evilblock.kits.Kit
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable

class RedeemKitEvent(val player: Player, val kit: Kit) : PluginEvent(), Cancellable {

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

}