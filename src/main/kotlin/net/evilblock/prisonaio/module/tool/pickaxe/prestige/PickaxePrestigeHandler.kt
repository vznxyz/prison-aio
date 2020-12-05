/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.pickaxe.prestige

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.tool.ToolsModule
import java.io.File

object PickaxePrestigeHandler : PluginHandler {

    private val prestigeMap: MutableMap<Int, PickaxePrestige> = hashMapOf()

    override fun getModule(): PluginModule {
        return ToolsModule
    }

    override fun hasDefaultInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "pickaxe-prestige.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val listType = object : TypeToken<List<PickaxePrestige>>() {}.type
                val list = Cubed.gson.fromJson(reader, listType) as List<PickaxePrestige>

                for (prestige in list) {
                    trackPrestige(prestige)
                }
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(prestigeMap.values), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getPrestigeSet(): Collection<PickaxePrestige> {
        return prestigeMap.values
    }

    fun trackPrestige(prestige: PickaxePrestige) {
        prestigeMap[prestige.number] = prestige
    }

    fun forgetPrestige(prestige: PickaxePrestige) {
        prestigeMap.remove(prestige.number)
    }

    fun getPrestige(number: Int): PickaxePrestige? {
        return prestigeMap[number]
    }

    fun getNextPrestige(current: Int): PickaxePrestige? {
        return prestigeMap[current + 1]
    }

    fun getMaxPrestige(): PickaxePrestige? {
        return prestigeMap.maxBy { it.key }?.value
    }

}