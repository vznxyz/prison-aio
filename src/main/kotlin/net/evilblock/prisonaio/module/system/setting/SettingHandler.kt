/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.setting

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

object SettingHandler : PluginHandler {

    private val settings: MutableMap<Setting, Any?> = EnumMap(Setting::class.java)

    override fun getModule(): PluginModule {
        return SystemModule
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "system-settings.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
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
        super.saveData()

        Files.write(Cubed.gson.toJson(settings), getInternalDataFile(), Charsets.UTF_8)
    }

    fun <T> getSetting(setting: Setting): T {
        return settings[setting] as T
    }

    fun <T> updateSetting(setting: Setting, value: T) {
        settings[setting] = value
    }

}