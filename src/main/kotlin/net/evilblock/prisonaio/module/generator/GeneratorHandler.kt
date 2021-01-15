/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator

import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import com.intellectualcrafters.plot.`object`.Plot
import com.intellectualcrafters.plot.`object`.PlotId
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.backup.BackupHandler
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.generator.build.GeneratorBuildLevel
import net.evilblock.prisonaio.module.generator.impl.core.CoreGenerator
import net.evilblock.prisonaio.module.generator.schematic.Schematic
import net.evilblock.prisonaio.module.generator.schematic.SchematicBlock
import net.evilblock.prisonaio.module.generator.schematic.rotate.RotateUtil
import net.evilblock.prisonaio.module.generator.schematic.rotate.RotatedSchematic
import net.evilblock.prisonaio.module.generator.schematic.rotate.Rotation
import net.evilblock.prisonaio.module.generator.service.GeneratorTickService
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.service.ServiceRegistry
import net.evilblock.prisonaio.util.plot.PlotUtil
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.experimental.and

object GeneratorHandler : PluginHandler() {

    val CHAT_PREFIX = "${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}Generators${ChatColor.GRAY}] "

    private val schematics: MutableMap<String, EnumMap<Rotation, RotatedSchematic>> = ConcurrentHashMap()
    private val levels: MutableMap<GeneratorType, Array<GeneratorBuildLevel>> = ConcurrentHashMap()

    private val generators: MutableSet<Generator> = ConcurrentHashMap.newKeySet()
    private val generatorsByPlot: MutableMap<PlotId, MutableSet<Generator>> = ConcurrentHashMap()

    override fun getModule(): PluginModule {
        return GeneratorsModule
    }

    override fun getInternalDataFile(): File {
        return File(File(getModule().getPluginFramework().dataFolder, "internal"), "generators.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        loadSchematics()
        loadLevels()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.copy(getInternalDataFile(), BackupHandler.findNextBackupFile("generators"))

            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val list = Cubed.gson.fromJson(reader.readLine(), object : TypeToken<List<Generator>>() {}.type) as List<Generator>
                for (generator in list) {
                    generator.initializeData()
                    trackGenerator(generator)
                }
            }
        }

        ServiceRegistry.register(GeneratorTickService, 20L, 20L)

        loaded = true
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(generators, object : TypeToken<Set<Generator>>() {}.type), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getGenerators(): Set<Generator> {
        return generators
    }

    fun getGeneratorsByPlot(plot: Plot): Set<Generator> {
        return generatorsByPlot.getOrDefault(plot.id, emptySet())
    }

    fun getGeneratorsByPlot(plotId: PlotId): Set<Generator> {
        return generatorsByPlot.getOrDefault(plotId, emptySet())
    }

    fun getCoreByPlot(plot: PlotId): CoreGenerator? {
        return getGeneratorsByPlot(plot).firstOrNull { it.getGeneratorType() == GeneratorType.CORE } as CoreGenerator?
    }

    fun getCoreByPlot(plot: Plot): CoreGenerator? {
        return getGeneratorsByPlot(plot).firstOrNull { it.getGeneratorType() == GeneratorType.CORE } as CoreGenerator?
    }

    fun trackGenerator(generator: Generator) {
        generators.add(generator)

        if (generatorsByPlot.containsKey(generator.plotId)) {
            generatorsByPlot[generator.plotId]!!.add(generator)
        } else {
            generatorsByPlot[generator.plotId] = ConcurrentHashMap.newKeySet<Generator>().also { it.add(generator) }
        }

        RegionHandler.trackRegion(generator)
        RegionHandler.updateBlockCache(generator)
    }

    fun forgetGenerator(generator: Generator) {
        generators.remove(generator)

        if (generatorsByPlot.containsKey(generator.plotId)) {
            generatorsByPlot[generator.plotId]!!.remove(generator)
        }

        RegionHandler.forgetRegion(generator)
        RegionHandler.clearBlockCache(generator)
    }

