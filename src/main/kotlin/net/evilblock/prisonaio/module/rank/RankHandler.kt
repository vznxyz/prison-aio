/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.rank

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object RankHandler : PluginHandler {

    private val ranksMap: MutableMap<String, Rank> = hashMapOf()

    override fun getModule(): PluginModule {
        return RanksModule
    }

    override fun hasInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "ranks.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val listType = object : TypeToken<List<Rank>>() {}.type
                val list = Cubed.gson.fromJson(reader, listType) as List<Rank>

                for (rank in list) {
                    ranksMap[rank.id.toLowerCase()] = rank
                }
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(ranksMap.values), getInternalDataFile(), Charsets.UTF_8)
    }

    fun trackRank(rank: Rank) {
        ranksMap[rank.id.toLowerCase()] = rank
    }

    fun forgetRank(rank: Rank) {
        ranksMap.remove(rank.id.toLowerCase())
    }

    fun getSortedRanks(): List<Rank> {
        return ArrayList(ranksMap.values.sortedBy { rank -> rank.sortOrder })
    }

    fun getRankById(id: String): Optional<Rank> {
        return Optional.ofNullable(ranksMap[id.toLowerCase()])
    }

    fun getStartingRank(): Rank {
        return getSortedRanks().first()
    }

    fun getLastRank(): Rank {
        return getSortedRanks().last()
    }

    fun getPreviousRank(rank: Rank): Optional<Rank> {
        val sortedRanks = getSortedRanks()
        val indexOf = sortedRanks.indexOf(rank)

        if (indexOf == -1) {
            return Optional.empty()
        }

        if (indexOf == 0) {
            return Optional.empty()
        }

        return Optional.of(sortedRanks[indexOf - 1])
    }

    fun getNextRank(rank: Rank): Optional<Rank> {
        val sortedRanks = getSortedRanks()
        val indexOf = sortedRanks.indexOf(rank)

        if (indexOf == -1) {
            return Optional.empty()
        }

        if (indexOf + 1 >= sortedRanks.size) {
            return Optional.empty()
        }

        return Optional.of(sortedRanks[indexOf + 1])
    }

}