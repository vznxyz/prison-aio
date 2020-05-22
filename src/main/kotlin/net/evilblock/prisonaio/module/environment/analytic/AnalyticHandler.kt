package net.evilblock.prisonaio.module.environment.analytic

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.PluginHandler
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.environment.EnvironmentModule
import java.io.File
import java.util.*

object AnalyticHandler : PluginHandler {

    private val analytics: MutableMap<Analytic, Any?> = EnumMap(Analytic::class.java)

    override fun getModule(): PluginModule {
        return EnvironmentModule
    }

    override fun hasInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "environment-analytics.json")
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
        Files.write(Cubed.gson.toJson(analytics), getInternalDataFile(), Charsets.UTF_8)
    }

    fun <T> getValue(analytic: Analytic): T {
        return analytics[analytic] as T
    }

    fun <T> updateValue(analytic: Analytic, value: T) {
        analytics[analytic] = value
    }

}