    fun getSchematic(file: String, rotation: Rotation): RotatedSchematic {
        return schematics[file].let {
            if (it == null) {
                throw IllegalStateException("Failed to find schematic file `$file`")
            } else {
                it[rotation]!!
            }
        }
    }

    private fun loadSchematics() {
        val folder = File(getModule().getPluginFramework().dataFolder, "schematics")
        if (!folder.exists()) {
            return
        }

        val schematicFiles: Array<File> = folder.listFiles() ?: return
        for (file in schematicFiles) {
            if (file.name.endsWith(".schematic")) {
                loadSchematic(file)
            }
        }
    }

    private fun loadSchematic(file: File) {
        val rotationMap = EnumMap<Rotation, RotatedSchematic>(Rotation::class.java)
        schematics[file.name] = rotationMap

        val schematic = Schematic.loadSchematic(file)
        if (schematic == null) {
            Bukkit.getLogger().warning("Invalid schematic " + file.name)
            return
        }

        for (rotation in Rotation.values()) {
            val rotated: Array<Array<Array<SchematicBlock>>> = rotation.rotate(schematic.getBlockMap(), Array<Array<Array<SchematicBlock>>>::class.java)
            val noAirBlocks = arrayListOf<SchematicBlock>()

            var villager = Vector(0, 0, 0)
            var villagerYaw = 0F

            for (y in rotated[0].indices) {
                for (x in rotated.indices) {
                    for (z in rotated[0][0].indices) {
                        val block = rotated[x][y][z]
                        if (block.getMaterial() !== Material.AIR) {
                            if (block.getMaterial().name.startsWith("REDSTONE_COMPARATOR")) {
                                villager = Vector(x + 0.5, y.toDouble(), z + 0.5)

                                when ((block.data and 3).toInt()) {
                                    1 -> villagerYaw = 90f
                                    2 -> villagerYaw = 180f
                                    3 -> villagerYaw = 270f
                                }

                                when (rotation.getOpposite()) {
                                    Rotation.EAST -> villagerYaw += 90f
                                    Rotation.SOUTH -> villagerYaw += 180f
                                    Rotation.WEST -> villagerYaw += 270f
                                    else -> {}
                                }

                                villagerYaw %= 360f
                            } else {
                                block.vector = Vector(x.toDouble(), y.toDouble(), z.toDouble())
                                block.data = RotateUtil.getDirectionalByte(block.getMaterial(), block.data, rotation)

                                noAirBlocks.add(block)
                            }
                        }
                    }
                }
            }

            val area = Vector(rotated.size - 1, rotated[0].size - 1, rotated[0][0].size - 1)

            val rotatedSchematic = RotatedSchematic(rotation, area, noAirBlocks.toTypedArray(), villager, villagerYaw)
            rotationMap[rotation] = rotatedSchematic
        }
    }

    private fun loadLevels() {
        for (type in GeneratorType.values()) {
            val loaded = arrayListOf<GeneratorBuildLevel>()

            val root = getModule().config.getConfigurationSection(type.configSection + ".levels")
            for (level in root.getKeys(false)) {
                val section = root.getConfigurationSection(level)
                loaded.add(type.createInstance.invoke(level.toInt(), section))
            }

            levels[type] = loaded.toTypedArray()
        }
    }

    fun getLevels(type: GeneratorType): Array<GeneratorBuildLevel> {
        return levels[type]!!
    }

    @JvmStatic
    fun isOccupiedArea(bounds: Cuboid, plot: Plot): Boolean {
        for (generator in generators) {
            if (generator.bounds.contains(bounds)) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun canAccess(player: Player): Boolean {
        val plot = PlotUtil.getPlot(player.location) ?: return false

        if (RegionBypass.hasBypass(player)) {
            RegionBypass.attemptNotify(player)
            return true
        }

        if (plot.isOwner(player.uniqueId)) {
            return true
        }

        return false
    }


}