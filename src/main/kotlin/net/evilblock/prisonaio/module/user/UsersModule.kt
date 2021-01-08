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
import net.evilblock.cubed.scoreboard.ScoreboardHandler
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.mechanic.economy.Currency
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
import net.evilblock.prisonaio.module.user.news.NewsHandler
import net.evilblock.prisonaio.module.user.news.command.NewsCommand
import net.evilblock.prisonaio.module.user.news.command.NewsEditorCommand
import net.evilblock.prisonaio.module.user.news.listener.NewsListeners
import net.evilblock.prisonaio.module.user.perk.Perk
import net.evilblock.prisonaio.module.user.perk.autosell.AutoSellNotification
import net.evilblock.prisonaio.module.user.scoreboard.PrisonScoreGetter
import net.evilblock.prisonaio.module.user.scoreboard.PrisonTitleGetter
import net.evilblock.prisonaio.module.user.scoreboard.ScoreboardConfigHandler
import net.evilblock.prisonaio.module.user.scoreboard.animation.RainbowAnimation
import net.evilblock.prisonaio.module.user.scoreboard.animation.TitleAnimation
import net.evilblock.prisonaio.module.user.setting.listener.UserSettingsListeners
import net.evilblock.prisonaio.module.user.setting.task.UserSettingsTickTask
import net.evilblock.prisonaio.module.user.task.PlayTimeSyncTask
import org.bukkit.event.Listener
import java.util.concurrent.ConcurrentHashMap

object UsersModule : PluginModule() {

    var permissionSalesMultipliers: Map<String, Double> = ConcurrentHashMap()
    var commandAliases: Set<Pair<String, String>> = ConcurrentHashMap.newKeySet()

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
        commandAliases = readCommandAliases()

        ScoreboardConfigHandler.initialLoad()
        NewsHandler.initialLoad()
        UserHandler.initialLoad()
        BankNoteHandler.initialLoad()

        Tasks.asyncTimer(PlayTimeSyncTask, 20L * 30, 2L * 30)
        Tasks.asyncTimer(UserSettingsTickTask, 20L, 20L)

        ScoreboardHandler.configure(PrisonTitleGetter, PrisonScoreGetter)

        Tasks.asyncTimer(TitleAnimation, 1L, 1L)
        Tasks.asyncTimer(RainbowAnimation, 1L, 1L)
    }

    override fun onDisable() {
        for (user in UserHandler.getUsers()) {
            user.statistics.syncPlayTime()
        }

        ScoreboardConfigHandler.saveData()
        NewsHandler.saveData()
        UserHandler.saveData()
        BankNoteHandler.saveData()
    }

    override fun onReload() {
        super.onReload()

        permissionSalesMultipliers = readPermissionSalesMultipliers()
        commandAliases = readCommandAliases()

        ScoreboardConfigHandler.loadConfig()
    }

    override fun onAutoSave() {
        ScoreboardConfigHandler.saveData()
        UserHandler.saveData()
        BankNoteHandler.saveData()
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            AutoSellNotification,
            CommandAliasListeners,
            DropPickaxeListeners,
            TokenShopListeners,
            UserLoadListeners,
            UserPerksListeners,
            UserPrestigeListeners,
            UserStatisticsListeners,
            UserSettingsListeners,
            BankNoteAdminListeners,
            BankNoteDupeListeners,
            BankNoteListeners,
            BankNoteLogListeners,
            NewsListeners
        )
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            AutoSellCommand.javaClass,
            AutoSmeltCommand.javaClass,
            BalanceCommand.javaClass,
            BalanceTopCommand.javaClass,
            DropCommand.javaClass,
            FlyCommand.javaClass,
            MainMenuCommand.javaClass,
            NicknameCommand.javaClass,
            PingCommand.javaClass,
            PlayTimeCommand.javaClass,
            PrestigeCommand.javaClass,
            PrestigeTokensCommand.javaClass,
            ProfileCommand.javaClass,
            RankupAllCommand.javaClass,
            RankupCommand.javaClass,
            RankupsCommand.javaClass,
            ShopMultiplierCommand.javaClass,
            SettingsCommand.javaClass,
            SpawnCommand.javaClass,
            ToggleEnchantMessagesCommand.javaClass,
            TokensCommand.javaClass,
            TokensHelpCommand.javaClass,
            TokensWithdrawCommand.javaClass,
            WithdrawCommand.javaClass,
            EconomyGiveCommand.javaClass,
            EconomyResetAllCommand.javaClass,
            EconomyResetCommand.javaClass,
            EconomySetCommand.javaClass,
            EconomyTakeCommand.javaClass,
            NicknameGrantCommand.javaClass,
            PerksGrantCommand.javaClass,
            PerksGrantsCommand.javaClass,
            PrestigeTokensGiveCommand.javaClass,
            PrestigeTokensResetCommand.javaClass,
            PrestigeTokensSetCommand.javaClass,
            PrestigeTokensTakeCommand.javaClass,
            TokensGiveCommand.javaClass,
            TokensResetCommand.javaClass,
            TokensSetCommand.javaClass,
            TokensTakeCommand.javaClass,
            BankNoteGiveCommand.javaClass,
            UserResetCommand.javaClass,
            UserSetPrestigeCommand.javaClass,
            UserSetRankCommand.javaClass,
            UserStatisticsCommands.javaClass,
            NewsCommand.javaClass,
            NewsEditorCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            User::class.java to UserParameterType,
            Perk::class.java to Perk.PerkParameterType
        )
    }

    fun isAutoSellPerkEnabledByDefault(): Boolean {
        return config.getBoolean("perks.auto-sell.default-enabled", false)
    }

    fun isAutoSmeltPerkEnabledByDefault(): Boolean {
        return config.getBoolean("perks.auto-smelt.default-enabled", true)
    }

    fun getBankNoteRedeemAlertThreshold(currency: Currency): Long {
        return config.getLong("bank-note.redeem-alert-threshold.${currency.toType().name}")
    }

    private fun readPermissionSalesMultipliers(): Map<String, Double> {
        val section = config.getConfigurationSection("perks.sales-boost.permission-multipliers")
        return section.getKeys(false).map {
            it to section.getDouble(it)
        }.sortedByDescending { it.second }.toMap()
    }

    private fun readCommandAliases(): Set<Pair<String, String>> {
        return if (config.contains("command-aliases")) {
            hashSetOf<Pair<String, String>>().also { set ->
                for (command in config.getConfigurationSection("command-aliases").getKeys(false)) {
                    set.add(Pair(command, config.getString("command-aliases.$command")))
                }
            }
        } else {
            emptySet()
        }
    }

}