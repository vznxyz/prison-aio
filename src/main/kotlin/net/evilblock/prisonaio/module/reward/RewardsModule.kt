/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward

import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.reward.deliveryman.DeliveryManHandler
import net.evilblock.prisonaio.module.reward.deliveryman.command.DeliveryManEditorCommand
import net.evilblock.prisonaio.module.reward.deliveryman.command.DeliveryManSpawnCommand
import net.evilblock.prisonaio.module.reward.event.EventRewardListeners
import net.evilblock.prisonaio.module.reward.minecrate.MineCrateHandler
import net.evilblock.prisonaio.module.reward.minecrate.listener.MineCrateListeners
import net.evilblock.prisonaio.module.reward.minecrate.task.MineCrateExpireTask
import org.bukkit.ChatColor
import org.bukkit.event.Listener

object RewardsModule : PluginModule() {

    override fun getName(): String {
        return "Rewards"
    }

    override fun getConfigFileName(): String {
        return "rewards"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        DeliveryManHandler.initialLoad()
        MineCrateHandler.initialLoad()

        getPluginFramework().server.scheduler.runTaskTimerAsynchronously(getPluginFramework(), MineCrateExpireTask, 20L, 20L)
    }

    override fun onDisable() {
        MineCrateHandler.clearSpawnedCrates()
    }

    override fun onReload() {
        super.onReload()

        MineCrateHandler.initialLoad()
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            EventRewardListeners,
            MineCrateListeners
        )
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            DeliveryManEditorCommand.javaClass,
            DeliveryManSpawnCommand.javaClass
        )
    }

    fun getChatPrefix(): String {
        return ChatColor.translateAlternateColorCodes('&', config.getString("chat-prefix"))
    }

    fun getMoneyPerBlockBreak(): Double {
        return config.getDouble("event-rewards.money-per-block-break", 0.0)
    }

    fun getTokensPerBlockBreak(): Long {
        return config.getLong("event-rewards.tokens-per-block-break", 1L)
    }

}