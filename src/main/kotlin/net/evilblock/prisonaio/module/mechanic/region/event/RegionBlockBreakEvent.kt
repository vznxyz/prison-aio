package net.evilblock.prisonaio.module.mechanic.region.event

import net.evilblock.prisonaio.module.PluginEvent
import net.evilblock.prisonaio.module.mechanic.region.Region
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable

class RegionBlockBreakEvent(val player: Player, val region: Region, val block: Block) : PluginEvent(), Cancellable {

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }

}