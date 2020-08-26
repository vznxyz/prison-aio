/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.event.game.arena

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.minigame.MinigamesModule
import java.io.File
import java.util.ArrayList

object EventGameArenaHandler : PluginHandler {

    private val arenas: MutableList<EventGameArena> = ArrayList()

    override fun getModule(): PluginModule {
        return MinigamesModule
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "event-arenas.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val dataType = object : TypeToken<List<EventGameArena>>() {}.type
                val data = Cubed.gson.fromJson(reader, dataType) as List<EventGameArena>

                for (arena in data) {
                    trackArena(arena)
                }
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(arenas), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getArenas(): List<EventGameArena> {
        return arenas
    }

    fun getArenaByName(name: String): EventGameArena? {
        return arenas.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }

    fun trackArena(arena: EventGameArena) {
        arenas.add(arena)
    }

    fun forgetArena(arena: EventGameArena) {
        arenas.remove(arena)
    }

}