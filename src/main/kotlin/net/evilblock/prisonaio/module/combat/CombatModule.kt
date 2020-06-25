/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.combat.apple.GodAppleCooldownHandler
import net.evilblock.prisonaio.module.combat.apple.listener.GodAppleListeners
import net.evilblock.prisonaio.module.combat.deathmessage.DeathMessageHandler
import net.evilblock.prisonaio.module.combat.enderpearl.EnderpearlCooldownHandler
import net.evilblock.prisonaio.module.combat.enderpearl.listener.EnderpearlListeners
import net.evilblock.prisonaio.module.combat.logger.CombatLoggerHandler
import net.evilblock.prisonaio.module.combat.region.CombatRegion
import net.evilblock.prisonaio.module.combat.region.CombatRegionHandler
import net.evilblock.prisonaio.module.combat.region.command.RegionCreateCommand
import net.evilblock.prisonaio.module.combat.region.command.RegionDeleteCommand
import net.evilblock.prisonaio.module.combat.region.command.RegionSetRegionCommand
import net.evilblock.prisonaio.module.combat.region.command.parameter.CombatRegionParameterType
import net.evilblock.prisonaio.module.combat.region.listener.CombatRegionPreventionListeners
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.combat.timer.command.BlockedCommandsAddCommand
import net.evilblock.prisonaio.module.combat.timer.command.BlockedCommandsListCommand
import net.evilblock.prisonaio.module.combat.timer.command.BlockedCommandsRemoveCommand
import net.evilblock.prisonaio.module.combat.timer.listener.CombatTimerListeners
import org.bukkit.event.Listener

object CombatModule : PluginModule() {

    override fun getName(): String {
        return "Combat"
    }

    override fun getConfigFileName(): String {
        return "combat"
    }

    override fun onEnable() {
        CombatLoggerHandler.initialLoad()
        CombatTimerHandler.initialLoad()
        CombatRegionHandler.initialLoad()
        DeathMessageHandler.initialLoad()
        GodAppleCooldownHandler.initialLoad()
        EnderpearlCooldownHandler.initialLoad()
    }

    override fun onReload() {
        super.onReload()
    }

    override fun onAutoSave() {
        CombatRegionHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            RegionCreateCommand.javaClass,
            RegionDeleteCommand.javaClass,
            RegionSetRegionCommand.javaClass,
            BlockedCommandsAddCommand.javaClass,
            BlockedCommandsListCommand.javaClass,
            BlockedCommandsRemoveCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(CombatRegion::class.java to CombatRegionParameterType())
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            GodAppleListeners,
            EnderpearlListeners,
            CombatTimerListeners,
            CombatRegionPreventionListeners
        )
    }

}