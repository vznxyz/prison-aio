/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warp

import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object WarpHandler : PluginHandler() {

    private val warps: MutableMap<String, Warp> = ConcurrentHashMap()

    override fun getModule(): PluginModule {
        return WarpsModule
    }

    override fun getInternalDataFile(): File {
        return File(File(getModule().getPluginFramework().dataFolder, "internal"), "warps.json")
    }

    fun getWarps(): Collection<Warp> {
        return warps.values
    }

    fun getWarpById(id: String): Warp? {
        return warps[id.toLowerCase()]
    }

    fun trackWarp(warp: Warp) {
        warps[warp.id.toLowerCase()] = warp
    }

    fun forgetWarp(warp: Warp) {
        warps.remove(warp.id.toLowerCase())
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val data = Cubed.gson.fromJson(reader.readLine(), object : TypeToken<List<Warp>>() {}.type) as List<Warp>

                for (warp in data) {
                    trackWarp(warp)
                }
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(warps.values, object : TypeToken<List<Warp>>() {}.type), getInternalDataFile(), Charsets.UTF_8)
    }

}