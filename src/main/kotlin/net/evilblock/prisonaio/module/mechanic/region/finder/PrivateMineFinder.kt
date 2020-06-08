package net.evilblock.prisonaio.module.mechanic.region.finder

import net.evilblock.prisonaio.module.mechanic.region.Region
import net.evilblock.prisonaio.module.mechanic.region.RegionFinder
import net.evilblock.prisonaio.module.privatemine.PrivateMineHandler
import org.bukkit.Location

object PrivateMineFinder : RegionFinder {

    override fun findRegion(location: Location): Region? {
        val optionalMine = PrivateMineHandler.getMineByLocation(location)
        if (optionalMine.isPresent) {
            return optionalMine.get()
        }
        return null
    }

    override fun getPriority(): Int {
        return 25
    }

}