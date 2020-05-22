package net.evilblock.prisonaio.module.enchant.event

import net.evilblock.prisonaio.module.PluginEvent
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable

class MineBombExplodeEvent(
    val player: Player,
    val blocks: List<Block>,
    val origin: Block,
    val level: Int
) : PluginEvent(), Cancellable {

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }

}