/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.global

import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.region.bitmask.BitmaskRegion
import net.evilblock.prisonaio.module.region.bitmask.RegionBitmask
import net.evilblock.prisonaio.util.plot.PlotUtil
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable

class GlobalRegion : BitmaskRegion("__global__", null) {

    init {
        setBitmask(RegionBitmask.SAFE_ZONE.bitmaskValue)
    }

    override fun getRegionName(): String {
        return "${ChatColor.GRAY}${ChatColor.BOLD}Wilderness"
    }

    override fun getPriority(): Int {
        return 999
    }

    override fun onRightClickBlock(player: Player, clickedBlock: Block, cancellable: Cancellable) {
        if (PlotUtil.getPlot(clickedBlock.location) != null) {
            return
        }

        if (clickedBlock.type == Material.ENDER_CHEST) {
            return
        }

        if (Constants.CONTAINER_TYPES.contains(clickedBlock.type)) {
            cancellable.isCancelled = true
        }

        if (Constants.INTERACTIVE_TYPES.contains(clickedBlock.type)) {
            cancellable.isCancelled = true
        }
    }

}