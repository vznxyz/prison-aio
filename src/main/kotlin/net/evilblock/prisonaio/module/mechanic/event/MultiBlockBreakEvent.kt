/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.event

import net.evilblock.prisonaio.module.PluginEvent
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable

class MultiBlockBreakEvent(val player: Player,
                           val block: Block,
                           val blockList: MutableList<Block>,
                           yield: Float) : PluginEvent(), Cancellable {

    var yield: Float = 100F.coerceAtMost(0F.coerceAtLeast(`yield`))
    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }

}