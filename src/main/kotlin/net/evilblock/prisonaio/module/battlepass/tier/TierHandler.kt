/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.tier

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.battlepass.BattlePassModule
import java.io.File

object TierHandler : PluginHandler {

    private val tiers: MutableMap<Int, Tier> = hashMapOf()

    override fun getModule(): PluginModule {
        return BattlePassModule
    }

    override fun hasDefaultInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "battle-pass-tiers.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val listType = object : TypeToken<List<Tier>>() {}.type
                val list = Cubed.gson.fromJson(reader, listType) as List<Tier>

                for (tier in list) {
                    tier.freeReward?.tier = tier
                    tier.premiumReward?.tier = tier

                    tiers[tier.number] = tier
                }
            }
        }

        for (i in 1..50) {
            tiers.putIfAbsent(i, Tier(i))
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(tiers.values), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getTiers(): List<Tier> {
        return tiers.values.toList()
    }

    fun getTierByNumber(number: Int): Tier? {
        return tiers[number]
    }

}