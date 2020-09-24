/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.system.analytic.AnalyticHandler
import net.evilblock.prisonaio.module.system.analytic.command.AnalyticsCommand
import net.evilblock.prisonaio.module.system.analytic.command.WipeAnalyticsCommand
import net.evilblock.prisonaio.module.system.analytic.listener.AnalyticListeners
import net.evilblock.prisonaio.module.system.command.*
import net.evilblock.prisonaio.module.system.sentient.SentientHandler
import net.evilblock.prisonaio.module.system.sentient.guard.command.SpawnPrisonGuardCommand
import net.evilblock.prisonaio.module.system.setting.listener.SettingListeners
import net.evilblock.prisonaio.module.system.setting.SettingHandler
import net.evilblock.prisonaio.module.system.setting.command.ConfigureFirstJoinMessageCommand
import net.evilblock.prisonaio.module.system.setting.command.ConfigureFirstJoinMessageToggleCommand
import net.evilblock.prisonaio.module.system.wizard.command.RunSetupCheckCommand
import org.bukkit.event.Listener

object SystemModule : PluginModule() {

    override fun getName(): String {
        return "System"
    }

    override fun getConfigFileName(): String {
        return "system"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        AnalyticHandler.initialLoad()
        SettingHandler.initialLoad()
        SentientHandler.initialLoad()
    }

    override fun onReload() {
        super.onReload()
    }

    override fun onDisable() {
        AnalyticHandler.saveData()
        SettingHandler.saveData()
        SentientHandler.saveData()
    }

    override fun onAutoSave() {
        AnalyticHandler.saveData()
        SettingHandler.saveData()
        SentientHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            ConfigureFirstJoinMessageCommand.javaClass,
            ConfigureFirstJoinMessageToggleCommand.javaClass,
            AnalyticsCommand.javaClass,
            WipeAnalyticsCommand.javaClass,
            SpawnPrisonGuardCommand.javaClass,
            RunSetupCheckCommand::class.java,
            ManageCommand::class.java,
            HealthCommand::class.java,
            HotFixCommands::class.java,
            GKitzCommand::class.java,
            ReloadCommand::class.java,
            SaveCommand::class.java
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf()
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            AnalyticListeners,
            SettingListeners
        )
    }

}