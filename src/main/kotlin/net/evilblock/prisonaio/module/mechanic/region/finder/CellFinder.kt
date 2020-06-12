package net.evilblock.prisonaio.module.mechanic.region.finder

import net.evilblock.prisonaio.module.cell.CellHandler
import net.evilblock.prisonaio.module.mechanic.region.Region
import net.evilblock.prisonaio.module.mechanic.region.RegionFinder
import org.bukkit.Location

object CellFinder : RegionFinder {

    override fun findRegion(location: Location): Region? {
        val cell = CellHandler.getCellByLocation(location)
        if (cell != null) {
            return cell
        }
        return null
    }

    override fun getPriority(): Int {
        return 50
    }

}