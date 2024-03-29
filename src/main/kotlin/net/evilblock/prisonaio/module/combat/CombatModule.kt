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
import net.evilblock.prisonaio.module.combat.bounty.BountyHandler
import net.evilblock.prisonaio.module.combat.command.ClearDamageCacheCommand
import net.evilblock.prisonaio.module.combat.damage.DamageTracker
import net.evilblock.prisonaio.module.combat.enderpearl.EnderpearlCooldownHandler
import net.evilblock.prisonaio.module.combat.enderpearl.listener.EnderpearlListeners
import net.evilblock.prisonaio.module.combat.logger.CombatLoggerHandler
import net.evilblock.prisonaio.module.combat.listener.CombatListeners
import net.evilblock.prisonaio.module.combat.logger.listener.CombatLoggerListeners
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.combat.timer.listener.CombatTimerListeners
import org.bukkit.event.Listener

object CombatModule : PluginModule() {

    private var disabledCommands: List<String> = arrayListOf()

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

        BountyHandler.initialLoad()
        CombatLoggerHandler.initialLoad()
        CombatTimerHandler.initialLoad()
        DamageTracker.initialLoad()
        GodAppleCooldownHandler.initialLoad()
        EnderpearlCooldownHandler.initialLoad()

        try {
            loadConfig()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDisable() {
        super.onDisable()

        BountyHandler.saveData()
    }

    override fun onAutoSave() {
        super.onAutoSave()

        BountyHandler.saveData()
    }

    override fun onReload() {
        super.onReload()

        try {
            loadConfig()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            ClearDamageCacheCommand.javaClass
        )
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            GodAppleListeners,
            EnderpearlListeners,
            CombatListeners,
            CombatTimerListeners,
            CombatLoggerListeners
        )
    }

    private fun loadConfig() {
        disabledCommands = config.getStringList("disabled-commands")
    }

    fun getDisabledCommands(): List<String> {
        return disabledCommands
    }

}