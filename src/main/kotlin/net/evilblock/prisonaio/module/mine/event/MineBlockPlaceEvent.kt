/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.event

import net.evilblock.cubed.plugin.PluginEvent
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