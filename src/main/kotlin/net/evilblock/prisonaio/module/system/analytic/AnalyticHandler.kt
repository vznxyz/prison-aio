/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.analytic

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.system.SystemModule
import java.io.File
import java.util.*

object AnalyticHandler : PluginHandler {

    private val analytics: MutableMap<Analytic, Any?> = EnumMap(Analytic::class.java)

    override fun getModule(): PluginModule {
        return SystemModule
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "system-analytics.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        if (getInternalDataFile().exists()) {
            Files.newReader(getInternalDataFile(), Charsets.UTF_8).use { reader ->
                val dataType = object : TypeToken<Map<Analytic, Any?>>() {}.type
                val data = Cubed.gson.fromJson(reader, dataType) as Map<Analytic, Any?>

                analytics.putAll(data)
            }
        }

        for (analytic in Analytic.values()) {
            if (!analytics.containsKey(analytic)) {
                analytics[analytic] = analytic.defaultValue
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(analytics), getInternalDataFile(), Charsets.UTF_8)
    }

    fun <T> getValue(analytic: Analytic): T {
        return analytics[analytic] as T
    }

    fun <T> updateValue(analytic: Analytic, value: T) {
        analytics[analytic] = value
    }

}