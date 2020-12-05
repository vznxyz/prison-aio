/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.util.plot

import com.intellectualcrafters.plot.PS
import com.intellectualcrafters.plot.`object`.Plot
import com.intellectualcrafters.plot.`object`.PlotId
import org.bukkit.Location

object PlotUtil {

    @JvmStatic
    fun getPlot(loc: Location) : Plot? {
        val plotLocation = com.intellectualcrafters.plot.`object`.Location(loc.world.name, loc.blockX, loc.blockY, loc.blockZ)
        val plotArea = PS.get().getApplicablePlotArea(plotLocation)
        if (plotArea != null) {
            val plot = plotArea.getPlot(plotLocation)
            if (plot != null && plot.worldName == loc.world.name) {
                return plot
            }
        }
        return null
    }

}