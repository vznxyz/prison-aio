/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.admin

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.admin.analytic.AnalyticHandler
import net.evilblock.prisonaio.module.admin.analytic.command.AnalyticsCommand
import net.evilblock.prisonaio.module.admin.analytic.command.WipeAnalyticsCommand
import net.evilblock.prisonaio.module.admin.analytic.listener.AnalyticListeners
import net.evilblock.prisonaio.module.admin.command.*
import net.evilblock.prisonaio.module.admin.setting.listener.SettingListeners
import net.evilblock.prisonaio.module.admin.setting.SettingHandler
import net.evilblock.prisonaio.module.admin.setting.command.ConfigureFirstJoinMessageCommand
import net.evilblock.prisonaio.module.admin.setting.command.ConfigureFirstJoinMessageToggleCommand
import net.evilblock.prisonaio.module.admin.wizard.command.RunSetupCheckCommand
import org.bukkit.Bukkit
import org.bukkit.event.Listener

object AdminModule : PluginModule() {

    override fun getName(): String {
        return "Admin"
    }

    override fun getConfigFileName(): String {
        return "admin"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        AnalyticHandler.initialLoad()
        SettingHandler.initialLoad()
    }

    override fun onReload() {
        super.onReload()
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
            WipeAnalyticsCommand.javaClass,
            RunSetupCheckCommand::class.java,
            ManageCommand::class.java,
            HotFixCommands::class.java,
            GKitzCommand::class.java,
            ReloadCommand::class.java,
            SaveCommand::class.java,
            WipeDatabaseCommand::class.java
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

    fun getTotalMemory(): Pair<Long, String> {
        return convertMemory(Runtime.getRuntime().totalMemory() / 1024)
    }

    fun getUsedMemory(): Pair<Long, String> {
        return convertMemory((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024)
    }

    private fun convertMemory(initial: Long): Pair<Long, String> {
        var memory = initial
        var memoryUnit = 0

        while (memory > 999) {
            memory /= 1024
            memoryUnit++
        }

        return Pair(memory, getMemoryUnitName(memoryUnit))
    }

    private fun getMemoryUnitName(unit: Int): String {
        return when (unit) {
            0 -> "KB"
            1 -> "MB"
            2 -> "GB"
            else -> "UNKNOWN UNIT"
        }
    }

    fun countEntities(): Int {
        var entities = 0
        for (world in Bukkit.getWorlds()) {
            entities += world.entityCount
        }
        return entities
    }

}