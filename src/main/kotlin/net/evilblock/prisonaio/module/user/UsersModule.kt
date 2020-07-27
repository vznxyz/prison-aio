/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.user.bank.BankNoteHandler
import net.evilblock.prisonaio.module.user.bank.command.BankNoteGiveCommand
import net.evilblock.prisonaio.module.user.command.WithdrawCommand
import net.evilblock.prisonaio.module.user.bank.listener.BankNoteAdminListeners
import net.evilblock.prisonaio.module.user.bank.listener.BankNoteDupeListeners
import net.evilblock.prisonaio.module.user.bank.listener.BankNoteListeners
import net.evilblock.prisonaio.module.user.bank.listener.BankNoteLogListeners
import net.evilblock.prisonaio.module.user.command.*
import net.evilblock.prisonaio.module.user.command.admin.*
import net.evilblock.prisonaio.module.user.command.parameter.UserParameterType
import net.evilblock.prisonaio.module.user.listener.*
import net.evilblock.prisonaio.module.user.perk.Perk
import net.evilblock.prisonaio.module.user.perk.autosell.AutoSellNotification
import net.evilblock.prisonaio.module.user.setting.listener.UserChatSettingsListeners
import net.evilblock.prisonaio.module.user.setting.listener.UserSettingsListeners
import net.evilblock.prisonaio.module.user.setting.task.UserSettingsTickTask
import net.evilblock.prisonaio.module.user.task.PlayTimeSyncTask
import org.bukkit.event.Listener

object UsersModule : PluginModule() {

    private lateinit var permissionSalesMultipliers: Map<String, Double>

    override fun getName(): String {
        return "Users"
    }

    override fun getConfigFileName(): String {
        return "users"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        permissionSalesMultipliers = readPermissionSalesMultipliers()

        UserHandler.initialLoad()
        BankNoteHandler.initialLoad()

        Tasks.asyncTimer(PlayTimeSyncTask, 20L * 30, 2L * 30)
        Tasks.asyncTimer(UserSettingsTickTask, 20L, 20L)
    }

    override fun onDisable() {
        for (user in UserHandler.getUsers()) {
            user.statistics.syncPlayTime()
        }

        UserHandler.saveData()
        BankNoteHandler.saveData()
    }

    override fun onReload() {
        super.onReload()

        permissionSalesMultipliers = readPermissionSalesMultipliers()
    }

    override fun onAutoSave() {
        UserHandler.saveData()
        BankNoteHandler.saveData()
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            AutoSellNotification,
            DropPickaxeListeners,
            TokenShopListeners,
            UserLoadListeners,
            UserPerksListeners,
            UserStatisticsListeners,
            UserSettingsListeners,
            UserChatSettingsListeners,
            BankNoteAdminListeners,
            BankNoteDupeListeners,
            BankNoteListeners,
            BankNoteLogListeners
        )
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            AutoSellCommand.javaClass,
            AutoSmeltCommand.javaClass,
            DropCommand.javaClass,
            FlyCommand.javaClass,
            PingCommand.javaClass,
            PrestigeCommand.javaClass,
            ProfileCommand.javaClass,
            RankupAllCommand.javaClass,
            RankupCommand.javaClass,
            RankupsCommand.javaClass,
            SalesMultiplierCommand.javaClass,
            SettingsCommand.javaClass,
            TokensCommand.javaClass,
            TokensHelpCommand.javaClass,
            TokensWithdrawCommand.javaClass,
            WithdrawCommand.javaClass,
            GrantPerkCommand.javaClass,
            PerkGrantsCommand.javaClass,
            TokensGiveCommand.javaClass,
            TokensResetCommand.javaClass,
            TokensSetCommand.javaClass,
            TokensTakeCommand.javaClass,
            BankNoteGiveCommand.javaClass,
            UserResetCommand.javaClass,
            UserSetPrestigeCommand.javaClass,
            UserSetRankCommand.javaClass,
            UserStatisticsCommands.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            User::class.java to UserParameterType,
            Perk::class.java to Perk.PerkParameterType
        )
    }

    fun isAutoSmeltPerkEnabledByDefault(): Boolean {
        return config.getBoolean("perks.auto-smelt.default-enabled")
    }

    fun getPermissionSalesMultipliers(): Map<String, Double> {
        return permissionSalesMultipliers
    }

    private fun readPermissionSalesMultipliers(): Map<String, Double> {
        val section = config.getConfigurationSection("perks.sales-boost.permission-multipliers")
        return section.getKeys(false).map {
            it to section.getDouble(it)
        }.sortedByDescending { it.second }.toMap()
    }

}