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
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.reward.RewardsModule
import java.io.File

object MultiplierEventHandler : PluginHandler {

    private var activeEvent: MultiplierEvent? = null

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
                activeEvent = Cubed.gson.fromJson(reader, object : TypeToken<MultiplierEvent>() {}.type) as MultiplierEvent?
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(activeEvent), getInternalDataFile(), Charsets.UTF_8)
    }

    fun isEventActive(): Boolean {
        return activeEvent != null
    }

    fun getActiveEvent(): MultiplierEvent? {
        return activeEvent
    }

    fun startEvent(event: MultiplierEvent) {
        activeEvent = event
        event.start()
    }

    fun endEvent(event: MultiplierEvent, forced: Boolean) {
        activeEvent = null
        event.end(forced)
    }

}