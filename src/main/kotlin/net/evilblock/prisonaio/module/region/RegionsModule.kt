/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.region.listener.RegionListeners
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.module.region.command.*
import net.evilblock.prisonaio.module.region.command.parameter.RegionParameterType
import org.bukkit.event.Listener

object RegionsModule : PluginModule() {

    override fun getName(): String {
        return "Regions"
    }

    override fun getConfigFileName(): String {
        return "regions"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        RegionHandler.initialLoad()
    }

    override fun onDisable() {
        RegionHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            RegionCreateCommand.javaClass,
            RegionDeleteCommand.javaClass,
            RegionClaimCommand.javaClass,
            RegionBitmaskCommands.javaClass,
            RegionBypassCommand.javaClass,
            RegionDebugCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(Region::class.java to RegionParameterType())
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            RegionListeners,
            RegionBypass
        )
    }

}