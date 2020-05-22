package net.evilblock.prisonaio.util.plot

import com.intellectualcrafters.plot.PS
import com.intellectualcrafters.plot.`object`.Plot
import org.bukkit.Location

object PlotUtil {

    @JvmStatic
    fun getPlot(loc: Location) : Plot? {
        val plotLocation = com.intellectualcrafters.plot.`object`.Location(loc.world.name, loc.blockX, loc.blockY, loc.blockZ)
        val plotArea = PS.get().getApplicablePlotArea(plotLocation)
        if (plotArea != null) {
            val plot = plotArea.getPlot(plotLocation)
            if (plot != null) {
                return plot
            }
        }
        return null
    }

}