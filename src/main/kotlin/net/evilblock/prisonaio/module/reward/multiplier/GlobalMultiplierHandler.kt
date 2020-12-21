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

object GlobalMultiplierHandler : PluginHandler() {

    private var activeEvent: GlobalMultiplier? = null

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
                activeEvent = Cubed.gson.fromJson(reader, object : TypeToken<GlobalMultiplier>() {}.type) as GlobalMultiplier?
            }
        }

        Tasks.asyncTimer(10L, 10L) {
            if (activeEvent != null && System.currentTimeMillis() >= activeEvent!!.expires) {
                endEvent(activeEvent!!, false)
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(activeEvent), getInternalDataFile(), Charsets.UTF_8)
    }

    fun isSet(): Boolean {
        return activeEvent != null
    }

    fun getActiveMultiplier(): GlobalMultiplier? {
        return activeEvent
    }

    fun setActiveMultiplier(event: GlobalMultiplier) {
        activeEvent = event
        event.start()
    }

    fun endEvent(event: GlobalMultiplier, forced: Boolean) {
        activeEvent = null
        event.end(forced)
    }

}