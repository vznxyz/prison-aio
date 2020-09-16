/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region

import com.google.common.base.Charsets
import com.google.common.collect.Maps
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.practice.region.util.CoordSet2D
import net.evilblock.practice.region.util.CoordSet3D
import net.evilblock.prisonaio.module.region.impl.global.GlobalRegion
import org.bukkit.Location
import org.bukkit.entity.Player
import java.io.File

object RegionHandler : PluginHandler {

    private val DATA_TYPE = object : TypeToken<List<Region>>() {}.type

    private val regions: MutableMap<String, Region> = Maps.newConcurrentMap()
    private val regions2D: MutableMap<CoordSet2D, Region> = Maps.newConcurrentMap()
    private val regions3D: MutableMap<CoordSet3D, Region> = Maps.newConcurrentMap()
    private val defaultRegion: Region = GlobalRegion()

    override fun getModule(): PluginModule {
        return RegionsModule
    }

    override fun getInternalDataFile(): File {
        return File(File(getModule().getPluginFramework().dataFolder, "internal"), "regions.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        if (getInternalDataFile().exists()) {
            Files.newReader(getInternalDataFile(), Charsets.UTF_8).use { reader ->
                val data = Cubed.gson.fromJson(reader, DATA_TYPE) as List<Region>

                for (region in data) {
                    regions[region.id.toLowerCase()] = region
                    updateBlockCache(region)
                }
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(regions.values, DATA_TYPE), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getRegions(): List<Region> {
        return regions.values.toList()
    }

    fun findRegion(id: String): Region? {
        return regions[id.trim().toLowerCase()]
    }

    fun trackRegion(region: Region) {
        regions[region.id.toLowerCase()] = region
    }

    fun forgetRegion(region: Region) {
        regions.remove(region.id.toLowerCase())
    }

    fun findRegion(location: Location): Region {
        return regions3D[CoordSet3D(location.world, location.x.toInt(), location.y.toInt(), location.z.toInt())]
            ?: regions2D[CoordSet2D(location.world, location.x.toInt(), location.y.toInt())]
            ?: defaultRegion
    }

    fun findRegion(player: Player): Region {
        return findRegion(player.location)
    }

    fun updateBlockCache(region: Region) {
        if (region.getCuboid() != null) {
            val cuboid = region.getCuboid()!!

            if (region.is3D()) {
                for (x in (cuboid.lowerX)..(cuboid.upperX)) {
                    for (y in (cuboid.lowerY)..(cuboid.upperY)) {
                        for (z in (cuboid.lowerZ)..(cuboid.upperZ)) {
                            val coordSet = CoordSet3D(cuboid.world, x, y, z)

                            val cachedRegion = regions3D[coordSet]
                            if (cachedRegion != null && cachedRegion.getPriority() > region.getPriority()) {
                                continue
                            }

                            regions3D[coordSet] = region
                        }
                    }
                }
            } else {
                for (x in (region.getCuboid()!!.lowerX)..(region.getCuboid()!!.upperX)) {
                    for (z in (region.getCuboid()!!.lowerZ)..(region.getCuboid()!!.upperZ)) {
                        val coordSet = CoordSet2D(region.getCuboid()!!.world, x, z)

                        val cachedRegion = regions2D[coordSet]
                        if (cachedRegion != null && cachedRegion.getPriority() > region.getPriority()) {
                            continue
                        }

                        regions2D[coordSet] = region
                    }
                }
            }
        }
    }

    fun clearBlockCache(region: Region) {
        if (region.getCuboid() != null) {
            val cuboid = region.getCuboid()!!

            if (region.is3D()) {
                for (x in (cuboid.lowerX)..(cuboid.upperX)) {
                    for (y in (cuboid.lowerY)..(cuboid.upperY)) {
                        for (z in (cuboid.lowerZ)..(cuboid.upperZ)) {
                            val coordSet = CoordSet3D(cuboid.world, x, y, z)

                            val cachedRegion = regions3D[coordSet]
                            if (cachedRegion != null && cachedRegion.getPriority() > region.getPriority()) {
                                continue
                            }

                            regions3D.remove(coordSet)
                        }
                    }
                }
            } else {
                for (x in (cuboid.lowerX)..(cuboid.upperX)) {
                    for (y in (cuboid.lowerY)..(cuboid.upperY)) {
                        for (z in (cuboid.lowerZ)..(cuboid.upperZ)) {
                            val coordSet = CoordSet2D(cuboid.world, x, z)

                            val cachedRegion = regions2D[coordSet]
                            if (cachedRegion != null && cachedRegion.getPriority() > region.getPriority()) {
                                continue
                            }

                            regions2D.remove(coordSet)
                        }
                    }
                }
            }
        }
    }

    fun getDefaultRegion(): Region {
        return defaultRegion
    }

}