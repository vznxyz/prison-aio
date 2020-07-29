/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.region.RegionsModule
import org.bukkit.entity.Player
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object MineHandler : PluginHandler {

    private val minesMap: HashMap<String, Mine> = hashMapOf()

    override fun getModule(): PluginModule {
        return MinesModule
    }

    override fun hasInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "mines.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val mapType = object : TypeToken<Map<String, Mine>>() {}.type
                val map = Cubed.gson.fromJson(reader, mapType) as Map<String, Mine>

                minesMap.putAll(map)
            }
        }

        Tasks.asyncDelayed(20L * 3) {
            for (mine in minesMap.values) {
                RegionsModule.updateBlockCache(mine)

                if (mine.region != null && mine.blocksConfig.blockTypes.isNotEmpty()) {
                    mine.resetRegion()
                }
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(minesMap), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getMines(): List<Mine> {
        return ArrayList(minesMap.values)
    }

    fun getMineById(id: String): Optional<Mine> {
        return Optional.ofNullable(minesMap[id.toLowerCase()])
    }

    fun createMine(id: String): Mine {
        val mine = Mine(id)
        minesMap[id.toLowerCase()] = mine
        return mine
    }

    fun deleteMine(id: String) {
        minesMap.remove(id)
    }

    fun deleteMine(mine: Mine) {
        deleteMine(mine.id)
    }

    fun getNearbyMine(player: Player): Optional<Mine> {
        for (mine in minesMap.values.filter { it.region != null }) {
            if (mine.isNearbyMine(player)) {
                return Optional.of(mine)
            }
        }
        return Optional.empty()
    }

}