/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass

import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.battlepass.challenge.ChallengeHandler
import net.evilblock.prisonaio.module.battlepass.tier.TierHandler
import net.evilblock.prisonaio.module.battlepass.challenge.listener.ChallengeListeners
import net.evilblock.prisonaio.module.battlepass.daily.DailyChallengeHandler
import net.evilblock.prisonaio.module.battlepass.command.*
import org.bukkit.ChatColor
import org.bukkit.event.Listener

object BattlePassModule : PluginModule() {

    val CHAT_PREFIX = "${ChatColor.GRAY}[${ChatColor.GOLD}${ChatColor.BOLD}JunkiePass${ChatColor.GRAY}] "

    override fun getName(): String {
        return "BattlePass"
    }

    override fun getConfigFileName(): String {
        return "battle-pass"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        TierHandler.initialLoad()
        ChallengeHandler.initialLoad()
        DailyChallengeHandler.initialLoad()
    }

    override fun onAutoSave() {
        TierHandler.saveData()
        ChallengeHandler.saveData()
        DailyChallengeHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            BattlePassCommand.javaClass,
            BattlePassEditorCommand.javaClass,
            BattlePassResetCommand.javaClass,
            BattlePassWipeCommand.javaClass,
            BattlePassSetPremiumCommand.javaClass,
            BattlePassSetXPCommand.javaClass
        )
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            ChallengeListeners
        )
    }

}