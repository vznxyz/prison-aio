/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.generator.build.mode.BuildModeHandler
import net.evilblock.prisonaio.module.generator.command.GeneratorCommand
import net.evilblock.prisonaio.module.generator.modifier.command.GiveModifierCommand
import net.evilblock.prisonaio.module.generator.modifier.GeneratorModifierType
import org.bukkit.event.Listener

object GeneratorsModule : PluginModule() {

    override fun getName(): String {
        return "Generators"
    }

    override fun getConfigFileName(): String {
        return "generators"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        super.onEnable()

        GeneratorHandler.initialLoad()
    }

    override fun onAutoSave() {
        GeneratorHandler.saveData()
    }

    override fun onDisable() {
        super.onDisable()

        GeneratorHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            GeneratorCommand::class.java,
            GiveModifierCommand::class.java
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            GeneratorModifierType::class.java to GeneratorModifierType.ModifierParameterType()
        )
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            BuildModeHandler
        )
    }

    override fun requiresLateLoad(): Boolean {
        return true
    }

}