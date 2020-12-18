/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warps

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.warps.command.*
import net.evilblock.prisonaio.module.warps.command.parameter.WarpParameterType
import org.bukkit.event.Listener

object WarpsModule : PluginModule() {

    override fun getName(): String {
        return "Warps"
    }

    override fun getConfigFileName(): String {
        return "warps"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        super.onEnable()

        WarpHandler.initialLoad()
    }

    override fun onDisable() {
        super.onDisable()

        WarpHandler.saveData()
    }

    override fun onAutoSave() {
        super.onAutoSave()

        WarpHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            WarpCommand.javaClass,
            WarpCreateCommand.javaClass,
            WarpDeleteCommand.javaClass,
            WarpEditorCommand.javaClass,
            WarpsCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(Warp::class.java to WarpParameterType())
    }

    override fun getListeners(): List<Listener> {
        return super.getListeners()
    }

    override fun requiresLateLoad(): Boolean {
        return true
    }

}