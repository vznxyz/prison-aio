/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.luckyblock

import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.Chance
import net.evilblock.cubed.util.bukkit.selection.InteractionHandler
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.mine.MinesModule
import net.evilblock.prisonaio.module.mine.variant.luckyblock.service.ProcessSpawnQueueService
import net.evilblock.prisonaio.service.ServiceRegistry
import java.io.File

object LuckyBlockHandler : PluginHandler {

    var disabled: Boolean = false

    val spawnSelectionHandler: InteractionHandler = InteractionHandler("LB_SPAWN_EDITOR")

    private val types: MutableMap<String, LuckyBlock> = hashMapOf()

    override fun getModule(): PluginModule {
        return MinesModule
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "lucky-block-types.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        Files.newReader(getInternalDataFile(), Charsets.UTF_8).use { reader ->
            val type = object : TypeToken<List<LuckyBlock>>() {}.type
            val list = Cubed.gson.fromJson(reader.readLine(), type) as List<LuckyBlock>

            for (luckyblock in list) {
                trackBlockType(luckyblock)
            }
        }

        ServiceRegistry.register(ProcessSpawnQueueService, 20L)
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(types.values), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getBlockTypes(): Collection<LuckyBlock> {
        return types.values
    }

    fun getBlockTypeById(id: String): LuckyBlock? {
        return types[id.toLowerCase()]
    }

    fun trackBlockType(block: LuckyBlock) {
        types[block.id.toLowerCase()] = block
    }

    fun forgetBlockType(block: LuckyBlock) {
        types.remove(block.id.toLowerCase())
    }

    fun pickRandomBlockType(): LuckyBlock? {
        if (types.isEmpty()) {
            return null
        }

        val hasChance = types.values.filter { it.isSetup() }
        if (hasChance.isEmpty()) {
            return null
        }

        return Chance.weightedPick(hasChance) { it.spawnChance }
    }

}