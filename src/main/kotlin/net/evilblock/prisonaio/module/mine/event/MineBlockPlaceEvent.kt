package net.evilblock.prisonaio.module.mine.event

import net.evilblock.prisonaio.module.PluginEvent
import net.evilblock.prisonaio.module.mine.Mine
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable

class MineBlockPlaceEvent(val player: Player, val block: Block, val mine: Mine) : PluginEvent(), Cancellable {

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }

}