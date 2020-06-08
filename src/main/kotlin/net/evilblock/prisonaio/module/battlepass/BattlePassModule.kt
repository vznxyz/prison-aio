package net.evilblock.prisonaio.module.battlepass

import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.battlepass.challenge.ChallengeHandler
import net.evilblock.prisonaio.module.battlepass.tier.TierHandler
import net.evilblock.prisonaio.module.battlepass.challenge.listener.ChallengeCompletionListeners
import net.evilblock.prisonaio.module.battlepass.command.BattlePassCommand
import net.evilblock.prisonaio.module.battlepass.command.BattlePassEditorCommand
import net.evilblock.prisonaio.module.battlepass.command.BattlePassSetPremiumCommand
import org.bukkit.event.Listener

object BattlePassModule : PluginModule() {

    override fun getName(): String {
        return "BattlePass"
    }

    override fun getConfigFileName(): String {
        return "battle-pass"
    }

    override fun onEnable() {
        TierHandler.initialLoad()
        ChallengeHandler.initialLoad()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            BattlePassCommand.javaClass,
            BattlePassEditorCommand.javaClass,
            BattlePassSetPremiumCommand.javaClass
        )
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            ChallengeCompletionListeners
        )
    }

}