package net.evilblock.prisonaio.module.minigame

import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.minigame.coinflip.CoinFlipHandler
import net.evilblock.prisonaio.module.minigame.coinflip.command.CoinFlipBrowseCommand

object MinigamesModule : PluginModule() {

    override fun getName(): String {
        return "Minigames"
    }

    override fun getConfigFileName(): String {
        return "minigames"
    }

    override fun onEnable() {
        CoinFlipHandler.initialLoad()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            CoinFlipBrowseCommand.javaClass
        )
    }

}