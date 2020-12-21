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
import net.evilblock.prisonaio.module.tool.enchant.Enchant
import java.io.File

object PickaxePrestigeHandler : PluginHandler() {

    private val prestigeInfoMap: MutableMap<Int, PickaxePrestigeInfo> = hashMapOf()

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
                val listType = object : TypeToken<List<PickaxePrestigeInfo>>() {}.type
                val list = Cubed.gson.fromJson(reader, listType) as List<PickaxePrestigeInfo>

                for (prestige in list) {
                    trackPrestige(prestige)
                }
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(prestigeInfoMap.values), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getPrestigeSet(): Collection<PickaxePrestigeInfo> {
        return prestigeInfoMap.values
    }

    fun trackPrestige(prestige: PickaxePrestigeInfo) {
        prestigeInfoMap[prestige.number] = prestige
    }

    fun forgetPrestige(prestige: PickaxePrestigeInfo) {
        prestigeInfoMap.remove(prestige.number)
    }

    fun getPrestige(number: Int): PickaxePrestigeInfo? {
        return prestigeInfoMap[number]
    }

    fun getNextPrestige(current: Int): PickaxePrestigeInfo? {
        return prestigeInfoMap[current + 1]
    }

    fun getMaxPrestige(): PickaxePrestigeInfo? {
        return prestigeInfoMap.maxBy { it.key }?.value
    }

    // find next prestige limits + add all of the previous limits
    fun findEnchantLimits(prestige: Int): Map<Enchant, Int> {
        return hashMapOf<Enchant, Int>().also { map ->
            val nextPrestige = getNextPrestige(prestige)
            if (nextPrestige != null) {
                map.putAll(nextPrestige.enchantLimits)

                for (prestigeInfo in getPrestigeSet()) {
                    if (prestige <= prestigeInfo.number) {
                        for ((enchant, limit) in prestigeInfo.enchantLimits) {
                            if (map.containsKey(enchant)) {
                                if (map[enchant]!! > limit) {
                                    map[enchant] = limit
                                }
                            } else {
                                map[enchant] = limit
                            }
                        }
                    }
                }
            } else {
                val maxPrestige = getMaxPrestige()
                if (maxPrestige != null) {
                    map.putAll(maxPrestige.enchantLimits)
                }
            }
        }
    }

}