package net.evilblock.prisonaio.module.mechanic.region.finder

import net.evilblock.prisonaio.module.cell.CellHandler
import net.evilblock.prisonaio.module.mechanic.region.Region
import net.evilblock.prisonaio.module.mechanic.region.RegionFinder
import org.bukkit.Location

object CellFinder : RegionFinder {

    override fun findRegion(location: Location): Region? {
        val optionalCell = CellHandler.getCellByLocation(location)
        if (optionalCell.isPresent) {
            return optionalCell.get()
        }
        return null
    }

    override fun getPriority(): Int {
        return 50
    }

}