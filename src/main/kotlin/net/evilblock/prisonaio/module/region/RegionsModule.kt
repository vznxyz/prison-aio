/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region

import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.region.listener.RegionListeners
import net.evilblock.prisonaio.module.region.listener.RegionWandListeners
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.module.region.command.RegionBypassCommand
import net.evilblock.prisonaio.module.region.command.RegionDebugCommand
import net.evilblock.prisonaio.module.region.command.RegionWandCommand
import net.evilblock.prisonaio.module.region.impl.safezone.SafeZoneRegion
import org.bukkit.Location
import org.bukkit.event.Listener

object RegionsModule : PluginModule() {

    private val regionBlockCache: MutableMap<RegionCoordSet, Region> = HashMap(86000)
    private val defaultRegion: Region = SafeZoneRegion()

    override fun getName(): String {
        return "Regions"
    }

    override fun getConfigFileName(): String {
        return "regions"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {

    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            RegionBypassCommand.javaClass,
            RegionWandCommand.javaClass,
            RegionDebugCommand.javaClass
        )
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            RegionListeners,
            RegionBypass,
            RegionWandListeners
        )
    }

    fun findRegion(location: Location): Region {
        val region = regionBlockCache[RegionCoordSet(location.world, location.x.toInt(), location.z.toInt())]
        if (region != null) {
            if (region.is3D()) {
                if (!region.getCuboid()!!.contains(location)) {
                    return defaultRegion
                }
            }
        }
        return region ?: defaultRegion
    }

    fun updateBlockCache(region: Region) {
        if (region.getCuboid() != null) {
            for (x in (region.getCuboid()!!.lowerX)..(region.getCuboid()!!.upperX)) {
                for (z in (region.getCuboid()!!.lowerZ)..(region.getCuboid()!!.upperZ)) {
                    regionBlockCache[RegionCoordSet(region.getCuboid()!!.world, x, z)] = region
                }
            }
        }
    }

    fun clearBlockCache(region: Region) {
        if (region.getCuboid() != null) {
            for (x in (region.getCuboid()!!.lowerX)..(region.getCuboid()!!.upperX)) {
                for (z in (region.getCuboid()!!.lowerZ)..(region.getCuboid()!!.upperZ)) {
                    regionBlockCache.remove(RegionCoordSet(region.getCuboid()!!.world, x, z))
                }
            }
        }
    }

    fun getDefaultRegion(): Region {
        return defaultRegion
    }

    fun canOpenEnderChestInGlobalRegion(): Boolean {
        return config.getBoolean("global-region.allow-open-enderchest", true)
    }

}