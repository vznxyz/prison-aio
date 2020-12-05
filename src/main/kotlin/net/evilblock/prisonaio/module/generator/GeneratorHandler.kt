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
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.generator.impl.core.CoreGenerator
import net.evilblock.prisonaio.module.generator.schematic.Schematic
import net.evilblock.prisonaio.module.generator.schematic.SchematicBlock
import net.evilblock.prisonaio.module.generator.schematic.rotate.RotateUtil
import net.evilblock.prisonaio.module.generator.schematic.rotate.RotatedSchematic
import net.evilblock.prisonaio.module.generator.schematic.rotate.Rotation
import net.evilblock.prisonaio.module.generator.service.GeneratorTickService
import net.evilblock.prisonaio.service.ServiceRegistry
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.util.Vector
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object GeneratorHandler : PluginHandler {

    val CHAT_PREFIX = "${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}Generators${ChatColor.GRAY}] "

    private val schematics: MutableMap<String, EnumMap<Rotation, RotatedSchematic>> = ConcurrentHashMap()

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

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val list = Cubed.gson.fromJson(reader.readLine(), object : TypeToken<List<Generator>>() {}.type) as List<Generator>
                for (generator in list) {
                    generator.initializeData()
                    trackGenerator(generator)
                }
            }
        }

        ServiceRegistry.register(GeneratorTickService)
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
    }

    fun forgetGenerator(generator: Generator) {
        generators.remove(generator)

        if (generatorsByPlot.containsKey(generator.plotId)) {
            generatorsByPlot[generator.plotId]!!.remove(generator)
        }
    }

    fun getSchematic(file: String, rotation: Rotation): RotatedSchematic? {
        return schematics[file].let { if (it == null) null else it[rotation]!! }
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
                            if (block.getMaterial() == Material.SKULL) {
                                villager = Vector(x + 0.5, y.toDouble(), z + 0.5)
                                villagerYaw = block.data.toFloat()
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

    @JvmStatic
    fun isOccupiedArea(bounds: Cuboid, plot: Plot): Boolean {
        for (generator in generators) {
            if (generator.bounds.contains(bounds)) {
                return true
            }
        }
        return false
    }

}