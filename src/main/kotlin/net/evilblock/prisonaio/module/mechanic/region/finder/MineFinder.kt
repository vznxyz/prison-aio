package net.evilblock.prisonaio.module.mechanic.region.finder

import net.evilblock.prisonaio.module.mechanic.region.Region
import net.evilblock.prisonaio.module.mechanic.region.RegionFinder
import net.evilblock.prisonaio.module.mine.MineHandler
import org.bukkit.Location

object MineFinder : RegionFinder {

    override fun findRegion(location: Location): Region? {
        val optionalMine = MineHandler.getMineByLocation(location)
        if (optionalMine.isPresent) {
            return optionalMine.get()
        }
        return null
    }

    override fun getPriority(): Int {
        return 100
    }

}