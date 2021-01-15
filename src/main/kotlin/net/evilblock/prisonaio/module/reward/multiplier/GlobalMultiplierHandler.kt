/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.multiplier

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.reward.RewardsModule
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object GlobalMultiplierHandler : PluginHandler() {

    private val activeEvents: ConcurrentHashMap<GlobalMultiplierType, GlobalMultiplier> = ConcurrentHashMap()

    override fun getModule(): PluginModule {
        return RewardsModule
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "multiplier-events.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val events = Cubed.gson.fromJson(reader, object : TypeToken<Collection<GlobalMultiplier>>() {}.type) as Collection<GlobalMultiplier>
                for (event in events) {
                    startEvent(event)
                }
            }
        }

        Tasks.asyncTimer(10L, 10L) {
            val expired = arrayListOf<GlobalMultiplier>()

            for (event in activeEvents.values) {
                if (event.isExpired()) {
                    expired.add(event)
                }
            }

            for (event in expired) {
                endEvent(event, false)
            }
        }

        loaded = true
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(activeEvents.values, object : TypeToken<Collection<GlobalMultiplier>>() {}.type), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getEvent(type: GlobalMultiplierType): GlobalMultiplier? {
        return activeEvents[type]
    }

    fun startEvent(event: GlobalMultiplier) {
        activeEvents[event.type] = event
        event.start()
    }

    fun endEvent(event: GlobalMultiplier, forced: Boolean) {
        activeEvents.remove(event.type)
        event.end(forced)
    }

}