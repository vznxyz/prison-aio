/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat

import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.combat.apple.GodAppleCooldownHandler
import net.evilblock.prisonaio.module.combat.apple.listener.GodAppleListeners
import net.evilblock.prisonaio.module.combat.deathmessage.DeathMessageHandler
import net.evilblock.prisonaio.module.combat.enderpearl.EnderpearlCooldownHandler
import net.evilblock.prisonaio.module.combat.enderpearl.listener.EnderpearlListeners
import net.evilblock.prisonaio.module.combat.logger.CombatLoggerHandler
import net.evilblock.prisonaio.module.combat.listener.CombatListeners
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

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        super.onEnable()

        CombatLoggerHandler.initialLoad()
        CombatTimerHandler.initialLoad()
        DeathMessageHandler.initialLoad()
        GodAppleCooldownHandler.initialLoad()
        EnderpearlCooldownHandler.initialLoad()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            BlockedCommandsAddCommand.javaClass,
            BlockedCommandsListCommand.javaClass,
            BlockedCommandsRemoveCommand.javaClass
        )
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            GodAppleListeners,
            EnderpearlListeners,
            CombatTimerListeners,
            CombatListeners
        )
    }

}