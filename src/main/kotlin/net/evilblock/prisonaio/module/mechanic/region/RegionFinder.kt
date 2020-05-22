package net.evilblock.prisonaio.module.mechanic.region

import org.bukkit.Location

interface RegionFinder {

    fun findRegion(location: Location): Region?

    fun getPriority(): Int

}