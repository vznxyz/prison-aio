package net.evilblock.prisonaio.module.environment.setting

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

object SettingHandler : PluginHandler {

    private val settings: MutableMap<Setting, Any?> = EnumMap(Setting::class.java)

    override fun getModule(): PluginModule {
        return EnvironmentModule
    }

    override fun hasInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "environment-settings.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        if (getInternalDataFile().exists()) {
            Files.newReader(getInternalDataFile(), Charsets.UTF_8).use { reader ->
                val dataType = object : TypeToken<Map<Setting, Any?>>() {}.type
                val data = Cubed.gson.fromJson(reader, dataType) as Map<Setting, Any?>

                settings.putAll(data)
            }
        }

        for (setting in Setting.values()) {
            if (!settings.containsKey(setting)) {
                settings[setting] = setting.defaultValue
            }
        }
    }

    override fun saveData() {
        Files.write(Cubed.gson.toJson(settings), getInternalDataFile(), Charsets.UTF_8)
    }

    fun <T> getSetting(setting: Setting): T {
        return settings[setting] as T
    }

    fun <T> updateSetting(setting: Setting, value: T) {
        settings[setting] = value
    }

}