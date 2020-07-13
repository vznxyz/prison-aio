/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.environment

import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.environment.analytic.AnalyticHandler
import net.evilblock.prisonaio.module.environment.analytic.command.AnalyticsCommand
import net.evilblock.prisonaio.module.environment.analytic.command.WipeAnalyticsCommand
import net.evilblock.prisonaio.module.environment.analytic.listener.AnalyticListeners
import net.evilblock.prisonaio.module.environment.setting.listener.SettingListeners
import net.evilblock.prisonaio.module.environment.setting.SettingHandler
import net.evilblock.prisonaio.module.environment.setting.command.ConfigureFirstJoinMessageCommand
import net.evilblock.prisonaio.module.environment.setting.command.ConfigureFirstJoinMessageToggleCommand
import org.bukkit.event.Listener

object EnvironmentModule : PluginModule() {

    override fun getName(): String {
        return "Environment"
    }

    override fun getConfigFileName(): String {
        return "environment"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        AnalyticHandler.initialLoad()
        SettingHandler.initialLoad()
    }

    override fun onDisable() {
        AnalyticHandler.saveData()
        SettingHandler.saveData()
    }

    override fun onAutoSave() {
        AnalyticHandler.saveData()
        SettingHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            ConfigureFirstJoinMessageCommand.javaClass,
            ConfigureFirstJoinMessageToggleCommand.javaClass,
            AnalyticsCommand.javaClass,
            WipeAnalyticsCommand.javaClass
        )
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            AnalyticListeners,
            SettingListeners
        )
    }

}