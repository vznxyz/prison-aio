package net.evilblock.prisonaio.module.mine

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.PluginHandler
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.mine.util.CoordsIntPair
import org.bukkit.Location
import org.bukkit.entity.Player
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object MineHandler : PluginHandler {

    private val minesMap: HashMap<String, Mine> = hashMapOf()
    private var coordsMap: HashMap<CoordsIntPair, Mine> = hashMapOf()

    override fun getModule(): PluginModule {
        return MinesModule
    }

    override fun hasInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "mines.json")
    }

    /**
     * Recalculates the coordinates -> mine map, which is used as a processing efficient method of determining if a location (coordinate pair) is part of a mine's region.
     */
    fun recalculateCoordsMap() {
        val map = hashMapOf<CoordsIntPair, Mine>()

        for (mine in minesMap.values) {
            if (mine.region != null) {
                val region = mine.region!!

                for (x in region.lowerX..region.upperX) {
                    for (y in region.lowerY..region.upperY) {
                        for (z in region.lowerZ..region.upperZ) {
                            map[CoordsIntPair(Location(region.world, x.toDouble(), y.toDouble(), z.toDouble()))] = mine
                        }
                    }
                }
            }
        }

        coordsMap = map
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

        recalculateCoordsMap()

        Tasks.async {
            for (mine in minesMap.values) {
                if (mine.region != null) {
                    mine.resetRegion()
                }
            }
        }
    }

    override fun saveData() {
        Files.write(Cubed.gson.toJson(minesMap), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getMines(): List<Mine> {
        return ArrayList(minesMap.values)
    }

    fun getMineById(id: String): Optional<Mine> {
        return Optional.ofNullable(minesMap[id.toLowerCase()])
    }

    fun getMineByLocation(location: Location): Optional<Mine> {
        return getMineByLocation(CoordsIntPair(location))
    }

    fun getMineByLocation(coords: CoordsIntPair): Optional<Mine> {
        val mine = coordsMap[coords]
        if (mine != null) {
            return Optional.of(mine)
        }
        return Optional.empty()
    }

    fun createMine(id: String): Mine {
        val mine = Mine(id)
        minesMap[id.toLowerCase()] = mine
        return mine
    }

    fun deleteMine(id: String) {
        minesMap.remove(id)
        recalculateCoordsMap()
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