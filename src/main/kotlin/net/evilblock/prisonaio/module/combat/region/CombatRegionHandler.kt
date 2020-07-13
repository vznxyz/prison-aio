/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.region

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.module.combat.CombatModule
import net.evilblock.prisonaio.module.region.RegionsModule
import java.io.File

object CombatRegionHandler : PluginHandler {

    private val regions: MutableMap<String, CombatRegion> = hashMapOf()

    override fun getModule(): PluginModule {
        return CombatModule
    }

    override fun hasInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "combat-regions.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val dataType = object : TypeToken<List<CombatRegion>>() {}.type
                val data = Cubed.gson.fromJson(reader, dataType) as List<CombatRegion>

                for (region in data) {
                    trackRegion(region)
                    RegionsModule.updateBlockCache(region)
                }
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(regions.values), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getRegions(): List<CombatRegion> {
        return regions.values.toList()
    }

    fun getRegionById(id: String): CombatRegion? {
        return regions[id.toLowerCase()]
    }

    fun trackRegion(region: CombatRegion) {
        regions[region.id.toLowerCase()] = region
    }

    fun forgetRegion(region: CombatRegion) {
        regions.remove(region.id.toLowerCase())
    }

}