package net.evilblock.prisonaio.module.mechanic.region

import net.evilblock.prisonaio.module.mechanic.region.finder.CellFinder
import net.evilblock.prisonaio.module.mechanic.region.finder.MineFinder
import net.evilblock.prisonaio.module.mechanic.region.finder.PrivateMineFinder
import org.bukkit.Location

object Regions {

    @JvmStatic
    val regionFinders: MutableList<RegionFinder> = arrayListOf(MineFinder, CellFinder, PrivateMineFinder)

    @JvmStatic
    fun findRegion(location: Location): Region? {
        for (finder in regionFinders.sortedByDescending { it.getPriority() }) {
            val finderResult = finder.findRegion(location)
            if (finderResult != null) {
                return finderResult
            }
        }
        return null
    }

}