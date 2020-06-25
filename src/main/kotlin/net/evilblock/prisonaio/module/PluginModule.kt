/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.PrisonAIO
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.Listener
import java.io.File

abstract class PluginModule {

    var config: YamlConfiguration = loadConfig()

    fun isEnabled(): Boolean {
        return PrisonAIO.instance.enabledModules.contains(this)
    }

    private fun getConfigFile(): File {
        return File(getPlugin().dataFolder, getConfigFileName() + ".yml")
    }

    private fun loadConfig(): YamlConfiguration {
        val configFile = getConfigFile()
        if (!configFile.exists()) {
            getPlugin().saveResource(getConfigFileName() + ".yml", false)
        }

        return YamlConfiguration.loadConfiguration(configFile)
    }

    fun saveConfig() {
        config.save(getConfigFile())
    }

    abstract fun getName(): String

    abstract fun getConfigFileName(): String

    open fun onEnable() {}

    open fun onDisable() {}

    open fun onReload() {
        config = loadConfig()
    }

    open fun onAutoSave() {}

    open fun requiresLateLoad(): Boolean {
        return false
    }

    open fun getCommands(): List<Class<*>> {
        return emptyList()
    }

    open fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return emptyMap()
    }

    open fun getListeners(): List<Listener> {
        return emptyList()
    }

    fun getPlugin(): PrisonAIO {
        return PrisonAIO.instance
    }

}